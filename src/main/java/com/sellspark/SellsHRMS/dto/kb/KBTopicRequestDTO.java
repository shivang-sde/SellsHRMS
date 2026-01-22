package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;



@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KBTopicRequestDTO {
    private Long subjectId;
    private String title;
    private String content;
    private String attachmentUrl;
}