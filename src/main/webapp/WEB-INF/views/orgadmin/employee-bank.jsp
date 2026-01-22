<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String empId = request.getParameter("empId");
%>

<div class="container mt-4">

    <h3>Employee Bank Details</h3>
    <p><b>Employee ID:</b> <%= empId %></p>

    <input type="hidden" id="empId" value="<%= empId %>" />

    <div class="card mt-3">
        <div class="card-body">

            <div class="row">

                <div class="col-md-6">
                    <label>Bank Name</label>
                    <input type="text" class="form-control" id="bankName">
                </div>

                <div class="col-md-6">
                    <label>Account Number</label>
                    <input type="text" class="form-control" id="accountNumber">
                </div>

                <div class="col-md-6 mt-3">
                    <label>IFSC Code</label>
                    <input type="text" class="form-control" id="ifscCode">
                </div>

                <div class="col-md-6 mt-3">
                    <label>Branch</label>
                    <input type="text" class="form-control" id="branch">
                </div>

                <div class="col-md-6 mt-3">
                    <label>Primary Account</label>
                    <select class="form-control" id="isPrimaryAccount">
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                    </select>
                </div>

            </div>

            <button class="btn btn-success mt-3" id="btnSaveBank">
                Save Bank Account
            </button>

        </div>
    </div>

    <hr>

    <h5>Saved Bank Accounts</h5>

    <table class="table table-striped table-bordered">
        <thead>
        <tr>
            <th>Bank</th>
            <th>Account</th>
            <th>IFSC</th</th>
            <th>Branch</th>
            <th>Primary</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody id="bankTableBody"></tbody>
    </table>

</div>

<script src="/assets/js/employee-bank.js"></script>
