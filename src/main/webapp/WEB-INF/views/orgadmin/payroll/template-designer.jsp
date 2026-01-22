<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Header -->
<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h3 class="mb-1">Salary Slip Template Designer</h3>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="/dashboard">Home</a></li>
                        <li class="breadcrumb-item"><a href="/payroll">Payroll</a></li>
                        <li class="breadcrumb-item active">Template Designer</li>
                    </ol>
                </nav>
            </div>
            <div>
                <button type="button" class="btn btn-outline-secondary me-2" onclick="loadTemplates()">
                    <i class="bi bi-list-ul"></i> My Templates
                </button>
                <button type="button" class="btn btn-primary" onclick="saveTemplate()">
                    <i class="bi bi-save"></i> Save Template
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Main Content Row -->
<div class="row">
    <!-- Left Panel - Field Selection -->
    <div class="col-lg-4">
        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-sliders"></i> Template Configuration</h5>
            </div>
            <div class="card-body" style="max-height: 75vh; overflow-y: auto;">
                
                <!-- Template Name -->
                <div class="mb-4">
                    <label class="form-label fw-bold">Template Name</label>
                    <input type="text" class="form-control" id="templateName" placeholder="e.g., Default Salary Slip">
                    <input type="hidden" id="templateId" value="${templateId}">
                </div>

                <!-- Logo Upload -->
                <div class="mb-4">
                    <label class="form-label fw-bold">Company Logo</label>
                    <div class="input-group">
                        <input type="file" class="form-control" id="logoFile" accept="image/*">
                        <button class="btn btn-outline-secondary" type="button" onclick="uploadLogo()">
                            <i class="bi bi-upload"></i> Upload
                        </button>
                    </div>
                    <input type="hidden" id="logoUrl">
                    <small class="text-muted">Recommended: 200x80px, PNG/JPG</small>
                    <div id="logoPreview" class="mt-2" style="display: none;">
                        <img id="logoPreviewImage" src="" alt="Logo Preview" style="max-height: 60px;">
                    </div>
                </div>

                <!-- Set as Default -->
                <div class="mb-4">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" id="isDefault">
                        <label class="form-check-label" for="isDefault">
                            Set as Default Template
                        </label>
                    </div>
                </div>

                <hr>

                <!-- Field Sections (Dynamically loaded) -->
                <div id="fieldSections">
                    <div class="text-center text-muted py-3">
                        <div class="spinner-border spinner-border-sm" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mb-0 mt-2 small">Loading fields...</p>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="mt-4">
                    <button type="button" class="btn btn-success w-100 mb-2" onclick="generatePreview()">
                        <i class="bi bi-eye"></i> Preview Template
                    </button>
                    <button type="button" class="btn btn-outline-secondary w-100" onclick="resetTemplate()">
                        <i class="bi bi-arrow-clockwise"></i> Reset
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Panel - Live Preview -->
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="bi bi-file-earmark-text"></i> Live Preview</h5>
                <div>
                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="printPreview()">
                        <i class="bi bi-printer"></i> Print
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger" onclick="exportToPDF()">
                        <i class="bi bi-file-pdf"></i> PDF
                    </button>
                </div>
            </div>
            <div class="card-body p-4" style="background-color: #f8f9fa;">
                <div id="previewContainer" class="bg-white p-4 shadow-sm" style="min-height: 800px;">
                    <div class="text-center text-muted py-5">
                        <i class="bi bi-file-earmark-text" style="font-size: 4rem; opacity: 0.3;"></i>
                        <p class="mt-3">Select fields and click "Preview Template" to see your design</p>
                        <small class="text-muted">Choose at least organisation and employee fields to get started</small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Template List Modal -->
<div class="modal fade" id="templateListModal" tabindex="-1" aria-labelledby="templateListModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="templateListModalLabel">
                    <i class="bi bi-collection"></i> My Templates
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div id="templateListLoader" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id="templateListContent" style="display: none;">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Template Name</th>
                                <th>Created Date</th>
                                <th class="text-center">Status</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="templateListBody">
                            <!-- Templates will be loaded here via JS -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Custom Styles for Template Designer -->
<style>
    /* Field Section Styling */
    .field-section {
        margin-bottom: 1.5rem;
    }

    .field-section-title {
        font-weight: 600;
        font-size: 0.95rem;
        color: #495057;
        margin-bottom: 0.75rem;
        padding-bottom: 0.5rem;
        border-bottom: 2px solid #e9ecef;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .field-section-title i {
        color: #0d6efd;
    }

    .field-checkboxes {
        padding-left: 0.25rem;
    }

    .form-check {
        padding-left: 1.5rem;
        margin-bottom: 0.5rem;
    }

    .form-check-input {
        cursor: pointer;
        margin-top: 0.25rem;
    }

    .form-check-label {
        cursor: pointer;
        user-select: none;
        font-size: 0.9rem;
        color: #495057;
    }

    .form-check-input:checked ~ .form-check-label {
        color: #0d6efd;
        font-weight: 500;
    }

    /* Preview Container */
    #previewContainer {
        border-radius: 0.375rem;
        transition: all 0.3s ease;
    }

    /* Print Styles */
    @media print {
        body * {
            visibility: hidden;
        }
        
        #previewContainer,
        #previewContainer * {
            visibility: visible;
        }
        
        #previewContainer {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            padding: 0 !important;
            box-shadow: none !important;
            background: white !important;
        }
    }

    /* Loading Animation */
    .spinner-border-sm {
        width: 1rem;
        height: 1rem;
        border-width: 0.15em;
    }

    /* Template Table Styling */
    #templateListBody tr {
        cursor: pointer;
        transition: background-color 0.2s;
    }

    #templateListBody tr:hover {
        background-color: #f8f9fa;
    }
</style>