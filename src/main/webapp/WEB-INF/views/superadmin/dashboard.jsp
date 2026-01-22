<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Super Admin Dashboard" />
<c:set var="pageScript" value="dashboard" />

<div class="row g-3">
  <div class="col-12 col-md-6 col-lg-4">
    <div class="card p-3">
      <div class="d-flex align-items-center justify-content-between">
        <div>
          <h6 class="mb-1">Total Organisations</h6>
          <h3 id="orgCount">...</h3>
        </div>
        <div class="icon-box bg-light p-2 rounded">
          <i class="fa fa-building fa-2x" style="color:var(--hrms-blue)"></i>
        </div>
      </div>
    </div>
  </div>

  <div class="col-12 col-md-6 col-lg-4">
    <div class="card p-3">
      <div class="d-flex align-items-center justify-content-between">
        <div>
          <h6 class="mb-1">Org Admins</h6>
          <h3 id="adminCount">...</h3>
        </div>
        <div class="icon-box bg-light p-2 rounded">
          <i class="fa fa-users fa-2x" style="color:#16a34a"></i>
        </div>
      </div>
    </div>
  </div>
</div>


  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script>
 
  $(function(){
    $.get('/api/superadmin/organisations').done(function(data){ $('#orgCount').text(data.length || 0); }).fail(()=>$('#orgCount').text('N/A'));
    $.get('/api/superadmin/orgadmins').done(function(data){ $('#adminCount').text(data.length || 0); }).fail(()=>$('#adminCount').text('N/A'));
  });
</script>
 