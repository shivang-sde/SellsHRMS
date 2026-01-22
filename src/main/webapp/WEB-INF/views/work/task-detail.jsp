<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="currentTaskId" value="${taskId}">

<div class="task-detail-page">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <a href="${pageContext.request.contextPath}/work/tasks" class="btn btn-link text-decoration-none ps-0">
                <i class="fas fa-arrow-left me-2"></i>Back to Tasks
            </a>
            <h2 class="mb-1" id="taskTitle">
                <i class="fas fa-tasks text-warning me-2"></i>Loading...
            </h2>
        </div>
        <div class="btn-group">
            <button class="btn btn-outline-warning" onclick="editCurrentTask()">
                <i class="fas fa-edit me-2"></i>Edit
            </button>
            <button class="btn btn-outline-danger" onclick="deleteCurrentTask()">
                <i class="fas fa-trash me-2"></i>Delete
            </button>
        </div>
    </div>

    <div class="row g-3">
        <!-- Task Details -->
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm mb-3">
                <div class="card-header bg-white border-0">
                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Task Details</h5>
                </div>
                <div class="card-body" id="taskDetails">
                    <div class="text-center py-5">
                        <div class="spinner-border text-primary" role="status"></div>
                    </div>
                </div>
            </div>

            <!-- Activity Log -->
            <div class="card border-0 shadow-sm mb-3">
                <div class="card-header bg-white border-0">
                    <h5 class="mb-0"><i class="fas fa-history me-2"></i>Activity Log</h5>
                </div>
                <div class="card-body">
                    <div id="activityLog">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <!-- Attachments -->
            <div class="card border-0 shadow-sm mb-3">
                <div class="card-header bg-white border-0 d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-paperclip me-2"></i>Attachments</h5>
                    <button class="btn btn-sm btn-primary" onclick="openAttachmentModal()">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
                <div class="card-body">
                    <div id="attachmentsList">
                        <div class="text-center py-3">
                            <div class="spinner-border spinner-border-sm text-primary"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Info -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white border-0">
                    <h5 class="mb-0"><i class="fas fa-chart-line me-2"></i>Quick Info</h5>
                </div>
                <div class="card-body" id="quickInfo">
                    <div class="text-center py-3">
                        <div class="spinner-border spinner-border-sm text-primary"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Attachment Upload Modal -->
<div class="modal fade" id="attachmentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-paperclip me-2"></i>Add Attachment
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="attachmentForm">
                    <div class="mb-3">
                        <label class="form-label">Select File <span class="text-danger">*</span></label>
                        <input type="file" class="form-control" name="file" required>
                        <div class="form-text">Max file size: 10MB</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="2"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="uploadAttachment()">Upload</button>
            </div>
        </div>
    </div>
</div>