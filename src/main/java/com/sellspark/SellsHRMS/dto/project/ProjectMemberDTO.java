package com.sellspark.SellsHRMS.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMemberDTO {
    private Long id;

    private Long projectId;
    private String projectName;

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String departmentName;

    private String role; // e.g. PROJECT_MANAGER, DEVELOPER, DESIGNER
    private BigDecimal allocationPercentage;

    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
