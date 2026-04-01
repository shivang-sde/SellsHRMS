<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  <%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>

      <div class="container-fluid py-4 px-lg-5">
        <div class="row mb-4 align-items-end">
          <div class="col-md-8">
            <nav aria-label="breadcrumb" class="mb-2">
              <ol class="breadcrumb small text-uppercase fw-bold ls-1">
                <li class="breadcrumb-item"><a href="#" class="text-decoration-none text-primary">Project Management</a>
                </li>
                <li class="breadcrumb-item active" id="ticketProject">Loading...</li>
              </ol>
            </nav>
            <h2 id="ticketTitle" class="fw-bold text-dark display-6 mb-0">---</h2>
          </div>
          <div class="col-md-4 text-md-end mt-3 mt-md-0">
            <div id="ticketStatusBadge" class="d-inline-block me-2 shadow-sm"></div>
            <button class="btn btn-primary btn-lg rounded-pill px-4 shadow-sm fw-bold" onclick="openTaskModal()">
              <i class="fas fa-plus-circle me-2"></i>Add Work Update
            </button>
          </div>
        </div>

        <div class="row g-4">
          <div class="col-lg-8">
            <div class="card border-0 shadow-sm rounded-4 mb-4">
              <div class="card-body p-4">
                <div class="d-flex align-items-center mb-3">
                  <div class="icon-box bg-soft-primary text-primary me-3">
                    <i class="fas fa-file-alt"></i>
                  </div>
                  <h5 class="fw-bold mb-0">Ticket Description</h5>
                </div>
                <div id="ticketDescription"
                  class="text-secondary lh-lg p-3 bg-light rounded-3 border-start border-primary border-4">
                  Loading description...
                </div>
              </div>
            </div>

            <div class="card border-0 shadow-sm rounded-4">
              <div class="card-header bg-white border-0 pt-4 px-4">
                <h5 class="fw-bold mb-0">Work Updates & Tasks</h5>
              </div>
              <div class="card-body px-0 pb-0">
                <div class="table-responsive">
                  <table class="table table-hover align-middle mb-0" id="tasksTable">
                    <thead class="bg-light">
                      <tr class="text-muted small fw-bold">
                        <th class="ps-4 py-3">TITLE</th>
                        <th>DESCRIPTION</th>
                        <th>AUTHOR</th>
                        <th>DATE</th>
                        <th>FILES</th>
                        <th class="text-end pe-4">ACTIONS</th>
                      </tr>
                    </thead>
                    <tbody>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="sticky-top" style="top: 2rem;">
              <div class="card border-0 shadow-sm rounded-4 mb-4">
                <div class="card-body p-4">
                  <h6 class="fw-bold text-uppercase small text-muted mb-4 ls-1">Timeline & Assignees</h6>

                  <div class="row mb-4">
                    <div class="col-6 border-end">
                      <label class="x-small fw-bold text-muted d-block text-uppercase">Start Date</label>
                      <span id="ticketStartDate" class="text-dark fw-medium small">---</span>
                    </div>
                    <div class="col-6">
                      <label class="x-small fw-bold text-muted d-block text-uppercase">End Date</label>
                      <span id="ticketEndDate" class="text-dark fw-medium small">---</span>
                    </div>
                  </div>

                  <div class="mb-4">
                    <label class="x-small fw-bold text-muted d-block text-uppercase mb-2">Created By</label>
                    <div class="d-flex align-items-center">
                      <div class="avatar-sm bg-info text-white me-2" id="creatorInitials">?</div>
                      <span id="ticketCreatedBy" class="fw-bold small">---</span>
                    </div>
                  </div>

                  <div class="mb-4">
                    <label class="x-small fw-bold text-muted d-block text-uppercase mb-2">Assignees</label>
                    <div id="ticketAssignees" class="d-flex flex-wrap gap-1 small fw-medium text-dark">
                    </div>
                  </div>
                </div>
              </div>

              <div class="card border-0 shadow-sm rounded-4 mb-4">
                <div class="card-header bg-white border-0 pt-4 px-4 pb-0">
                  <h6 class="fw-bold text-uppercase small text-muted ls-1">Attachments</h6>
                </div>
                <div class="card-body p-4" id="ticketAttachmentsList">
                </div>
              </div>

              <div class="card border-0 shadow-sm rounded-4">
                <div class="card-header bg-white border-0 pt-4 px-4 pb-0">
                  <h6 class="fw-bold text-uppercase small text-muted ls-1">Activity Log</h6>
                </div>
                <div class="card-body p-4">
                  <ul class="activity-timeline" id="ticketActivityList">
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>


      <!-- Task Modal -->
      <!-- Add Work Update Modal -->
      <div class="modal fade" id="taskModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <form id="taskForm" class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header border-0 pb-0">
              <h5 class="fw-bold" id="taskModalTitle">Add Work Update</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body py-4">
              <c:if test="${not empty ticketId}">
                <input type="hidden" name="ticketId" id="taskTicketId" value="${ticketId}">
              </c:if>

              <div class="mb-3">
                <label class="form-label small fw-bold text-muted">Update Title</label>
                <input type="text" class="form-control rounded-3 py-2" name="title" placeholder="What did you do?"
                  required>
              </div>

              <div class="mb-3">
                <label class="form-label small fw-bold text-muted">Detailed Description</label>
                <textarea class="form-control rounded-3" name="description" rows="4"
                  placeholder="Describe your progress..." required></textarea>
              </div>

              <div id="attachmentRows" class="bg-light p-3 rounded-3 mb-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <label class="form-label small fw-bold text-muted mb-0">Attachments</label>
                  <button type="button" class="btn btn-sm btn-link text-primary text-decoration-none fw-bold"
                    onclick="addAttachmentRow()">
                    <i class="fas fa-plus-circle me-1"></i>Add File
                  </button>
                </div>
              </div>
            </div>
            <div class="modal-footer border-0 pt-0">
              <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Close</button>
              <button type="button" class="btn btn-primary rounded-pill px-4 fw-bold"
                onclick="saveTicketWorkUpdate()">Save
                Progress</button>
            </div>
          </form>
        </div>
      </div>


      <script>
        // Inject directly from JSP model
        window.ticketId = '${ticketId}';

        // Normalize: if EL injected empty or "null", set to actual null
        if (!window.ticketId || window.ticketId === 'null') {
          window.ticketId = null;
        }
      </script>


      <style>
        /* Dashboard Styling Essentials */
        :root {
          --primary-soft: #eef2ff;
          --primary-main: #4f46e5;
        }

        .bg-soft-primary {
          background-color: var(--primary-soft);
        }

        .ls-1 {
          letter-spacing: 1px;
        }

        .x-small {
          font-size: 0.7rem;
        }

        .icon-box {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .avatar-sm {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 0.8rem;
          font-weight: bold;
        }

        /* Activity Timeline Look */
        .activity-timeline {
          list-group-style: none;
          padding-left: 1rem;
          border-left: 2px solid #f1f3f5;
          margin-left: 0.5rem;
        }

        .activity-timeline .list-group-item {
          border: none;
          background: transparent;
          padding: 0 0 1.5rem 1.5rem;
          position: relative;
        }

        .activity-timeline .list-group-item::before {
          content: '';
          position: absolute;
          left: -1.45rem;
          top: 0.2rem;
          width: 12px;
          height: 12px;
          background: white;
          border: 2px solid var(--primary-main);
          border-radius: 50%;
        }

        /* Table Hover */
        #tasksTable tbody tr {
          transition: all 0.2s;
          border-bottom: 1px solid #f8f9fa;
        }

        #tasksTable tbody tr:hover {
          background-color: #fcfcff;
          transform: scale(1.002);
        }
      </style>