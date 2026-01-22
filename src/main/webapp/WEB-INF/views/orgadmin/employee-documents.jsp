<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String empId = request.getParameter("empId");
%>

<div class="container mt-4">

    <h3>Employee Documents</h3>
    <p><b>Employee ID:</b> <%= empId %></p>

    <input type="hidden" id="empId" value="<%= empId %>" />

    <div class="card mt-3">
        <div class="card-body">

            <div class="row">

                <div class="col-md-6">
                    <label>Document Type</label>
                    <select class="form-control" id="documentType">
                        <option value="">Select</option>
                        <option>Aadhar</option>
                        <option>PAN</option>
                        <option>Resume</option>
                        <option>Offer</option>
                        <option>Agreement</option>
                        <option>Other</option>
                    </select>
                </div>

                <div class="col-md-6">
                    <label>Document URL (Google Drive / Dropbox / Link)</label>
                    <input type="text" class="form-control" id="externalUrl">
                </div>

                <div class="col-md-6 mt-3">
                    <label>Remove Server File?</label>
                    <select id="removeFile" class="form-control">
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                    </select>
                </div>

            </div>

            <button class="btn btn-primary mt-3" id="btnSaveDoc">
                Save Document
            </button>

        </div>
    </div>

    <hr>

    <h4>Uploaded Documents</h4>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Document Type</th>
            <th>URL</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody id="docTableBody"></tbody>
    </table>

</div>

<script src="/assets/js/employee-documents.js"></script>
