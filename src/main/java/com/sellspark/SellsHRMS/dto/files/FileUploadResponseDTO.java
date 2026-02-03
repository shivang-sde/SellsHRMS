package com.sellspark.SellsHRMS.dto.files;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponseDTO {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Double fileSizeKB;
}
