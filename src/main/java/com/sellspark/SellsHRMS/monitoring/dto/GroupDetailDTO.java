package com.sellspark.SellsHRMS.monitoring.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailDTO {
    private GroupDTO group;
    private List<UrlListItemDTO> urls;
    private List<GroupMemberDTO> members;
    private List<UserBasicDTO> availableUsers;
}