package com.sellspark.SellsHRMS.monitoring.service;

import com.sellspark.SellsHRMS.monitoring.dto.*;

import java.util.List;

public interface MonitorGroupService {

    // ==================== Group CRUD ====================
    GroupDTO createGroup(CreateGroupRequest request, Long organisationId, Long createdBy);

    GroupDTO updateGroup(String id, UpdateGroupRequest request, Long organisationId, Long userId);

    void deleteGroup(String id, Long organisationId, Long userId);

    List<UrlListItemDTO> getAvailableUrlsForGroup(String groupId, Long organisationId, Long userId, String search);

    // ==================== Group Queries ====================
    List<GroupListDTO> getGroups(Long organisationId, Long userId);

    GroupDetailDTO getGroupDetail(String id, Long organisationId, Long userId);

    // ==================== Group URL Management ====================
    void addUrlToGroup(String groupId, String urlId, Long organisationId, Long userId);

    void removeUrlFromGroup(String groupId, String urlId, Long organisationId, Long userId);

    // ==================== Group Member Management ====================
    void addMemberToGroup(String groupId, Long memberId, Long organisationId, Long addedBy);

    void removeMemberFromGroup(String groupId, Long memberId, Long organisationId, Long userId);
}