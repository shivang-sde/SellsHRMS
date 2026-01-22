<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- jQuery first -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<!-- Bootstrap & AdminLTE -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/admin-lte@4.0.0/dist/js/adminlte.min.js"></script>

<!-- DataTables -->
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>

<!-- SweetAlert2 -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.js"></script>

<script>
    function logout(e) {
        e.preventDefault();
        $.post("/api/auth/logout")
            .done(function(){ window.location.href = "/login"; })
            .fail(function(){ Swal.fire('Logout Failed', 'Please try again', 'error'); });
    }

    // helper to show toast
    function showToast(icon, title){
        Swal.fire({ toast: true, position: 'top-end', icon: icon, title: title, showConfirmButton:false, timer:2000 });
    }
</script>

<!-- load page-specific script if set by JSP content -->
<c:if test="${not empty pageScript}">
    <script src="/js/${pageScript}.js"></script>
</c:if>
