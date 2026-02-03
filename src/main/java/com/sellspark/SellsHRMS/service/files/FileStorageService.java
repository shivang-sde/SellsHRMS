package com.sellspark.SellsHRMS.service.files;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

/**
 * 🔹 Core abstraction for file storage across the system.
 *
 * Can be implemented by:
 * - LocalFileStorageService (Local disk)
 * - S3FileStorageService (Amazon S3)
 * - AzureBlobStorageService (Azure)
 *
 * Used by FileStorageUtils, FileUploadHelper, and all modules.
 */
public interface FileStorageService {

  /**
   * Stores a file under the given folder path.
   *
   * @param file           the file to store
   * @param relativeFolder relative folder path (e.g. "projects/project-12")
   * @return the public or relative URL (e.g.
   *         "/uploads/projects/project-12/uuid.png")
   * @throws Exception if the operation fails
   */
  String store(MultipartFile file, String relativeFolder) throws Exception;

  /**
   * Deletes a stored file by its URL or path.
   *
   * @param urlPath full or relative URL (e.g.
   *                "/uploads/projects/project-12/uuid.png")
   * @return true if deleted successfully, false otherwise
   * @throws Exception if deletion fails
   */
  boolean delete(String urlPath) throws Exception;

  /**
   * Resolves a URL (e.g. /uploads/xyz.png) into an absolute local filesystem
   * path.
   * Used mainly in local storage implementations.
   *
   * @param urlPath stored file URL or relative path
   * @return the absolute path to the file (or null if not found)
   */
  Path toAbsolutePath(String urlPath);
}
