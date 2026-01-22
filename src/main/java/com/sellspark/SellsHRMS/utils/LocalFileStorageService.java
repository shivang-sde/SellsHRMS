package com.sellspark.SellsHRMS.utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.service.FileStorageService;

@Service
public class LocalFileStorageService  implements FileStorageService{
 
    @Value("${app.upload.base-dir}")
    private String baseDir;

    @Value("${app.upload.url-path:/uploads}")
    private String uploadUrlPath;

    @Override
    public String store(MultipartFile file, String relativeFolder) throws Exception {

        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("Empty File");
        }

        String cleanName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int idx = cleanName.lastIndexOf('.');
        if(idx > 0) ext = cleanName.substring(idx);

        String fileName = UUID.randomUUID().toString() + ext;


       Path folder = Paths.get(baseDir).toAbsolutePath().normalize().resolve(relativeFolder);
       Files.createDirectories(folder);
       Path target = folder.resolve(fileName);

       // store

       try(InputStream is = file.getInputStream()){
        Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
       }

       String rel = relativeFolder.replace("\\", "/");

       if(!rel.startsWith("/")) rel = "/" + rel;

       return uploadUrlPath + rel + "/" + fileName;

    }



    @Override
    public boolean delete(String urlPath) throws Exception {
        if(urlPath == null) return false;

        // urlPath like /uploads/12/urlPath
        Path abs = toAbsolutePath(urlPath);

        if(abs != null) {
            return Files.deleteIfExists(abs);
        }
        return false;
    }

    @Override
    public Path toAbsolutePath(String urlPath) {
        if(urlPath == null) return null;

        String cleaned = urlPath;

        //remove prefix (/uploads)
        if(cleaned.startsWith(uploadUrlPath)){
            cleaned = cleaned.substring(uploadUrlPath.length());
        }
        // enusre it doesn't start with "/"
        if(cleaned.startsWith("/")) cleaned.substring(1);
        Path p = Paths.get(baseDir).toAbsolutePath().normalize().resolve(cleaned);
        return p;
    }

    

}
