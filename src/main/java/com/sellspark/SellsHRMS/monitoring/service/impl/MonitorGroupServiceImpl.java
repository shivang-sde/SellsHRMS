package com.sellspark.SellsHRMS.monitoring.service.impl;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.monitoring.dto.*;
import com.sellspark.SellsHRMS.monitoring.entity.*;
import com.sellspark.SellsHRMS.monitoring.repository.*;
import com.sellspark.SellsHRMS.monitoring.service.MonitorGroupService;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MonitorGroupServiceImpl implements MonitorGroupService {

    private final MonitorGroupRepository groupRepository;
    private final MonitorGroupUrlRepository groupUrlRepository;
    private final MonitorGroupMemberRepository groupMemberRepository;
    private final MonitorUrlRepository urlRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    // ==================== Group CRUD ====================

    @Override
    public GroupDTO createGroup(CreateGroupRequest request, Long organisationId, Long createdBy) {
        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation not found: " + organisationId));
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> new RuntimeException("User not found: " + createdBy));

        MonitorGroup group = MonitorGroup.builder()
                .id(UUID.randomUUID().toString())
                .organisation(organisation)
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MonitorGroup saved = groupRepository.save(group);
        log.info("Group created: {} by {}", saved.getName(), creator.getEmail());
        return convertToGroupDTO(saved);
    }

    @Override
    public GroupDTO updateGroup(String id, UpdateGroupRequest request, Long organisationId, Long userId) {
        MonitorGroup group = getGroupEntity(id, organisationId);

        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        group.setUpdatedAt(LocalDateTime.now());

        MonitorGroup updated = groupRepository.save(group);
        log.info("Group updated: {} by user {}", updated.getName(), userId);
        return convertToGroupDTO(updated);
    }

    @Override
    public void deleteGroup(String id, Long organisationId, Long userId) {
        MonitorGroup group = getGroupEntity(id, organisationId);
        groupRepository.delete(group);
        log.info("Group deleted: {} by user {}", group.getName(), userId);
    }

    // ==================== Group Queries ====================

    @Override
    public List<GroupListDTO> getGroups(Long organisationId, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<MonitorGroup> groups;
        if (currentUser.getSystemRole() == User.SystemRole.SUPER_ADMIN) {
            groups = groupRepository.findAll();
        } else {
            groups = groupRepository.findByOrganisationId(organisationId);
        }

        return groups.stream()
                .map(this::convertToGroupListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GroupDetailDTO getGroupDetail(String id, Long organisationId, Long userId) {
        MonitorGroup group = getGroupEntity(id, organisationId);
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Validate access for non-super-admin
        if (currentUser.getSystemRole() != User.SystemRole.SUPER_ADMIN) {
            if (!group.getOrganisation().getId().equals(organisationId)) {
                throw new RuntimeException("Access denied: Group belongs to different organisation");
            }
        }

        // Get URLs in this group
        List<MonitorUrl> urls = groupUrlRepository.findUrlsByGroupId(id);

        log.info("Fetching URLs for Group ID: {}", id);
        // Get members in this group with user details
        List<MonitorGroupMember> members = groupMemberRepository.findByGroupIdWithUser(id);
        log.info("Found {} URLs", urls.size());
        // Get available users (all users in organisation except current members)
        List<UserBasicDTO> availableUsers = getAvailableUsers(group, organisationId, currentUser);

        return GroupDetailDTO.builder()
                .group(convertToGroupDTO(group))
                .urls(urls.stream()
                        .map(this::convertToUrlListItemDTO)
                        .collect(Collectors.toList()))
                .members(members.stream()
                        .map(this::convertToGroupMemberDTO)
                        .collect(Collectors.toList()))
                .availableUsers(availableUsers)
                .build();
    }

    // Implementation
    @Override
    public List<UrlListItemDTO> getAvailableUrlsForGroup(String groupId, Long organisationId, Long userId,
            String search) {
        // 1. Get IDs of URLs already in this group
        List<String> existingUrlIds = groupUrlRepository.findUrlIdsByGroupId(groupId);

        // 2. Fetch all URLs for the organisation (excluding those in the group)
        List<MonitorUrl> allUrls;

        if (search != null && !search.isEmpty()) {
            // If searching, filter by name/url and exclude existing
            allUrls = urlRepository.findByOrganisationIdAndNameContainingIgnoreCaseAndIdNotIn(
                    organisationId, search, existingUrlIds);
        } else {
            // If no search, just exclude existing (limit to recent 50 to avoid performance
            // hit if list is huge)
            allUrls = urlRepository.findByOrganisationIdAndIdNotIn(
                    organisationId, existingUrlIds);
        }

        return allUrls.stream()
                .map(this::convertToUrlListItemDTO)
                .collect(Collectors.toList());
    }

    // ==================== Group URL Management ====================

    @Override
    public void addUrlToGroup(String groupId, String urlId, Long organisationId, Long userId) {
        MonitorGroup group = getGroupEntity(groupId, organisationId);
        MonitorUrl url = urlRepository.findById(urlId)
                .orElseThrow(() -> new RuntimeException("URL not found: " + urlId));

        // Validate URL belongs to same organisation
        if (!url.getOrganisation().getId().equals(organisationId)) {
            throw new RuntimeException("Access denied: URL belongs to different organisation");
        }

        // Check if already exists
        if (groupUrlRepository.existsByGroupIdAndUrlId(groupId, urlId)) {
            throw new RuntimeException("URL already in group");
        }

        MonitorGroupUrl groupUrl = MonitorGroupUrl.builder()
                .id(UUID.randomUUID().toString())
                .group(group)
                .url(url)
                .createdAt(LocalDateTime.now())
                .build();

        groupUrlRepository.save(groupUrl);
        log.info("URL {} added to group {} by user {}", url.getName(), group.getName(), userId);
    }

    @Override
    public void removeUrlFromGroup(String groupId, String urlId, Long organisationId, Long userId) {
        // Verify group exists and belongs to organisation
        getGroupEntity(groupId, organisationId);

        if (!groupUrlRepository.existsByGroupIdAndUrlId(groupId, urlId)) {
            throw new RuntimeException("URL not in group");
        }

        groupUrlRepository.deleteByGroupIdAndUrlId(groupId, urlId);
        log.info("URL {} removed from group {} by user {}", urlId, groupId, userId);
    }

    // ==================== Group Member Management ====================

    @Override
    public void addMemberToGroup(String groupId, Long memberId, Long organisationId, Long addedBy) {
        MonitorGroup group = getGroupEntity(groupId, organisationId);
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found: " + memberId));
        User adder = userRepository.findById(addedBy)
                .orElseThrow(() -> new RuntimeException("User not found: " + addedBy));

        // Check if user belongs to same organisation (for non-super-admin)
        if (adder.getSystemRole() != User.SystemRole.SUPER_ADMIN) {
            if (!user.getOrganisation().getId().equals(organisationId)) {
                throw new RuntimeException("User does not belong to current organisation");
            }
        }

        // Check if already member
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, memberId)) {
            throw new RuntimeException("User already in group");
        }

        MonitorGroupMember member = MonitorGroupMember.builder()
                .id(UUID.randomUUID().toString())
                .group(group)
                .user(user)
                .addedBy(adder)
                .createdAt(LocalDateTime.now())
                .build();

        groupMemberRepository.save(member);
        log.info("User {} added to group {} by {}", user.getEmail(), group.getName(), adder.getEmail());
    }

    @Override
    public void removeMemberFromGroup(String groupId, Long memberId, Long organisationId, Long userId) {
        // Verify group exists and belongs to organisation
        getGroupEntity(groupId, organisationId);

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, memberId)) {
            throw new RuntimeException("User not in group");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, memberId);
        log.info("User {} removed from group {} by user {}", memberId, groupId, userId);
    }

    // ==================== Private Helper Methods ====================

    private MonitorGroup getGroupEntity(String id, Long organisationId) {
        MonitorGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found: " + id));

        if (!group.getOrganisation().getId().equals(organisationId)) {
            throw new RuntimeException("Access denied: Group belongs to different organisation");
        }
        return group;
    }

    private List<UserBasicDTO> getAvailableUsers(MonitorGroup group, Long organisationId, User currentUser) {
        List<Long> existingMemberIds = groupMemberRepository.findUserIdsByGroupId(group.getId());

        List<User> allUsers;
        if (currentUser.getSystemRole() == User.SystemRole.SUPER_ADMIN) {
            allUsers = userRepository.findAll();
        } else {
            allUsers = userRepository.findByOrganisationId(organisationId);
        }

        return allUsers.stream()
                .filter(u -> !existingMemberIds.contains(u.getId()))
                .map(this::convertToUserBasicDTO)
                .collect(Collectors.toList());
    }

    // ==================== Converter Methods ====================

    private GroupDTO convertToGroupDTO(MonitorGroup group) {
        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .createdByName(group.getCreatedBy() != null ? group.getCreatedBy().getEmail() : null)
                .build();
    }

    private GroupListDTO convertToGroupListDTO(MonitorGroup group) {

        log.info("count url : " + groupUrlRepository.countByGroupId(group.getId()));
        log.info("count member : " + groupMemberRepository.countByGroupId(group.getId()));

        return GroupListDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .urlCount(groupUrlRepository.countByGroupId(group.getId()))
                .memberCount(groupMemberRepository.countByGroupId(group.getId()))
                .createdAt(group.getCreatedAt())
                .build();
    }

    private UrlListItemDTO convertToUrlListItemDTO(MonitorUrl url) {
        return UrlListItemDTO.builder()
                .id(url.getId())
                .name(url.getName())
                .url(url.getUrl())
                .currentStatus(url.getCurrentStatus().name())
                .uptimePercentage(url.getUptimePercentage().doubleValue())
                .lastResponseTime(url.getLastResponseTime())
                .lastCheckedAt(url.getLastCheckedAt())
                .isActive(url.getIsActive())
                .checkInterval(url.getCheckInterval())
                .build();
    }

    private GroupMemberDTO convertToGroupMemberDTO(MonitorGroupMember member) {
        User user = member.getUser();
        User addedBy = member.getAddedBy();

        return GroupMemberDTO.builder()
                .userId(user.getId())
                .firstName(user.getEmployee() != null ? user.getEmployee().getFirstName() : "")
                .lastName(user.getEmployee() != null ? user.getEmployee().getLastName() : "")
                .email(user.getEmail())
                .role(user.getSystemRole() != null ? user.getSystemRole().name() : "")
                .addedAt(member.getCreatedAt())
                .addedByName(addedBy != null ? addedBy.getEmail() : null)
                .build();
    }

    private UserBasicDTO convertToUserBasicDTO(User user) {
        return UserBasicDTO.builder()
                .id(user.getId())
                .firstName(user.getEmployee() != null ? user.getEmployee().getFirstName() : "")
                .lastName(user.getEmployee() != null ? user.getEmployee().getLastName() : "")
                .email(user.getEmail())
                .employeeCode(user.getEmployee() != null ? user.getEmployee().getEmployeeCode() : null)
                .build();
    }
}