<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
            <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

                <div class="card hrms-card shadow-sm">
                    <div
                        class="card-header bg-primary-hrms text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fa fa-envelope me-2"></i>SMTP Configuration</h5>
                        <span class="badge bg-light text-dark" id="configStatus">
                            <i class="fa fa-circle fa-xs me-1"></i>Not Configured
                        </span>
                    </div>

                    <div class="card-body">
                        <form id="smtpConfigForm" class="needs-validation" novalidate>
                            <input type="hidden" id="orgId" value="${currentOrgId}">

                            <div class="row g-3">
                                <!-- SMTP Host & Port -->
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">SMTP Host <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="smtpHost" name="smtpHost"
                                        placeholder="smtp.gmail.com" required>
                                    <div class="invalid-feedback">Please enter SMTP host</div>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Port <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="smtpPort" name="smtpPort"
                                        placeholder="587" value="587" min="1" max="65535" required>
                                    <div class="invalid-feedback">Valid port required</div>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">&nbsp;</label>
                                    <div class="form-check form-switch mt-2">
                                        <input class="form-check-input" type="checkbox" id="useTls" checked>
                                        <label class="form-check-label" for="useTls">Use TLS/SSL</label>
                                    </div>
                                </div>

                                <!-- Credentials -->
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Username <span
                                            class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="username" name="username"
                                        placeholder="noreply@yourorg.com" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Password <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="password" name="password"
                                            placeholder="••••••••" required>
                                        <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                            <i class="fa fa-eye"></i>
                                        </button>
                                    </div>
                                    <small class="text-muted">Password is encrypted before storage</small>
                                </div>

                                <!-- From Address -->
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">From Email <span
                                            class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="fromEmail" name="fromEmail"
                                        placeholder="notifications@yourorg.com" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">From Name</label>
                                    <input type="text" class="form-control" id="fromName" name="fromName"
                                        placeholder="Your Organization">
                                </div>

                                <!-- Rate Limits -->
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Daily Limit</label>
                                    <input type="number" class="form-control" id="dailyLimit" name="dailyLimit"
                                        value="1000" min="1">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Hourly Limit</label>
                                    <input type="number" class="form-control" id="hourlyLimit" name="hourlyLimit"
                                        value="100" min="1">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Status</label>
                                    <select class="form-select" id="isActive" name="isActive">
                                        <option value="true" selected>Active</option>
                                        <option value="false">Inactive</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="d-flex gap-2 mt-4 pt-3 border-top">
                                <button type="button" class="btn btn-outline-primary" id="testSmtpBtn">
                                    <i class="fa fa-plug me-1"></i>Test Connection
                                </button>
                                <button type="submit" class="btn btn-primary-hrms">
                                    <i class="fa fa-save me-1"></i>Save Configuration
                                </button>
                                <button type="button" class="btn btn-secondary-hrms" id="resetBtn">
                                    <i class="fa fa-undo me-1"></i>Reset
                                </button>
                            </div>
                        </form>

                        <!-- Test Result Area -->
                        <div id="testResult" class="mt-3 d-none">
                            <div class="alert alert-dismissible fade show" role="alert">
                                <span id="testResultIcon"></span>
                                <strong id="testResultTitle"></strong>
                                <p class="mb-0 mt-1" id="testResultMessage"></p>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </div>

                        <!-- Current Config Summary -->
                        <div class="mt-4">
                            <h6 class="fw-semibold mb-2"><i class="fa fa-info-circle me-1"></i>Current Configuration
                            </h6>
                            <div class="table-responsive">
                                <table class="table table-sm table-borderless mb-0" id="configSummary">
                                    <tbody>
                                        <tr>
                                            <td class="text-muted w-25">Status:</td>
                                            <td id="summaryStatus">-</td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">Host:</td>
                                            <td id="summaryHost">-</td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">From:</td>
                                            <td id="summaryFrom">-</td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">Daily Sent:</td>
                                            <td id="summarySent">-</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Loading Overlay -->
                <div id="loadingOverlay" class="loading-overlay d-none">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>