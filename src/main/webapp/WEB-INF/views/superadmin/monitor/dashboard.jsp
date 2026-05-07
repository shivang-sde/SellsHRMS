<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="container-fluid p-3">
        <div class="row mb-4">
            <div class="col-12">
                <h4 class="fw-bold">Global URL Monitor Dashboard</h4>
                <p class="text-muted">Super Admin view - Monitor all organisations</p>
            </div>
        </div>

        <div class="card border-0 shadow-sm">
            <div class="card-body text-center py-5">
                <i class="fa-solid fa-globe fa-4x text-muted mb-3"></i>
                <h5>Global Monitoring Coming Soon</h5>
                <p class="text-muted">Cross-organisation URL monitoring dashboard is under development.</p>
                <a href="${pageContext.request.contextPath}/superadmin/monitor/urls" class="btn btn-primary">
                    View All URLs
                </a>
            </div>
        </div>
    </div>


    <!-- At the bottom of dashboard.jsp -->
    <script src="${pageContext.request.contextPath}/js/monitor/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/monitor/monitorAPI.js"></script>
    <script src="${pageContext.request.contextPath}/js/monitor/dashboard.js"></script>