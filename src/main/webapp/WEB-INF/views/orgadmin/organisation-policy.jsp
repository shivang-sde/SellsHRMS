<%@ page contentType="text/html;charset=UTF-8" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <c:set var="pageTitle" value="Organisation Policy" />
    <c:set var="pageScript" value="orgadmin/organisation-policy" />
    <c:set var="pageStyle" value="organisation-policy" />

    <div class="container-fluid mt-4">

      <!-- Page Header -->
      <div class="policy-page-header mb-4">
        <h4 class="fw-bold"><i class="fa fa-file-contract me-2 text-primary"></i>Organisation Policy</h4>
        <p class="text-muted text-wrap" style="max-width: 600px;">Configure your organisation's financial year, work hours, attendance, and leave policies.</p>
      </div>

      <!-- No Policy Alert -->
      <div class="row">
        <div class="col-12">
          <div id="noPolicyAlert" class="alert policy-alert d-none" role="alert">
            <div class="alert-icon">
              <i class="fa fa-info-circle"></i>
            </div>
            <div>
              <strong>No policy found.</strong> Your organisation doesn't have a policy configured yet. Create one to
              get started.
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

        <div class="card-body p-4 p-md-5">
          <form id="organisationPolicyForm">
            <input type="hidden" id="policyId">

            <!-- ═══════ Financial Year Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-calendar-alt me-2"></i>Financial Year</h6>
                  <p class="text-muted small mb-0">Set the start date for your organisation's financial year</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-6">
                  <label class="form-label"><i class="fa fa-calendar me-1 text-secondary"></i>Start Month</label>
                  <select id="financialYearStartMonth" class="form-select" required disabled>
                    <option value="" disabled selected>Select Month...</option>
                    <option value="1">1 - January</option>
                    <option value="2">2 - February</option>
                    <option value="3">3 - March</option>
                    <option value="4">4 - April</option>
                    <option value="5">5 - May</option>
                    <option value="6">6 - June</option>
                    <option value="7">7 - July</option>
                    <option value="8">8 - August</option>
                    <option value="9">9 - September</option>
                    <option value="10">10 - October</option>
                    <option value="11">11 - November</option>
                    <option value="12">12 - December</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label"><i class="fa fa-calendar-day me-1 text-secondary"></i>Start Day</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="financialYearStartDay" class="form-control" min="1" max="31" required
                      disabled>
                    <span class="input-group-text text-muted">Day</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Leave Year Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-calendar-check me-2"></i>Leave Year</h6>
                  <p class="text-muted small mb-0">Define when the leave cycle begins for balance calculations</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-6">
                  <label class="form-label"><i class="fa fa-calendar me-1 text-secondary"></i>Start Month</label>
                  <select id="leaveYearStartMonth" class="form-select" required disabled>
                    <option value="" disabled selected>Select Month...</option>
                    <option value="1">1 - January</option>
                    <option value="2">2 - February</option>
                    <option value="3">3 - March</option>
                    <option value="4">4 - April</option>
                    <option value="5">5 - May</option>
                    <option value="6">6 - June</option>
                    <option value="7">7 - July</option>
                    <option value="8">8 - August</option>
                    <option value="9">9 - September</option>
                    <option value="10">10 - October</option>
                    <option value="11">11 - November</option>
                    <option value="12">12 - December</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label"><i class="fa fa-calendar-day me-1 text-secondary"></i>Start Day</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="leaveYearStartDay" class="form-control" min="1" max="31" required disabled>
                    <span class="input-group-text text-muted">Day</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Work Hours Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-business-time me-2"></i>Work Hours & Office Timing</h6>
                  <p class="text-muted small mb-0">Standard working hours and office schedule configuration</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-6 col-lg-3">
                  <label class="form-label" for="officeStartTime"><i class="fa fa-clock me-1 text-secondary"></i>Office Timing</label>
                  <div class="office-timing-group">
                    <input type="time" name="officeStartTime" id="officeStartTime" class="form-control custom-time">
                    <span class="timing-separator">to</span>
                    <input type="time" name="officeClosedTIme" id="officeClosedTime" class="form-control custom-time">
                  </div>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label"><i class="fa fa-hourglass-half me-1 text-secondary"></i>Standard Daily Hours</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="standardDailyHours" class="form-control" step="0.1" min="0" required
                      disabled>
                    <span class="input-group-text text-muted">Hrs</span>
                  </div>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label"><i class="fa fa-calendar-week me-1 text-secondary"></i>Weekly Hours</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="weeklyHours" class="form-control" step="0.1" min="0" required disabled>
                    <span class="input-group-text text-muted">Hrs</span>
                  </div>
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label"><i class="fa fa-calendar-alt me-1 text-secondary"></i>Min. Monthly Hours</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="minMonthlyHours" class="form-control" step="0.1" min="0" required disabled>
                    <span class="input-group-text text-muted">Hrs</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-calendar-day me-2"></i>Week Off</h6>
                  <p class="text-muted small mb-0">Select weekly off days</p>
                </div>
              </div>

              <div class="week-off-container mt-4">
                <div class="d-flex flex-wrap gap-3">
                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-sun" value="SUNDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-sun">Sun</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-mon" value="MONDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-mon">Mon</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-tue" value="TUESDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-tue">Tue</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-wed" value="WEDNESDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-wed">Wed</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-thu" value="THURSDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-thu">Thu</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-fri" value="FRIDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-fri">Fri</label>

                  <input type="checkbox" class="btn-check week-off-checkbox" id="wo-sat" value="SATURDAY"
                    autocomplete="off" disabled>
                  <label class="btn btn-outline-primary rounded-pill px-4" for="wo-sat">Sat</label>
                </div>
              </div>
            </div>

            <!-- ═══════ Time & Grace Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-hourglass-half me-2"></i>Time & Grace Periods</h6>
                  <p class="text-muted small mb-0">Auto punch-out and grace minutes for late check-in / early check-out</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-user-clock me-1 text-secondary"></i>Auto Punch Out Time</label>
                  <input type="time" id="autoPunchOutTime" class="form-control custom-time" required disabled>
                </div>
                <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-running me-1 text-secondary"></i>Late Grace Limit</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="lateGraceMinutes" class="form-control" min="0" required disabled>
                    <span class="input-group-text text-muted">Mins</span>
                  </div>
                </div>
                <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-door-open me-1 text-secondary"></i>Early Out Grace</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="earlyOutGraceMinutes" class="form-control" min="0" required disabled>
                    <span class="input-group-text text-muted">Mins</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Policy Toggles Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-sliders-h me-2"></i>Policy Toggles</h6>
                  <p class="text-muted small mb-0">Enable or disable specific policies for your organisation</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card d-flex align-items-center w-100">
                    <div class="form-check form-switch m-0 p-0 d-flex align-items-center flex-nowrap gap-2 w-100">
                      <input class="form-check-input flex-shrink-0 m-0" type="checkbox" role="switch"
                        id="overtimeAllowed" disabled>
                      <label class="form-check-label mb-0 text-nowrap" for="overtimeAllowed"><i class="fa fa-user-plus me-1 text-secondary"></i>Overtime Allowed</label>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card d-flex align-items-center w-100">
                    <div class="overtime-multiplier-wrap w-100">
                      <div class="d-flex w-100 justify-content-between align-items-center flex-nowrap gap-2">
                        <label class="form-label mb-0 text-nowrap" for="overtimeMultiplier"><i class="fa fa-times-circle me-1 text-secondary"></i>Overtime Multiplier</label>
                        <div class="input-group input-group-sm m-0 flex-nowrap" style="width: 100px;">
                          <span class="input-group-text">x</span>
                          <input type="number" id="overtimeMultiplier" class="form-control text-center px-1" step="0.1"
                            min="0" disabled>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- <div class="col-md-6 col-lg-4">
                  <div class="toggle-card">
                    <input class="form-check-input" type="checkbox" role="switch" id="flexibleHourModelEnabled"
                      disabled>
                    <label class="form-check-label" for="flexibleHourModelEnabled"><i class="fa fa-people-arrows me-1 text-secondary"></i>Flexible Hour Model</label>
                  </div>
                </div> -->
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card d-flex align-items-center w-100">
                    <div class="form-check form-switch m-0 p-0 d-flex align-items-center flex-nowrap gap-2 w-100">
                      <input class="form-check-input flex-shrink-0 m-0" type="checkbox" role="switch"
                        id="carryForwardEnabled" disabled>
                      <label class="form-check-label mb-0 text-nowrap" for="carryForwardEnabled"><i class="fa fa-share me-1 text-secondary"></i>Carry Forward Enabled</label>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 col-lg-4">
                  <div class="toggle-card d-flex align-items-center w-100">
                    <div class="form-check form-switch m-0 p-0 d-flex align-items-center flex-nowrap gap-2 w-100">
                      <input class="form-check-input flex-shrink-0 m-0" type="checkbox" role="switch"
                        id="encashmentEnabled" disabled>
                      <label class="form-check-label mb-0 text-nowrap" for="encashmentEnabled"><i class="fa fa-money-bill-wave me-1 text-secondary"></i>Encashment Enabled</label>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Payroll & Salary Cycle Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-money-check-alt me-2"></i>Payroll & Salary Cycle</h6>
                  <p class="text-muted small mb-0">Configure the default salary processing timeline</p>
                </div>
              </div>
              <div class="row g-4">
                <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-calendar-check me-1 text-secondary"></i>Cycle Start Day</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="salaryCycleStartDay" class="form-control" min="1" max="31" required
                      disabled>
                    <span class="input-group-text text-muted">of month</span>
                  </div>
                </div>
                <!-- <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-stopwatch me-1 text-secondary"></i>Cycle Duration</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="cycleDuration" class="form-control" min="1" max="31" required disabled>
                    <span class="input-group-text text-muted">Days</span>
                  </div>
                </div> -->
                <div class="col-md-4">
                  <label class="form-label"><i class="fa fa-file-invoice-dollar me-1 text-secondary"></i>Payslip Offset</label>
                  <div class="input-group flex-nowrap">
                    <input type="number" id="payslipGenerationOffsetDays" class="form-control" min="0" required
                      disabled>
                    <span class="input-group-text text-muted">Days</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- ═══════ Additional Notes Section ═══════ -->
            <div class="policy-section mb-5">
              <div class="policy-section-header border-bottom pb-3 mb-4">
                <div class="w-100">
                  <h6 class="d-flex align-items-center text-primary mb-1"><i class="fa fa-sticky-note me-2"></i>Additional Notes</h6>
                  <p class="text-muted small mb-0">Any extra policy details or reminders</p>
                </div>
              </div>
              <div class="row">
                <div class="col-12">
                  <textarea id="additionalNotes" class="form-control" rows="3" disabled></textarea>
                </div>
              </div>
            </div>

            <!-- Action Bar -->
            <div class="d-flex justify-content-between align-items-center mt-5 pt-4 border-top">
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