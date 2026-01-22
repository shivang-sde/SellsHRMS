<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="events-management">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Events</h2>
      <p class="text-muted mb-0">Manage organizational events</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createEventModal">
      <i class="fa fa-calendar-plus me-2"></i>Create Event
    </button>
  </div>

  <div class="card border-0 shadow-sm">
    <div class="card-body">
      <div id="eventsList"></div>
    </div>
  </div>
</div>

<!-- Create Modal -->
<div class="modal fade" id="createEventModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Create Event</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="createEventForm">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" rows="3"></textarea>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Date <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="startDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">End Date <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="endDate" required>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">Location</label>
            <input type="text" class="form-control" name="location">
          </div>
          <div class="mb-3">
            <label class="form-label">Type</label>
            <select class="form-select" name="type">
              <option value="MEETING">Meeting</option>
              <option value="CELEBRATION">Celebration</option>
              <option value="TRAINING">Training</option>
              <option value="CUSTOM">Custom</option>
            </select>
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
<div class="modal fade" id="editEventModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Event</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="editEventForm">
        <input type="hidden" id="editEventId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title" id="editEventTitle" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" id="editEventDescription" rows="3"></textarea>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Date</label>
              <input type="date" class="form-control" name="startDate" id="editEventStartDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">End Date</label>
              <input type="date" class="form-control" name="endDate" id="editEventEndDate" required>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">Location</label>
            <input type="text" class="form-control" name="location" id="editEventLocation">
          </div>
          <div class="mb-3">
            <label class="form-label">Type</label>
            <select class="form-select" name="type" id="editEventType">
              <option value="MEETING">Meeting</option>
              <option value="CELEBRATION">Celebration</option>
              <option value="TRAINING">Training</option>
              <option value="CUSTOM">Custom</option>
            </select>
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
.event-card {
  border-left: 4px solid #198754;
  transition: all 0.2s;
}
.event-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
</style>