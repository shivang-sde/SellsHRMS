package com.sellspark.SellsHRMS.controller.api.common;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.dto.files.Base64FileUploadRequestDTO;
import com.sellspark.SellsHRMS.dto.files.FileUploadResponseDTO;
import com.sellspark.SellsHRMS.service.files.FileStorageService;
import com.sellspark.SellsHRMS.service.files.FileUploadService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 🌍 Universal File Upload API
 *
 * Supports both:
 * - Multipart form uploads (e.g. web clients)
 * - Base64 uploads (e.g. mobile clients)
 *
 * No business logic — purely for generic file handling.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadRestController {

    private final FileUploadService fileUploadService;
    private final FileStorageService fileStorageService;

    // ----------------------------------------------------
    // 🟢 MULTIPART UPLOAD
    // ----------------------------------------------------
    @PostMapping(value = "/upload", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<List<FileUploadResponseDTO>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String module,
            @RequestParam(required = false) String entityFolder) {

        var uploaded = fileUploadService.uploadMultipartFiles(files, module, entityFolder);
        return ResponseEntity.ok(ApiResponse.ok("Files uploaded successfully", uploaded));
    }

    // ----------------------------------------------------
    // 🟣 BASE64 UPLOAD
    // ----------------------------------------------------
    @PostMapping("/upload/base64")
    public ResponseEntity<ApiResponse<FileUploadResponseDTO>> uploadBase64File(
            @RequestBody Base64FileUploadRequestDTO req) {

        var uploaded = fileUploadService.uploadBase64File(req);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded successfully", uploaded));
    }

    // ----------------------------------------------------
    // 🟤 SECURE FILE DOWNLOAD
    // ----------------------------------------------------
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String fileUrl) {
        try {
            // Convert relative URL to absolute path
            Path filePath = fileStorageService.toAbsolutePath(fileUrl);

            // Security: Ensure the path is inside allowed base directory
            if (filePath == null || !Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("File not found"));
            }

            // Detect MIME type
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null)
                mimeType = "application/octet-stream";

            InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to download file"));
        }
    }
}
