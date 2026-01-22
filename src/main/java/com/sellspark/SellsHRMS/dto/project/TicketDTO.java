package com.sellspark.SellsHRMS.dto.project;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualCompletionDate;
    private LocalDate actualStartDate; // When employee actually starts work
    private LocalDate assignedAt; 
    private List<Long> assigneeIds;
    private List<String> assigneeNames;
    private List<TaskDTO> tasks;
    private List<TicketAttachmentDTO> attachments;

    private Long createdById;
    private String createdByName;
    
}
