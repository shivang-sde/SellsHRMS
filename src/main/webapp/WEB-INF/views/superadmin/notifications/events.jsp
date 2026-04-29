<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="mb-0"><i class="fa fa-bell me-2 text-primary"></i>Notification Events</h4>
                <button class="btn btn-primary-hrms" data-bs-toggle="modal" data-bs-target="#eventModal"
                    onclick="openCreateModal()">
                    <i class="fa fa-plus me-1"></i>Add Event
                </button>
            </div>

            <div class="card hrms-card shadow-sm">
                <div class="card-body">
                    <!-- Filters -->
                    <div class="row g-2 mb-3">
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="searchInput" placeholder="Search events..."
                                onkeyup="filterEvents()">
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" id="moduleFilter" onchange="filterEvents()">
                                <option value="">All Modules</option>
                                <option value="LEAVE">Leave</option>
                                <option value="ATTENDANCE">Attendance</option>
                                <option value="PAYROLL">Payroll</option>
                                <option value="RECRUITMENT">Recruitment</option>
                                <option value="PERFORMANCE">Performance</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <select class="form-select" id="statusFilter" onchange="filterEvents()">
                                <option value="">All Status</option>
                                <option value="true">Active</option>
                                <option value="false">Inactive</option>
                            </select>
                        </div>
                    </div>

                    <!-- Events Table -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="eventsTable">
                            <thead class="table-light">
                                <tr>
                                    <th>Event Code</th>
                                    <th>Module</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody id="eventsBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>

                    <!-- Empty State -->
                    <div id="emptyState" class="text-center py-5 d-none">
                        <i class="fa fa-bell-slash fa-3x text-muted mb-3"></i>
                        <h5 class="text-muted">No events found</h5>
                        <p class="text-muted small">Create your first notification event to get started</p>
                        <button class="btn btn-primary-hrms" data-bs-toggle="modal" data-bs-target="#eventModal"
                            onclick="openCreateModal()">
                            <i class="fa fa-plus me-1"></i>Create Event
                        </button>
                    </div>
                </div>
            </div>

            <!-- Event Modal -->
            <div class="modal fade" id="eventModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form id="eventForm">
                            <div class="modal-header">
                                <h5 class="modal-title" id="modalTitle">Add Notification Event</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" id="eventId">

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Event Code <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="formEventCode"
                                        placeholder="e.g., LEAVE_APPROVED" pattern="[A-Z_]+"
                                        title="Use uppercase letters and underscores only" maxlength="50" required>
                                    <div class="form-text">Unique identifier (e.g., LEAVE_APPROVED, PAYROLL_GENERATED)
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Module <span
                                            class="text-danger">*</span></label>
                                    <select class="form-select" id="formModule" required>
                                        <option value="">Select Module</option>
                                        <option value="EMPLOYEE">Employee</option>
                                        <option value="LEAVE">Leave</option>
                                        <option value="ATTENDANCE">Attendance</option>
                                        <option value="PRODUCTIVITY">Productivity</option>
                                        <!-- <option value="PAYROLL">Payroll</option> -->
                                        <!-- <option value="RECRUITMENT">Recruitment</option> -->
                                        <!-- <option value="PERFORMANCE">Performance Review</option> -->
                                        <!-- <option value="ONBOARDING">Onboarding</option> -->
                                        <!-- <option value="OFFBOARDING">Offboarding</option> -->
                                        <!-- <option value="SYSTEM">System</option> -->
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Description</label>
                                    <textarea class="form-control" id="formDescription" rows="3"
                                        placeholder="Brief description of when this event triggers..."
                                        maxlength="500"></textarea>
                                </div>

                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" id="formIsActive" checked>
                                    <label class="form-check-label fw-semibold" for="formIsActive">
                                        Active by Default
                                    </label>
                                    <div class="form-text">Inactive events won't appear in preference settings</div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary-hrms"
                                    data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-primary-hrms">
                                    <i class="fa fa-save me-1"></i>Save Event
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Delete Confirmation Modal -->
            <div class="modal fade" id="deleteModal" tabindex="-1">
                <div class="modal-dialog modal-sm modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-body text-center py-4">
                            <i class="fa fa-triangle-exclamation fa-3x text-warning mb-3"></i>
                            <h5>Delete Event?</h5>
                            <p class="text-muted small mb-0">
                                This will remove the event from all organization preferences. This action cannot be
                                undone.
                            </p>
                        </div>
                        <div class="modal-footer justify-content-center">
                            <button type="button" class="btn btn-secondary-hrms" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Delete</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Loading Overlay -->
            <div id="loadingOverlay" class="loading-overlay d-none">
                <div class="spinner-border text-primary" role="status"></div>
            </div>