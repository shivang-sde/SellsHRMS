<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="projects-page">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-project-diagram text-primary me-2"></i>Projects</h2>
            <p class="text-muted mb-0">Manage and track all organization projects</p>
        </div>
        <button class="btn btn-primary" onclick="openProjectModal()">
            <i class="fas fa-plus me-2"></i>New Project
        </button>
    </div>

    <!-- Filters -->
    <div class="card border-0 shadow-sm mb-3">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <input type="text" class="form-control" id="searchInput" 
                           placeholder="Search projects...">
                </div>
                <div class="col-md-3">
                    <select class="form-select" id="statusFilter">
                        <option value="">All Status</option>
                        <option value="ACTIVE">Active</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="ON_HOLD">On Hold</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                </div>
                <!-- <div class="col-md-3">
                    <select class="form-select" id="typeFilter">
                        <option value="">All Types</option>
                        <option value="INTERNAL">Internal</option>
                        <option value="EXTERNAL">External</option>
                        <option value="CLIENT">Client</option>
                    </select>
                </div> -->
                <div class="col-md-2">
                    <button class="btn btn-outline-secondary w-100" onclick="resetFilters()">
                        <i class="fas fa-redo me-2"></i>Reset
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Projects Table -->
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="projectsTable">
                    <thead class="table-light">
                        <tr>
                            <th>Project Name</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Priority</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Manager</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="7" class="text-center py-5">
                                <div class="spinner-border text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Project Modal -->
<div class="modal fade" id="projectModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="fas fa-project-diagram me-2"></i>
          <span id="modalTitle">Add New Project</span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div class="modal-body">
        <form id="projectForm">
          <div class="row g-3">
            <!-- Project Name -->
            <div class="col-md-6">
              <label class="form-label">Project Name <span class="text-danger">*</span></label>
              <input type="text" class="form-control" name="name" required>
            </div>

            <!-- Type -->
            <div class="col-md-6">
              <label class="form-label">Project Type <span class="text-danger">*</span></label>
              <select class="form-select" name="projectType" required>
                <option value="">Select Type</option>
                <option value="SOFTWARE_DEVELOPMENT">Software Development</option>
                <option value="IT_SUPPORT">IT Support</option>
                <option value="DESIGN">Design</option>
                <option value="CUSTOMER_SERVICE">Customer Service</option>
                <option value="HR">HR</option>
                <option value="MARKETING">Marketing</option>
                <option value="OPERATIONS">Operations</option>
                <option value="SALES">Sales</option>
                <option value="FINANCE">Finance</option>
                <option value="OTHER_PERSONAL">Other/Personal</option>
              </select>
            </div>

            <!-- Methodology -->
            <div class="col-md-6">
              <label class="form-label">Methodology <span class="text-danger">*</span></label>
              <select class="form-select" name="methodology" required>
                <option value="">Select Methodology</option>
                <option value="AGILE">Agile</option>
                <option value="SCRUM">Scrum</option>
                <option value="KANBAN">Kanban</option>
                <option value="WATERFALL">Waterfall</option>
                <option value="LEAN">Lean</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <!-- Priority -->
            <div class="col-md-6">
              <label class="form-label">Priority <span class="text-danger">*</span></label>
              <select class="form-select" name="priority" required>
                <option value="">Select Priority</option>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>

            <!-- Status -->
            <div class="col-md-6">
              <label class="form-label">Status <span class="text-danger">*</span></label>
              <select class="form-select" name="status" required>
                <option value="PLANNING">Planning</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="ON_HOLD">On Hold</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>

            <!-- Dates -->
            <div class="col-md-6">
              <label class="form-label">Start Date <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="startDate" required>
            </div>

            <div class="col-md-6">
              <label class="form-label">End Date</label>
              <input type="date" class="form-control" name="endDate">
            </div>

            <!-- Description -->
            <div class="col-12">
              <label class="form-label">Description</label>
              <textarea class="form-control" name="description" rows="3"></textarea>
            </div>
          </div>

          <!-- Project Manager -->
<div class="col-md-6">
  <label class="form-label">Project Manager</label>
  <select class="form-select" id="projectManagerSelect" name="projectManagerId">
    <option value="">-- Default: You --</option>
  </select>
  <small class="text-muted">If left blank, you will be assigned as Project Manager.</small>
</div>

<!-- Project Team Lead -->
<div class="col-md-6">
  <label class="form-label">Project Team Lead</label>
  <select class="form-select" id="projectTeamLeadSelect" name="projectTeamLeadId">
    <option value="">Select Team Lead</option>
  </select>
</div>


        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveProject()">Save Project</button>
      </div>
    </div>
  </div>
</div>
