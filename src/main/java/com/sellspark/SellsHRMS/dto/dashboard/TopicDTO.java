package com.sellspark.SellsHRMS.dto.dashboard;


import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDTO {
    private Long id;
    private String title;
    private String attachmentUrl;
}