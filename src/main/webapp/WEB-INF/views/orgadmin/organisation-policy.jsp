<%@ page contentType="text/html;charset=UTF-8" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <c:set var="pageTitle" value="Organisation Policy" />
    <c:set var="pageScript" value="orgadmin/organisation-policy" />
    <c:set var="pageStyle" value="organisation-policy" />

    <div class="container-fluid mt-4">

      <!-- Page Header -->
      <div class="policy-page-header">
        <h4><i class="fa fa-file-contract me-2" style="color:#0284c7"></i>Organisation Policy</h4>
        <p>Configure your organisation's financial year, work hours, attendance, and leave policies.</p>
      </div>

      <!-- No Policy Alert -->
      <div class="row">
        <div class="col-12">
          <div id="noPolicyAlert" class="alert policy-alert d-none" role="alert">
            <div class="alert-icon">
              <i class="fa fa-info-circle"></i>
            </div>
            <div>
              <strong>No policy found.</strong> Your organisation doesn't have a policy configured yet. Create one to get started.
              <button id="createPolicyBtn" class="btn btn-sm btn-primary ms-2" style="border-radius:8px">
                <i class="fa fa-plus"></i> Create Policy
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Policy Card -->
      <div class="card policy-card shadow-sm border-0">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="m-0"><i class="fa fa-cog"></i> Policy Settings</h5>
          <button id="editPolicyBtn" class="btn btn-edit-policy">
            <i class="fa fa-edit"></i> Edit Policy
          </button>
        </div>

        <div class="card-body">
          <form id="organisationPolicyForm">
            <input type="hidden" id="policyId">

            <!-- ═══════ Financial Year Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon financial">
                  <i class="fa fa-calendar-alt"></i>
                </div>
                <div>
                  <h6>Financial Year</h6>
                  <p>Set the start date for your organisation's financial year</p>
                </div>
              </div>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Start Month</label>
                  <input type="number" id="financialYearStartMonth" class="form-control" min="1" max="12" required disabled>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Start Day</label>
                  <input type="number" id="financialYearStartDay" class="form-control" min="1" max="31" required disabled>
                </div>
              </div>
            </div>

            <!-- ═══════ Leave Year Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon leave">
                  <i class="fa fa-calendar-check"></i>
                </div>
                <div>
                  <h6>Leave Year</h6>
                  <p>Define when the leave cycle begins for balance calculations</p>
                </div>
              </div>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Start Month</label>
                  <input type="number" id="leaveYearStartMonth" class="form-control" min="1" max="12" required disabled>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Start Day</label>
                  <input type="number" id="leaveYearStartDay" class="form-control" min="1" max="31" required disabled>
                </div>
              </div>
            </div>

            <!-- ═══════ Work Hours Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon work">
                  <i class="fa fa-business-time"></i>
                </div>
                <div>
                  <h6>Work Hours & Office Timing</h6>
                  <p>Standard working hours and office schedule configuration</p>
                </div>
              </div>
              <div class="row g-3">
                <div class="col-md-6 col-lg-3">
                  <label class="form-label" for="officeStartTime">Office Timing</label>
                  <div class="office-timing-group">
                    <input type="time" name="officeStartTime" id="officeStartTime" class="form-control">
                    <span class="timing-separator">to</span>
                    <input type="time" name="officeClosedTIme" id="officeClosedTime" class="form-control">
                  </div>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label">Standard Daily Hours</label>
                  <input type="number" id="standardDailyHours" class="form-control" step="0.1" min="0" required disabled>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label">Weekly Hours</label>
                  <input type="number" id="weeklyHours" class="form-control" step="0.1" min="0" required disabled>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label">Min. Monthly Hours</label>
                  <input type="number" id="minMonthlyHours" class="form-control" step="0.1" min="0" required disabled>
                </div>
              </div>
            </div>

            <!-- ═══════ Time & Grace Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon grace">
                  <i class="fa fa-hourglass-half"></i>
                </div>
                <div>
                  <h6>Time & Grace Periods</h6>
                  <p>Auto punch-out and grace minutes for late check-in / early check-out</p>
                </div>
              </div>
              <div class="row g-3">
                <div class="col-md-4">
                  <label class="form-label">Auto Punch Out Time</label>
                  <input type="time" id="autoPunchTime" class="form-control" required disabled>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Late Grace (Minutes)</label>
                  <input type="number" id="lateGraceMinutes" class="form-control" min="0" required disabled>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Early Out Grace (Min.)</label>
                  <input type="number" id="earlyOutGraceMinutes" class="form-control" min="0" required disabled>
                </div>
              </div>
            </div>

            <!-- ═══════ Policy Toggles Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon toggles">
                  <i class="fa fa-sliders-h"></i>
                </div>
                <div>
                  <h6>Policy Toggles</h6>
                  <p>Enable or disable specific policies for your organisation</p>
                </div>
              </div>
              <div class="row g-3">
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <input class="form-check-input" type="checkbox" role="switch" id="overtimeAllowed" disabled>
                    <label class="form-check-label" for="overtimeAllowed">Overtime Allowed</label>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <div class="overtime-multiplier-wrap">
                      <label class="form-label" for="overtimeMultiplier">Overtime Multiplier</label>
                      <input type="number" id="overtimeMultiplier" class="form-control" step="0.1" min="0" disabled>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <input class="form-check-input" type="checkbox" role="switch" id="flexibleHourModelEnabled" disabled>
                    <label class="form-check-label" for="flexibleHourModelEnabled">Flexible Hour Model</label>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <input class="form-check-input" type="checkbox" role="switch" id="carryForwardEnabled" disabled>
                    <label class="form-check-label" for="carryForwardEnabled">Carry Forward Enabled</label>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <input class="form-check-input" type="checkbox" role="switch" id="encashmentEnabled" disabled>
                    <label class="form-check-label" for="encashmentEnabled">Encashment Enabled</label>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Additional Notes Section ═══════ -->
            <div class="policy-section">
              <div class="policy-section-header">
                <div class="section-icon notes">
                  <i class="fa fa-sticky-note"></i>
                </div>
                <div>
                  <h6>Additional Notes</h6>
                  <p>Any extra policy details or reminders</p>
                </div>
              </div>
              <div class="row">
                <div class="col-12">
                  <textarea id="additionalNotes" class="form-control" rows="3" disabled></textarea>
                </div>
              </div>
            </div>

            <!-- Action Bar -->
            <div class="d-flex justify-content-between align-items-center mt-2">
              <!-- Last Updated -->
              <div class="text-muted" id="lastUpdatedInfo" style="font-size: 0.85rem; display: none;">
                <i class="fa fa-clock me-1"></i> Last updated:
                <span id="lastUpdatedTime"></span>
              </div>

              <!-- Save Button -->
              <div class="ms-auto">
                <button type="submit" id="savePolicyBtn" class="btn btn-save-policy d-none">
                  <i class="fa fa-save"></i> Save Policy
                </button>
              </div>
            </div>

          </form>
        </div>
      </div>
    </div>