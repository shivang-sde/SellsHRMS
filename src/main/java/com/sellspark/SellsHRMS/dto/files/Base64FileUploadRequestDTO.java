package com.sellspark.SellsHRMS.dto.files;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Base64FileUploadRequestDTO {
    private String base64Data; // Base64 encoded string (may include prefix)
    private String originalName; // Optional, e.g. "resume.pdf"
    private String module; // e.g. "projects", "tasks", "hr"
    private String entityFolder; // e.g. "project-12", "employee-45"
}
