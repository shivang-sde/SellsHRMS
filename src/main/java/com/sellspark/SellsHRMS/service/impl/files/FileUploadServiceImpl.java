package com.sellspark.SellsHRMS.service.impl.files;

import com.sellspark.SellsHRMS.dto.files.Base64FileUploadRequestDTO;
import com.sellspark.SellsHRMS.dto.files.FileUploadResponseDTO;
import com.sellspark.SellsHRMS.utils.FileStorageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sellspark.SellsHRMS.service.files.FileUploadService;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔹 Universal service for handling file uploads (multipart or base64).
 * No business logic — purely storage-related.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileStorageUtils fileStorageUtils;

    /**
     * Handles multipart file uploads.
     */
    public List<FileUploadResponseDTO> uploadMultipartFiles(
            List<MultipartFile> files, String module, String entityFolder) {

        List<FileUploadResponseDTO> result = new ArrayList<>();
        if (files == null || files.isEmpty())
            return result;

        for (MultipartFile file : files) {
            String url = fileStorageUtils.saveFile(file, module, entityFolder);
            result.add(FileUploadResponseDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(url)
                    .fileType(file.getContentType())
                    .fileSizeKB((double) file.getSize() / 1024)
                    .build());
        }
        return result;
    }

    /**
     * Handles base64 uploads (for mobile/API).
     */
    public FileUploadResponseDTO uploadBase64File(Base64FileUploadRequestDTO req) {
        String url = fileStorageUtils.saveBase64File(
                req.getBase64Data(),
                req.getOriginalName(),
                req.getModule(),
                req.getEntityFolder());

        return FileUploadResponseDTO.builder()
                .fileName(req.getOriginalName())
                .fileUrl(url)
                .fileType(detectFileType(req.getBase64Data(), req.getOriginalName()))
                .build();
    }

    /**
     * Basic MIME-type detection (optional enhancement).
     */
    private String detectFileType(String base64, String name) {
        if (base64 != null && base64.startsWith("data:")) {
            int end = base64.indexOf(';');
            if (end > 5)
                return base64.substring(5, end);
        }
        if (name != null && name.toLowerCase().endsWith(".pdf"))
            return "application/pdf";
        if (name != null && name.toLowerCase().endsWith(".png"))
            return "image/png";
        if (name != null && name.toLowerCase().endsWith(".jpg"))
            return "image/jpeg";
        return "application/octet-stream";
    }
}
