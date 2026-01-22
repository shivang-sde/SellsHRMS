package com.sellspark.SellsHRMS.dto.leave;

import lombok.Data;

@Data
public class LeaveApprovalRequest {
    private Long leaveId;
    private Long approvedById;
    private String status;
    private String remarks;

}
