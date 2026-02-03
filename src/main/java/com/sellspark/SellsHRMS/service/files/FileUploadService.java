package com.sellspark.SellsHRMS.service.files;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.dto.files.Base64FileUploadRequestDTO;
import com.sellspark.SellsHRMS.dto.files.FileUploadResponseDTO;

public interface FileUploadService {

    FileUploadResponseDTO uploadBase64File(Base64FileUploadRequestDTO request);

    List<FileUploadResponseDTO> uploadMultipartFiles(List<MultipartFile> files, String module, String entityFolder);

}
