package com.sellspark.SellsHRMS.dto.project;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskLabelDTO {
    private Long id;
    private String name;
}
