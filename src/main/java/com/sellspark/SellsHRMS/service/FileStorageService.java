package com.sellspark.SellsHRMS.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    
    /**
     * Stores file under folder (relative to base dir). Returns the public URL path to the stored file (e.g. /uploads/empId/filename)
     */
    
    String store(MultipartFile file, String relativeFolder) throws Exception;


      /**
     * Delete stored file by its path (path returned by store). Return true if deleted.
     */
    boolean delete(String urlPath) throws Exception;


      /**
     * absolute path on disk for the given stored URL (optional helper)
     */
    Path toAbsolutePath(String urlPath);

}
