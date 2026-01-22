package com.sellspark.SellsHRMS.service.impl;


import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentResponse;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.EmployeeDocument;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.FileUploadException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeDocumentRepository;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.service.EmployeeDocumentService;
import com.sellspark.SellsHRMS.service.FileStorageService;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {
    
    private final EmployeeRepository employeeRepo;
    private final EmployeeDocumentRepository docRepo;
    private final FileStorageService storage;

    @Override
    public List<EmployeeDocumentResponse> getByEmployee(Long empId){
        return docRepo.findByEmployeeId(empId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EmployeeDocumentResponse uploadFile(Long empId, String docType, MultipartFile file) throws Exception {
        
        Employee emp = employeeRepo.findById(empId)
        .orElseThrow(() -> new EmployeeNotFoundException(empId));

        // If an entry for this type exists, we update it (replace file), else insert new.
        Optional<EmployeeDocument> existingDocOpt = docRepo.findByEmployeeIdAndDocumentType(empId, docType);

        //store file into disk
        String relativeFolder = String.valueOf(empId); // store under folder with empid
        String publicUrl = storage.store(file, relativeFolder);

        EmployeeDocument doc = existingDocOpt.orElseGet(() -> EmployeeDocument.builder()
            .employee(emp).documentType(docType).build());

        // if existed previous fileurl, try to delete old file (best-effort)

        try{
            if(doc.getFileUrl() != null && !doc.getFileUrl().isBlank()){
                storage.delete(doc.getFileUrl());
            }

        }catch(Exception ignored) {
            
        }

        doc.setFileUrl(publicUrl);
        doc.setExternalUrl(null);
        doc.setContentType(docType);
        doc.setUploadedAt(LocalDateTime.now());
        doc.setVerified(false);

        EmployeeDocument saved = docRepo.save(doc);

        // kick background processing: e.g. virus scan, generate thumbnail, push to S3, OCR, etc.
        postUploadBackgroundProcessing(saved);
        return toResponse(saved);

    }

    @Override
    @Transactional
    public EmployeeDocumentResponse saveLink(EmployeeDocumentRequest req) {
        Employee emp = employeeRepo.findById(req.getEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(req.getEmployeeId()));

        Optional<EmployeeDocument> existingOpt = docRepo.findByEmployeeIdAndDocumentType(req.getEmployeeId(), req.getDocumentType());

        EmployeeDocument doc = existingOpt.orElseGet(() -> EmployeeDocument.builder()
            .employee(emp)
            .documentType(req.getDocumentType())
            .build());

        if(req.isRemoveFile()) {
            try {
                if (doc.getFileUrl() != null) storage.delete(doc.getFileUrl());
                
            } catch (Exception ignored) {}
            doc.setFileUrl(null);
        }
        doc.setExternalUrl(req.getExternalUrl());
        doc.setUploadedAt(LocalDateTime.now());
        doc.setVerified(false);
        docRepo.save(doc);

        return toResponse(doc);
    }

    @Override
    @Transactional
    public void delete(Long documentId) throws Exception {
        EmployeeDocument doc = docRepo.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("EmployeeDocument", "id", documentId));
        
        
        // delete file if stored
        if(doc.getFileUrl() != null) {
            try{
                storage.delete(doc.getFileUrl());
            } catch(Exception ignored) {
                log.warn("Failed to delete old file", ignored);
                throw new FileUploadException("Failed to upload/delete file", ignored);
            }
        }
        docRepo.delete(doc);
    }

    private EmployeeDocumentResponse toResponse(EmployeeDocument d) {
        String name = null;
        if(d.getEmployee() != null) {
            String f = d.getEmployee().getFirstName() == null ? "" : d.getEmployee().getFirstName();
            String l = d.getEmployee().getLastName() ==  null ? "" : d.getEmployee().getLastName();
            name = (f + " " + l).trim();
        }

        return EmployeeDocumentResponse.builder()
            .id(d.getId())
            .employeeId(d.getEmployee() != null ? d.getEmployee().getId() : null)
            .employeeName(name)
            .documentType(d.getDocumentType())
            .fileUrl(d.getFileUrl())
            .externalUrl(d.getExternalUrl())
            .originalFilename(d.getOriginalFilename())
            .contentType(d.getContentType())
            .uploadedAt(d.getUploadedAt())
            .verified(d.getVerified())
            .build();
    }

    /**
     * Background processing hook. This method is called AFTER storing metadata and file.
     * Marked @Async to run on executor (fileProcessingExecutor).
     */
    @Async("fileProcessingExecutor")
    public void postUploadBackgroundProcessing(EmployeeDocument doc) {
        try {
            // sample tasks:
            // 1) virus scan (call to local virus scanner)
            // 2) generate thumbnail for PDFs/images
            // 3) copy/move to cloud storage (S3)
            // 4) send notification / audit log

            // Example placeholder:
            // Thread.sleep(2000); // simulate work
            // If you want to push to S3, call S3 upload here (not included)
        } catch (Exception ex) {
            // log but don't fail the upload response
            ex.printStackTrace();
        }
    }

    // Background job uses @Async("fileProcessingExecutor"). Configure executor sizes in application.properties.
    // If you add heavy processing (OCR, PDF conversions), do it here and push to S3/cdn inside this method.
}
