<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="leave-types-management">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Leave Types</h2>
      <p class="text-muted mb-0">Configure and manage leave type policies</p>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-outline-secondary" onclick="window.location.href='${pageContext.request.contextPath}/org/leaves'">
        <i class="fa fa-arrow-left me-2"></i>Back to Leaves
      </button>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createLeaveTypeModal">
        <i class="fa fa-plus me-2"></i>Create Leave Type
      </button>
    </div>
  </div>

  <div class="alert alert-info border-0 shadow-sm mb-4">
    <h6 class="fw-bold"><i class="fa fa-lightbulb me-2"></i>How Leave Accrual & Balance Work</h6>
    <ul class="mb-0 small">
      <li>
        <strong>Accrual Method</strong> defines <em>when</em> leaves are credited:
        <ul>
          <li><b>Monthly</b> – Employee earns leave each month (e.g. 2 per month = 24 per year).</li>
          <li><b>Annual</b> – All leave is credited at the start of the leave year.</li>
          <li><b>Pro-Rata</b> – Leave is calculated based on joining date or service period.</li>
          <li><b>None</b> – Used for special types like Comp-Off or Unpaid Leave.</li>
        </ul>
      </li>
      <li>
        <strong>Accrual Rate</strong> is the number of leave days earned per accrual cycle (usually monthly).
      </li>
      <li>
        Even without a cron job, the system automatically calculates leave earned <b>till the current month</b>
        when applying for or approving leave.
      </li>
      <li>
        <strong>Carry Forward</strong> applies only to <b>Monthly Accrual</b> types.
        Limit must not exceed monthly accrual rate.
      </li>
      <li>
        <strong>Annual Limit</strong> is the total leave allowed in a full year.
        For monthly accrual, it’s usually <code>12 × Accrual Rate</code>.
      </li>
      <li>
        <strong>Max Consecutive Days</strong> restricts how many continuous days can be taken at once,
        even if the employee has more balance.
      </li>
      <li>
        <strong>Validity Days</strong> apply only for <b>None</b> accrual types
        (like Comp-Off) and expire after the given number of days.
      </li>
    </ul>
  </div>


  <!-- Leave Types Grid -->
  <div class="row" id="leaveTypesGrid">
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</div>

<!-- Create Leave Type Modal -->
<div class="modal fade" id="createLeaveTypeModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Create Leave Type</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="createLeaveTypeForm">
        <div class="modal-body">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Name <span class="text-danger">*</span></label>
              <input type="text" class="form-control" name="name" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Annual Limit (Days) <span class="text-danger">*</span></label>
              <input type="number" required class="form-control" name="annualLimit" min="0"  >
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" rows="2"></textarea>
          </div>

          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">Accrual Method <span class="text-danger">*</span></label> 
              <select class="form-select" required name="accrualMethod">
                <option value="NONE">None</option>
                <option value="MONTHLY">Monthly</option>
                <option value="ANNUAL">Annual</option>
                <option value="PRO_RATA">Pro Rata</option>
              </select>
              <small class="text-muted d-block" id="accrualHint"></small>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">
                Accrual Rate (per month)
                <i class="fa fa-info-circle text-muted"
                  title="Number of leave days earned per month. Only applicable for Monthly accrual."></i>
              </label>

              <input type="number" step="0.5" class="form-control" name="accrualRate" min="0">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">Applicable Gender <span class="text-danger">*</span></label>
              <select class="form-select" required name="applicableGender">
                <option value="ALL">All</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
              </select>
            </div>
          </div>

          <hr>
          <h6 class="mb-3">Leave Settings</h6>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="isPaid" checked>
                <label class="form-check-label">Is Paid</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="requiresApproval" checked>
                <label class="form-check-label">Requires Approval</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="carryForwardAllowed">
                <label class="form-check-label">Allow Carry Forward</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Carry Forward Limit</label>
              <input type="number" class="form-control" name="carryForwardLimit" min="0">
            </div>
            <small class="text-muted d-block" id="carryForwardHint"></small>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="encashable">
                <label class="form-check-label">Encashable</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="allowHalfDay">
                <label class="form-check-label">Allow Half Day</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="availableDuringProbation">
                <label class="form-check-label">Available During Probation</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="includeHolidaysInLeave">
                <label class="form-check-label">Include Holidays in Leave</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="visibleToEmployees" checked>
                <label class="form-check-label">Visible to Employees</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Max Consecutive Days</label>
              <input type="number" class="form-control" name="maxConsecutiveDays" min="0">
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">
              Validity Days (for special leaves)
              <i class="fa fa-info-circle text-muted"
                title="Used for comp-off or time-sensitive leave. Only applicable when Accrual Method = None."></i>
            </label>

            <input type="number" class="form-control" name="validityDays" min="0">
            <small class="text-muted d-block" id="validityHint"></small>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Create Leave Type</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Leave Type Modal -->
<div class="modal fade" id="editLeaveTypeModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
       <sec:authorize access="hasAuthority('ORG_ADMIN')">
        <h5 class="modal-title">Edit Leave Type</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
       </sec:authorize>
      </div>
      <form id="editLeaveTypeForm">
        <input type="hidden" id="editLeaveTypeId">
        <div class="modal-body">
          <!-- Same fields as create form -->
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Name <span class="text-danger">*</span></label>
              <input type="text" class="form-control" name="name" id="editName" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Annual Limit (Days) <span class="text-danger">*</span></label>
              <input type="number" class="form-control" required name="annualLimit" id="editAnnualLimit" min="0">
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" id="editDescription" rows="2"></textarea>
          </div>

          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">Accrual Method <span class="text-danger">*</span></label>
              <select class="form-select" name="accrualMethod" required id="editAccrualMethod">
                <option value="NONE">None</option>
                <option value="MONTHLY">Monthly</option>
                <option value="ANNUAL">Annual</option>
                <option value="PRO_RATA">Pro Rata</option>
              </select>
              <small class="text-muted d-block" id="accrualHint"></small>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">Accrual Rate</label>
              <input type="number" step="0.5" class="form-control" name="accrualRate" id="editAccrualRate" min="0">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">Applicable Gender <span class="text-danger">*</span></label>
              <select class="form-select" name="applicableGender" required id="editApplicableGender">
                <option value="ALL">All</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
              </select>
            </div>
          </div>

          <hr>
          <h6 class="mb-3">Leave Settings</h6>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="isPaid" id="editIsPaid">
                <label class="form-check-label">Is Paid</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="requiresApproval" id="editRequiresApproval">
                <label class="form-check-label">Requires Approval</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="carryForwardAllowed" id="editCarryForwardAllowed">
                <label class="form-check-label">Allow Carry Forward</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Carry Forward Limit</label>
              <input type="number" class="form-control" name="carryForwardLimit" id="editCarryForwardLimit" min="0">
            </div>
            <small class="text-muted d-block" id="carryForwardHint"></small>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="encashable" id="editEncashable">
                <label class="form-check-label">Encashable</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="allowHalfDay" id="editAllowHalfDay">
                <label class="form-check-label">Allow Half Day</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="availableDuringProbation" id="editAvailableDuringProbation">
                <label class="form-check-label">Available During Probation</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="includeHolidaysInLeave" id="editIncludeHolidaysInLeave">
                <label class="form-check-label">Include Holidays in Leave</label>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" name="visibleToEmployees" id="editVisibleToEmployees">
                <label class="form-check-label">Visible to Employees</label>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Max Consecutive Days</label>
              <input type="number" class="form-control" name="maxConsecutiveDays" id="editMaxConsecutiveDays" min="0">
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Validity Days</label>
            <input type="number" class="form-control" name="validityDays" id="editValidityDays" min="0">
          <small class="text-muted d-block" id="validityHint"></small>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Update Leave Type</button>
        </div>
      </form>
    </div>
  </div>
</div>

<style>
.leave-type-card {
  transition: all 0.3s;
  border: 1px solid #e0e0e0;
}

.leave-type-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.leave-type-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px 8px 0 0;
}

.feature-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  margin: 2px;
  background: rgba(0,0,0,0.1);
}

.modal {
    margin-top: 5vh;
    margin-bottom: 5vh;
}

</style>