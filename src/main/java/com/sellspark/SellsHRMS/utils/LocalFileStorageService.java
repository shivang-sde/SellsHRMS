package com.sellspark.SellsHRMS.utils;

import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sellspark.SellsHRMS.service.files.FileStorageService;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.upload.base-dir}")
    private String baseDir; // e.g. "C:/uploads/hrms-files/"

    @Value("${app.upload.url-path:/files/hrms}")
    private String urlPath; // e.g. "/files/hrms"

    @Override
    public String store(MultipartFile file, String relativeFolder) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file upload not allowed");
        }

        // Clean file name and generate unique one
        String cleanName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int idx = cleanName.lastIndexOf('.');
        if (idx > 0)
            ext = cleanName.substring(idx);
        String fileName = UUID.randomUUID() + ext;

        // Create directory
        Path folder = Paths.get(baseDir).toAbsolutePath().normalize().resolve(relativeFolder);
        Files.createDirectories(folder);
        Path target = folder.resolve(fileName);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // Build file URL
        String rel = relativeFolder.replace("\\", "/");
        if (!rel.startsWith("/"))
            rel = "/" + rel;

        return urlPath + rel + "/" + fileName;
    }

    @Override
    public boolean delete(String urlPath) throws Exception {
        if (urlPath == null)
            return false;
        Path abs = toAbsolutePath(urlPath);
        if (abs != null)
            return Files.deleteIfExists(abs);
        return false;
    }

    @Override
    public Path toAbsolutePath(String urlPath) {
        if (urlPath == null)
            return null;

        String cleaned = urlPath;
        if (cleaned.startsWith(this.urlPath)) {
            cleaned = cleaned.substring(this.urlPath.length());
        }
        if (cleaned.startsWith("/"))
            cleaned = cleaned.substring(1);

        return Paths.get(baseDir).toAbsolutePath().normalize().resolve(cleaned);
    }
}
