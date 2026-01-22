<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Organisation Policy" />
<c:set var="pageScript" value="orgadmin-organisation-policy" />

<div class="container-fluid mt-4">
  <div class="row">
    <div class="col-12">
      <div id="noPolicyAlert" class="alert alert-info d-none" role="alert">
        <i class="fa fa-info-circle me-1"></i>
        No organisation policy found. Please create one to proceed.
        <button id="createPolicyBtn" class="btn btn-sm btn-primary ms-2">
          <i class="fa fa-plus"></i> Create Policy
        </button>
      </div>
    </div>
  </div>

  <div class="card shadow-sm border-0">
    <div class="card-header bg-light d-flex justify-content-between align-items-center">
      <h5 class="m-0">Organisation Policy</h5>
      <button id="editPolicyBtn" class="btn btn-primary">
        <i class="fa fa-edit"></i> Edit Policy
      </button>
    </div>

    <div class="card-body">
      <form id="organisationPolicyForm" class="row g-3">
        <input type="hidden" id="policyId">

        <!-- Financial Year -->
        <div class="col-md-3">
          <label class="form-label">Financial Year Start Month</label>
          <input type="number" id="financialYearStartMonth" class="form-control" min="1" max="12" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Financial Year Start Day</label>
          <input type="number" id="financialYearStartDay" class="form-control" min="1" max="31" required disabled>
        </div>

        <!-- Leave Year -->
        <div class="col-md-3">
          <label class="form-label">Leave Year Start Month</label>
          <input type="number" id="leaveYearStartMonth" class="form-control" min="1" max="12" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Leave Year Start Day</label>
          <input type="number" id="leaveYearStartDay" class="form-control" min="1" max="31" required disabled>
        </div>

        <!-- Work hours -->
        <div class="col-md-3">
          <label class="form-label">Standard Daily Hours</label>
          <input type="number" id="standardDailyHours" class="form-control" step="0.1" min="0" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Weekly Hours</label>
          <input type="number" id="weeklyHours" class="form-control" step="0.1" min="0" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Minimum Monthly Hours</label>
          <input type="number" id="minMonthlyHours" class="form-control" step="0.1" min="0" required disabled>
        </div>

        <!-- Time & Grace -->
        <div class="col-md-3">
          <label class="form-label">Auto Punch Out Time</label>
          <input type="time" id="autoPunchTime" class="form-control" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Late Grace (Minutes)</label>
          <input type="number" id="lateGraceMinutes" class="form-control" min="0" required disabled>
        </div>
        <div class="col-md-3">
          <label class="form-label">Early Out Grace (Minutes)</label>
          <input type="number" id="earlyOutGraceMinutes" class="form-control" min="0" required disabled>
        </div>

        <!-- Boolean Policies -->
        <div class="col-md-3 form-check mt-4">
          <input class="form-check-input" type="checkbox" id="overtimeAllowed" disabled>
          <label class="form-check-label">Overtime Allowed</label>
        </div>
        <div class="col-md-3">
          <label class="form-label">Overtime Multiplier</label>
          <input type="number" id="overtimeMultiplier" class="form-control" step="0.1" min="0" disabled>
        </div>

        <div class="col-md-3 form-check mt-4">
          <input class="form-check-input" type="checkbox" id="flexibleHourModelEnabled" disabled>
          <label class="form-check-label">Flexible Hour Model</label>
        </div>
        <div class="col-md-3 form-check mt-4">
          <input class="form-check-input" type="checkbox" id="carryForwardEnabled" disabled>
          <label class="form-check-label">Carry Forward Enabled</label>
        </div>
        <div class="col-md-3 form-check mt-4">
          <input class="form-check-input" type="checkbox" id="encashmentEnabled" disabled>
          <label class="form-check-label">Encashment Enabled</label>
        </div>

        <!-- Additional Notes -->
        <div class="col-12">
          <label class="form-label">Additional Notes</label>
          <textarea id="additionalNotes" class="form-control" rows="3" disabled></textarea>
        </div>

        <!-- Buttons -->
        <div class="col-12 text-end">
          <button type="submit" id="savePolicyBtn" class="btn btn-success d-none">
            <i class="fa fa-save"></i> Save Policy
          </button>
        </div>

        <!-- Last Updated -->
        <div class="col-12 text-muted text-end" id="lastUpdatedInfo" style="font-size: 0.9rem; display: none;">
          <i class="fa fa-clock me-1"></i> Last updated:
          <span id="lastUpdatedTime"></span>
        </div>
      </form>
    </div>
  </div>
</div>

