<!-- org/announcements.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="announcements-management">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Announcements</h2>
      <p class="text-muted mb-0">Manage organizational announcements</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createAnnouncementModal">
      <i class="fa fa-bullhorn me-2"></i>Create Announcement
    </button>
  </div>

  <div class="card border-0 shadow-sm">
    <div class="card-body">
      <div id="announcementsList"></div>
    </div>
  </div>
</div>

<!-- Create Modal -->
<div class="modal fade" id="createAnnouncementModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Create Announcement</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="createAnnouncementForm">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Message <span class="text-danger">*</span></label>
            <textarea class="form-control" name="message" rows="4" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Valid Until</label>
            <input type="datetime-local" class="form-control" name="validUntil">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Create</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Modal -->
<div class="modal fade" id="editAnnouncementModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Announcement</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="editAnnouncementForm">
        <input type="hidden" id="editAnnouncementId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title" id="editTitle" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Message</label>
            <textarea class="form-control" name="message" id="editMessage" rows="4" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Valid Until</label>
            <input type="datetime-local" class="form-control" name="validUntil" id="editValidUntil">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Update</button>
        </div>
      </form>
    </div>
  </div>
</div>

<style>
.announcement-card {
  border-left: 4px solid #0d6efd;
  transition: all 0.2s;
}
.announcement-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
</style>

<script src="/js/org/announcements.js"></script>