
package com.sellspark.SellsHRMS.notification.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.notification.dto.NotificationTemplatePreviewRequestDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateRequestDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateResponseDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationTemplate;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import com.sellspark.SellsHRMS.notification.repository.NotificationTemplateRepository;
import com.sellspark.SellsHRMS.notification.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository repository;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\[\\[\\$\\{([a-zA-Z0-9_]+)\\}\\]\\]");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> COMMON_VARIABLES = Arrays.asList(
            "employeeName",
            "recipientName",
            "department",
            "dateOfJoining",
            "startDate",
            "endDate",
            "leaveDays",
            "reason",
            "approver",
            "remarks");

    public NotificationTemplate getTemplate(String eventCode, TargetRole role) {
        return repository
                .findByEventCodeAndTargetRoleAndIsActiveTrue(eventCode, role)
                .orElseThrow(() -> new RuntimeException(
                        "Template not found for " + eventCode + " - " + role));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> getAllTemplates() {
        List<NotificationTemplate> templates = repository.findAllByOrderByIdDesc();
        List<NotificationTemplateResponseDTO> result = new ArrayList<>();
        for (NotificationTemplate template : templates) {
            result.add(toResponse(template));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponseDTO getTemplateById(Long id) {
        NotificationTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found for id: " + id));
        return toResponse(template);
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDTO createTemplate(NotificationTemplateRequestDTO request) {
        String normalizedEventCode = normalizeEventCode(request.getEventCode());
        if (repository.existsByEventCodeAndTargetRole(normalizedEventCode, request.getTargetRole())) {
            throw new RuntimeException("Template already exists for event " + normalizedEventCode + " and role "
                    + request.getTargetRole());
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .eventCode(normalizedEventCode)
                .targetRole(request.getTargetRole())
                .subject(request.getSubject().trim())
                .body(request.getBody().trim())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();

        return toResponse(repository.save(template));
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDTO updateTemplate(Long id, NotificationTemplateRequestDTO request) {
        NotificationTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found for id: " + id));

        String normalizedEventCode = normalizeEventCode(request.getEventCode());
        repository.findByEventCodeAndTargetRole(normalizedEventCode, request.getTargetRole())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Template already exists for event " + normalizedEventCode
                            + " and role " + request.getTargetRole());
                });

        template.setEventCode(normalizedEventCode);
        template.setTargetRole(request.getTargetRole());
        template.setSubject(request.getSubject().trim());
        template.setBody(request.getBody().trim());
        template.setIsActive(request.getIsActive() == null ? template.getIsActive() : request.getIsActive());

        return toResponse(repository.save(template));
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDTO toggleTemplateStatus(Long id) {
        NotificationTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found for id: " + id));
        template.setIsActive(Boolean.FALSE.equals(template.getIsActive()));
        return toResponse(repository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponseDTO previewTemplate(Long id, NotificationTemplatePreviewRequestDTO request) {
        NotificationTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found for id: " + id));

        Map<String, String> sampleData = defaultSampleData();
        if (request != null && request.getSampleData() != null && !request.getSampleData().isEmpty()) {
            sampleData.putAll(request.getSampleData());
        }

        String renderedSubject = renderTemplate(template.getSubject(), sampleData);
        String renderedBody = renderTemplate(template.getBody(), sampleData);

        NotificationTemplateResponseDTO response = toResponse(template);
        response.setSubject(renderedSubject);
        response.setBody(renderedBody);
        return response;
    }

    @Override
    @Transactional
    public List<NotificationTemplateResponseDTO> seedDefaultTemplates() {
        List<TemplateSeed> seeds = List.of(
                new TemplateSeed("LEAVE_APPLIED", TargetRole.MANAGER, "Leave request from [[${employeeName}]]",
                        "<p>Hello [[${approver}]],</p><p>[[${employeeName}]] has requested [[${leaveDays}]] day(s) leave from [[${startDate}]] to [[${endDate}]].</p><p>Reason: [[${reason}]]</p>"),
                new TemplateSeed("LEAVE_APPROVED", TargetRole.EMPLOYEE, "Your leave is approved",
                        "<p>Hello [[${employeeName}]],</p><p>Your leave from [[${startDate}]] to [[${endDate}]] has been approved by [[${approver}]].</p><p>Remarks: [[${remarks}]]</p>"),
                new TemplateSeed("ONBOARDING_WELCOME", TargetRole.EMPLOYEE, "Welcome to the team, [[${employeeName}]]",
                        "<p>Hi [[${employeeName}]],</p><p>Welcome to [[${department}]]. Your joining date is [[${dateOfJoining}]].</p>"));

        List<NotificationTemplateResponseDTO> created = new ArrayList<>();
        for (TemplateSeed seed : seeds) {
            if (!repository.existsByEventCodeAndTargetRole(seed.eventCode(), seed.targetRole())) {
                NotificationTemplate template = NotificationTemplate.builder()
                        .eventCode(seed.eventCode())
                        .targetRole(seed.targetRole())
                        .subject(seed.subject())
                        .body(seed.body())
                        .isActive(Boolean.TRUE)
                        .build();
                created.add(toResponse(repository.save(template)));
            }
        }
        return created;
    }

    private NotificationTemplateResponseDTO toResponse(NotificationTemplate template) {
        List<String> variables = extractVariables(template.getSubject() + " " + template.getBody());
        return NotificationTemplateResponseDTO.builder()
                .id(template.getId())
                .eventCode(template.getEventCode())
                .targetRole(template.getTargetRole())
                .subject(template.getSubject())
                .body(template.getBody())
                .isActive(template.getIsActive())
                .updatedTime(TIME_FORMATTER.format(LocalDateTime.now()))
                .variables(variables)
                .build();
    }

    private String normalizeEventCode(String eventCode) {
        return eventCode == null ? null : eventCode.trim().toUpperCase();
    }

    private List<String> extractVariables(String content) {
        Set<String> variables = new LinkedHashSet<>();
        if (content == null || content.isBlank()) {
            return new ArrayList<>();
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return new ArrayList<>(variables);
    }

    private String renderTemplate(String content, Map<String, String> values) {
        if (content == null) {
            return "";
        }
        String rendered = content;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String token = "[[${" + entry.getKey() + "}]]";
            rendered = rendered.replace(token, entry.getValue());
        }
        return rendered;
    }

    private Map<String, String> defaultSampleData() {
        Map<String, String> sampleData = new LinkedHashMap<>();
        for (String variable : COMMON_VARIABLES) {
            sampleData.put(variable, "[" + variable + "]");
        }
        return sampleData;
    }

    private record TemplateSeed(String eventCode, TargetRole targetRole, String subject, String body) {
    }
}
