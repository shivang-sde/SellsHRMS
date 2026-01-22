<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card shadow-sm p-4">
  <h4 class="mb-3">
    <i class="fa fa-shield-alt me-2"></i>
    Create / Edit Role
  </h4>

  <form id="roleForm">
    <div class="row mb-3">
      <div class="col-md-6">
        <label class="form-label">Role Name <span class="text-danger">*</span></label>
        <input type="text" name="name" class="form-control" placeholder="e.g., HR, Manager" required>
      </div>
      <div class="col-md-6">
        <label class="form-label">Description</label>
        <input type="text" name="description" class="form-control" placeholder="Short role description">
      </div>
    </div>

    <div class="mb-3">
      <label class="form-label fw-bold">Select Permissions</label>
      <div id="permissionsList" class="row g-2">
        <!-- dynamically loaded permissions -->
      </div>
    </div>

    <div class="d-flex justify-content-end">
      <button type="submit" id="saveBtn" class="btn btn-primary">
        <i class="fa fa-save me-2"></i>Save Role
      </button>
    </div>
  </form>
</div>
