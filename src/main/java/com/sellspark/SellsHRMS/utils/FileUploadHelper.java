package com.sellspark.SellsHRMS.utils;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.entity.TaskAttachment;
import com.sellspark.SellsHRMS.repository.TaskAttachmentRepository;
import com.sellspark.SellsHRMS.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A generic, reusable file upload helper for any entity type (task, comment, HR docs, etc.)
 */
@Component
@RequiredArgsConstructor
public class FileUploadHelper {

    private final FileStorageService fileStorageService;
    private final TaskAttachmentRepository taskAttachmentRepository;

    /**
     * Uploads and optionally saves attachments for a given business context.
     *
     * @param files         files to upload
     * @param baseFolder    logical folder (e.g. "tasks", "employee-documents", "org-policies")
     * @param entityFolder  additional subfolder context (e.g. "task-12", "comment-45")
     * @param uploader      employee performing the upload
     * @param task          optional task to associate attachments
     * @return list of uploaded file metadata or saved TaskAttachment entities
     */
    @Async("fileProcessingExecutor")
    public List<TaskAttachment> uploadFiles(
            List<MultipartFile> files,
            String baseFolder,
            String entityFolder,
            Employee uploader,
            Task task
    ) throws Exception {

        if (files == null || files.isEmpty()) return List.of();

        List<TaskAttachment> attachments = new ArrayList<>();

        // Build folder structure: e.g. /uploads/tasks/task-12/
        String folderPath = baseFolder;
        if (entityFolder != null && !entityFolder.isBlank()) {
            folderPath = baseFolder + "/" + entityFolder;
        }

        for (MultipartFile file : files) {
            String fileUrl = fileStorageService.store(file, folderPath);

            TaskAttachment attachment = TaskAttachment.builder()
                    .task(task)
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSizeKB((double) file.getSize() / 1024)
                    .uploadedBy(uploader) //// can be null
                    .uploadedAt(LocalDateTime.now())
                    .build();

            if (task != null) {
                taskAttachmentRepository.save(attachment);
            }

            attachments.add(attachment);
        }

        return attachments;
    }

    /**
     * Deletes a file from disk and optionally the DB record.
     */
    public boolean deleteAttachment(TaskAttachment attachment) {
        try {
            fileStorageService.delete(attachment.getFileUrl());
            if (attachment.getId() != null) {
                taskAttachmentRepository.delete(attachment);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Builds a safe folder name for any purpose (e.g. org/project/task)
     */
    public String buildFolderPath(Long organisationId, String moduleName, Long entityId) {
        String base = "org-" + organisationId + "/" + moduleName;
        if (entityId != null) base += "/" + moduleName + "-" + entityId;
        return base;
    }
}
