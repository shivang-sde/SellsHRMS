package com.sellspark.SellsHRMS.notification.service.impl;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.organisation.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.notification.dto.EmailConfigDTO;
import com.sellspark.SellsHRMS.notification.entity.OrgEmailConfig;
import com.sellspark.SellsHRMS.notification.exception.InvalidSMTPConfigException;
import com.sellspark.SellsHRMS.notification.mapper.OrgEmailConfigMapper;
import com.sellspark.SellsHRMS.notification.repository.OrganisationEmailConfigRepository;
import com.sellspark.SellsHRMS.notification.service.EmailConfigService;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // All public methods now use these settings
public class EmailConfigServiceImpl implements EmailConfigService {
        private final OrganisationEmailConfigRepository configRepo;
        private final OrganisationRepository orgRepo;
        private final OrgEmailConfigMapper emailConfigMapper;

        @Override
        public EmailConfigDTO saveConfig(EmailConfigDTO dto) {

                log.info("Saving SMTP configuration for org: {},{},{},{},{}", dto.getOrgId(), dto.getSmtpHost(),
                                dto.getSmtpPort(),
                                dto.getSmtpUsername());

                Organisation org = orgRepo.findById(dto.getOrgId())
                                .orElseThrow(() -> new OrganisationNotFoundException(dto.getOrgId()));

                OrgEmailConfig config = OrgEmailConfig.builder()
                                .organisation(org)
                                .smtpHost(dto.getSmtpHost())
                                .smtpPort(dto.getSmtpPort())
                                .smtpUsername(dto.getSmtpUsername())
                                .smtpPassword(dto.getSmtpPassword()) // will be encrypted by JPA converter
                                .useTls(dto.getUseTls())
                                .useSsl(dto.getUseSsl())
                                .fromEmail(dto.getFromEmail())
                                .fromName(dto.getFromName())
                                .dailyLimit(dto.getDailyLimit())
                                .hourlyLimit(dto.getHourlyLimit())
                                .build();
                return emailConfigMapper.toDto(configRepo.save(config));
        }

        @Override
        public EmailConfigDTO updateConfig(EmailConfigDTO dto) {

                OrgEmailConfig config = configRepo.findById(dto.getOrgId())
                                .orElseThrow(() -> new ResourceNotFoundException("Email configuration not found"));

                config.setSmtpHost(dto.getSmtpHost());
                config.setSmtpPort(dto.getSmtpPort());
                config.setSmtpUsername(dto.getSmtpUsername());
                config.setSmtpPassword(dto.getSmtpPassword());
                config.setUseTls(dto.getUseTls());
                config.setUseSsl(dto.getUseSsl());
                config.setFromEmail(dto.getFromEmail());
                config.setFromName(dto.getFromName());
                config.setDailyLimit(dto.getDailyLimit());
                config.setHourlyLimit(dto.getHourlyLimit());

                return emailConfigMapper.toDto(configRepo.save(config));
        }

        @Override
        public EmailConfigDTO getActiveConfig(Long orgId) {
                OrgEmailConfig config = configRepo.findByOrgIdAndIsActiveTrue(orgId)
                                .orElseThrow(() -> new InvalidSMTPConfigException(
                                                "No active email configuration found for organization: " + orgId));
                return emailConfigMapper.toDto(config);
        }

        /**
         * Test SMTP credentials without saving
         */

        @Override
        public boolean testSmtpConnection(EmailConfigDTO dto) {

                log.info("Testing SMTP connection for org: {},{},{},{}", dto.getOrgId(), dto.getSmtpHost(),
                                dto.getSmtpPort(),
                                dto.getSmtpUsername());
                JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                mailSender.setHost(dto.getSmtpHost());
                mailSender.setPort(dto.getSmtpPort());
                mailSender.setUsername(dto.getSmtpUsername());
                mailSender.setPassword(dto.getSmtpPassword());

                Properties props = mailSender.getJavaMailProperties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.auth", "true");

                // only use tls, ssl deprecated
                if (Boolean.TRUE.equals(dto.getUseTls())) {
                        props.put("mail.smtp.starttls.enable", true);
                }
                // Short timeouts for testing
                props.put("mail.smtp.connectiontimeout", "3000");
                props.put("mail.smtp.timeout", "3000");
                props.put("mail.debug", "true");

                try {
                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                        helper.setFrom(dto.getFromEmail());
                        helper.setTo("shivang.sellpark@gmail.com");
                        helper.setSubject("SMTP Connection Test - Sellspark HRMS");
                        helper.setText("This is a test email to verify SMTP configuration.");
                        mailSender.send(mimeMessage);
                        log.info("SMTP test successful for host: {} org: {}",
                                        dto.getSmtpHost(), dto.getOrgId());
                        return true;
                } catch (Exception e) {
                        log.error("SMTP test failed for org: {} - {}", dto.getOrgId(), e.getMessage());
                        throw new InvalidSMTPConfigException(
                                        "Failed to connect to SMTP server: " + e.getCause().getMessage() + " Cause2 "
                                                        + e.getMessage());
                }
        }

}
