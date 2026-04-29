<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <div class="card hrms-card shadow-sm">
                <div class="card-header bg-primary-hrms d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fa fa-bell me-2"></i>Notification Preferences</h5>
                </div>

                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="preferencesTable">
                            <thead class="table-light">
                                <tr>
                                    <th style="width: 250px">Event</th>
                                    <th>Module</th>
                                    <th>Description</th>
                                    <th class="text-center" style="width: 80px">Actions</th>
                                </tr>
                            </thead>
                            <tbody id="preferencesBody">
                                <!-- Populated by JS -->
                                <tr>
                                    <td colspan="${5}" class="text-center py-4">
                                        <div class="spinner-border text-primary" role="status"></div>
                                        <p class="text-muted mt-2 mb-0">Loading preferences...</p>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="card-footer text-muted small">
                    <i class="fa fa-info-circle me-1"></i>
                    Toggle switches to enable/disable email notifications for each event and role combination.
                </div>
            </div>


            <!-- Loading Overlay -->
            <div id="loadingOverlay" class="loading-overlay d-none">
                <div class="spinner-border text-primary" role="status"></div>
            </div>




            <%-- ✅ Expose TargetRole enum values to JavaScript --%>
                <script>

                    // ✅ Also expose APP config if not already global
                    window.APP = window.APP || {
                        CONTEXT_PATH: '${pageContext.request.contextPath}',
                        ORG_ID: '${orgId}'  // Make sure 'orgId' is in your model
                    };
                </script>