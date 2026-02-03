package com.sellspark.SellsHRMS.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.service.files.FileStorageService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Centralized file utility to simplify file upload & folder path generation.
 * Works across modules (projects, tasks, HR, etc.)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageUtils {

    private final FileStorageService fileStorageService;

    // ------------------------------------------------------------
    // 🟢 MULTIPART UPLOADS
    // ------------------------------------------------------------

    /**
     * Uploads a single file to a structured folder.
     * 
     * @param file         file to upload
     * @param baseFolder   root folder (e.g. "projects", "tasks",
     *                     "employee-documents")
     * @param entityFolder subfolder (e.g. "project-12", "task-45")
     * @return accessible file URL (e.g. /uploads/projects/project-12/uuid.pdf)
     */
    public String saveFile(MultipartFile file, String baseFolder, String entityFolder) {
        try {
            String folder = buildFolderPath(baseFolder, entityFolder);
            return fileStorageService.store(file, folder);
        } catch (Exception e) {
            log.error("Error saving file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Uploads multiple files at once to the same folder.
     */
    public List<String> saveFiles(List<MultipartFile> files, String baseFolder, String entityFolder) {
        List<String> urls = new ArrayList<>();
        if (files == null || files.isEmpty())
            return urls;

        for (MultipartFile file : files) {
            urls.add(saveFile(file, baseFolder, entityFolder));
        }
        return urls;
    }

    // ------------------------------------------------------------
    // 🟣 BASE64 UPLOADS (for mobile / API)
    // ------------------------------------------------------------

    /**
     * Uploads a single Base64-encoded file.
     * 
     * @param base64Content base64 string of file (e.g.
     *                      "data:image/png;base64,....")
     * @param originalName  optional original filename (if known)
     * @param baseFolder    main module folder (e.g. "projects")
     * @param entityFolder  subfolder (e.g. "project-12")
     * @return accessible file URL
     */
    public String saveBase64File(String base64Content, String originalName, String baseFolder, String entityFolder) {
        if (base64Content == null || base64Content.isBlank())
            throw new IllegalArgumentException("Base64 content is empty");

        try {
            // Extract metadata if present (e.g. "data:image/png;base64,....")
            String[] parts = base64Content.split(",");
            String metadata = parts.length > 1 ? parts[0] : "";
            String base64Data = parts.length > 1 ? parts[1] : parts[0];

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Guess extension from metadata or original filename
            String ext = "";
            if (metadata.contains("image/png"))
                ext = ".png";
            else if (metadata.contains("image/jpeg"))
                ext = ".jpg";
            else if (metadata.contains("application/pdf"))
                ext = ".pdf";
            else if (originalName != null) {
                int lastDot = originalName.lastIndexOf('.');
                ext = lastDot > 0 ? originalName.substring(lastDot) : "";
            }

            String safeName = UUID.randomUUID() + ext;
            String folder = buildFolderPath(baseFolder, entityFolder);

            // Create temp file (to reuse existing FileStorageService)
            Path tempFile = Files.createTempFile("upload-", safeName);
            Files.write(tempFile, decodedBytes);

            MultipartFile multipartFile = new TempMultipartFile(tempFile, safeName, Files.probeContentType(tempFile));
            String url = fileStorageService.store(multipartFile, folder);

            Files.deleteIfExists(tempFile);
            return url;

        } catch (Exception e) {
            log.error("❌ Error saving Base64 file: {}", e.getMessage());
            throw new RuntimeException("Base64 upload failed: " + e.getMessage());
        }
    }

    /**
     * Uploads multiple Base64 files.
     */
    public List<String> saveBase64Files(List<String> base64Files, String baseFolder, String entityFolder) {
        List<String> urls = new ArrayList<>();
        if (base64Files == null || base64Files.isEmpty())
            return urls;
        for (String content : base64Files)
            urls.add(saveBase64File(content, null, baseFolder, entityFolder));
        return urls;
    }

    // ------------------------------------------------------------
    // 🔴 DELETE / PATH HELPERS
    // ------------------------------------------------------------

    /**
     * Deletes a file by its URL.
     */
    public boolean deleteFile(String urlPath) {
        try {
            return fileStorageService.delete(urlPath);
        } catch (Exception e) {
            log.error("Error deleting file: {}", urlPath, e);
            return false;
        }
    }

    /**
     * Builds a safe relative folder path: e.g. "projects/project-12"
     */
    public String buildFolderPath(String baseFolder, String entityFolder) {
        StringBuilder folder = new StringBuilder();
        folder.append(baseFolder);

        if (entityFolder != null && !entityFolder.isBlank()) {
            if (!entityFolder.startsWith("/"))
                folder.append("/");
            folder.append(entityFolder);
        }

        return folder.toString().replace("\\", "/");
    }

    /**
     * Generates a unique folder name for a given org/module/entity.
     * Example: "org-1/projects/project-25"
     */
    public String buildOrgEntityPath(Long orgId, String moduleName, Long entityId) {
        StringBuilder path = new StringBuilder("org-").append(orgId).append("/").append(moduleName);
        if (entityId != null)
            path.append("/").append(moduleName).append("-").append(entityId);
        return path.toString();
    }

    // ------------------------------------------------------------
    // 🧩 Inner Helper Class — Temp Multipart Adapter
    // ------------------------------------------------------------
    private record TempMultipartFile(Path file, String originalFilename, String contentType)
            implements MultipartFile {

        @Override
        public String getName() {
            return originalFilename;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return file == null || file.toFile().length() == 0;
        }

        @Override
        public long getSize() {
            return file.toFile().length();
        }

        @Override
        public byte[] getBytes() throws IOException {
            return Files.readAllBytes(file);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(file);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.copy(file, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
