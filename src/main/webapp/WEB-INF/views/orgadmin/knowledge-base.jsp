<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="kb-management">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Knowledge Base</h2>
      <p class="text-muted mb-0">Manage organizational knowledge and documentation</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createSubjectModal">
      <i class="fa fa-plus me-2"></i>Create Subject
    </button>
  </div>

  <!-- Subjects Grid -->
  <div class="row" id="subjectsGrid">
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
  </div>
</div>

<!-- Create Subject Modal -->
<div class="modal fade" id="createSubjectModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Create Subject</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="createSubjectForm">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" rows="3"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Create Subject</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Subject Modal -->
<div class="modal fade" id="editSubjectModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Subject</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="editSubjectForm">
        <input type="hidden" id="editSubjectId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" id="editSubjectTitle" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" id="editSubjectDescription" rows="3"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Update Subject</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- View Subject with Topics Modal -->
<div class="modal fade" id="viewSubjectModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h5 class="modal-title" id="viewSubjectTitle"></h5>
          <small class="text-muted" id="viewSubjectDescription"></small>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="mb-0">Topics</h6>
          <button class="btn btn-sm btn-primary" onclick="openCreateTopicModal()">
            <i class="fa fa-plus me-1"></i>Add Topic
          </button>
        </div>
        <div id="topicsList">
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Create Topic Modal -->
<div class="modal fade" id="createTopicModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Create Topic</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="createTopicForm">
        <input type="hidden" id="createTopicSubjectId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Content <span class="text-danger">*</span></label>
            <textarea class="form-control" name="content" rows="6" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Attachment URL (optional)</label>
            <input type="url" class="form-control" name="attachmentUrl" placeholder="https://...">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Create Topic</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Topic Modal -->
<div class="modal fade" id="editTopicModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Topic</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="editTopicForm">
        <input type="hidden" id="editTopicId">
        <input type="hidden" id="editTopicSubjectId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" id="editTopicTitle" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Content <span class="text-danger">*</span></label>
            <textarea class="form-control" name="content" id="editTopicContent" rows="6" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Attachment URL (optional)</label>
            <input type="url" class="form-control" name="attachmentUrl" id="editTopicAttachment" placeholder="https://...">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Update Topic</button>
        </div>
      </form>
    </div>
  </div>
</div>

<style>
.subject-card {
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  height: 100%;
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15) !important;
}

.topic-item {
  border-left: 3px solid #0d6efd;
  transition: all 0.2s;
}

.topic-item:hover {
  background-color: #f8f9fa;
  border-left-color: #0b5ed7;
}
</style>