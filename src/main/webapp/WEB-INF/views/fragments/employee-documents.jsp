<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<input type="hidden" id="employeeId" value="${employeeId != null ? employeeId : ''}"/>

<div class="card mt-3">
  <div class="card-header d-flex justify-content-between align-items-center">
    <strong>Documents</strong>
    <button id="btnOpenDocModal" class="btn btn-sm btn-primary">Add / Upload</button>
  </div>
  <div class="card-body">
    <div id="docAlert"></div>

    <table class="table table-sm">
      <thead>
        <tr>
          <th>Type</th>
          <th>File</th>
          <th>External</th>
          <th>Uploaded At</th>
          <th></th>
        </tr>
      </thead>
      <tbody id="documentsTbody">
        <!-- populated by JS -->
      </tbody>
    </table>
  </div>
</div>

<!-- Modal -->
<div class="modal" id="docModal" tabindex="-1" style="display:none;">
  <div class="modal-dialog">
    <div class="modal-content p-2">
      <div class="modal-header">
        <h5 class="modal-title">Upload Document</h5>
        <button type="button" class="btn-close" onclick="closeDocModal()"></button>
      </div>
      <div class="modal-body">
        <form id="docUploadForm">
          <input type="hidden" id="modalEmployeeId" />
          <div class="mb-2">
            <label>Document Type</label>
            <select id="modalDocType" class="form-select">
              <option value="RESUME">Resume</option>
              <option value="OFFER">Offer Letter</option>
              <option value="JOINING">Joining Letter</option>
              <option value="AGREEMENT">Contract/Agreement</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div class="mb-2">
            <label>File</label>
            <input id="modalFile" type="file" class="form-control" accept=".pdf,.doc,.docx,.jpg,.png"/>
          </div>
          <div class="mb-2">
            <label>Or external link (Dropbox/Google)</label>
            <input id="modalExternal" type="url" class="form-control" placeholder="https://"/>
          </div>

          <div id="modalProgress" style="display:none;">
            <div class="progress">
              <div id="modalProgressBar" class="progress-bar" role="progressbar" style="width:0%">0%</div>
            </div>
          </div>

          <div class="form-check mt-2">
            <input id="modalRemoveFile" class="form-check-input" type="checkbox"/>
            <label class="form-check-label">Remove existing server file (keep only external link)</label>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" onclick="closeDocModal()">Cancel</button>
        <button id="modalSaveBtn" class="btn btn-primary">Save</button>
      </div>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/employee/employee-documents.js"></script>
