<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Departments" />

<div class="container-fluid">

    <div class="card shadow-sm mt-3">
        <div class="card-header d-flex justify-content-between">
            <h5 class="mb-0">Departments</h5>
            <button class="btn btn-primary btn-sm" data-open-dept-create>
                + Add Department
            </button>
        </div>

        <div class="card-body p-0">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="departmentTableBody">
                <tr><td colspan="3" class="text-center">Loading...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- MODAL: DEPARTMENT -->
<div class="modal fade" id="deptModal">
    <div class="modal-dialog">
        <form id="deptModalForm" class="modal-content">
            <div class="modal-header">
                <h5 id="deptModalTitle" class="modal-title">Add Department</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <input type="hidden" id="deptId">

                <div class="mb-3">
                    <label>Name</label>
                    <input id="deptNameInput" class="form-control" required>
                </div>

                <div class="mb-3">
                    <label>Description</label>
                    <textarea id="deptDescInput" class="form-control"></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button class="btn btn-primary" type="submit">Save</button>
            </div>
        </form>
    </div>
</div>

<script  src="/assets/js/dept.js"></script>
