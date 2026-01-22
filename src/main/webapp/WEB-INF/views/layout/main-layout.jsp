<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${fn:escapeXml(pageTitle)} | SellsHRMS</title>
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css"> -->
    <!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css"> -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/toast.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/payroll.css">

</head>

<body class="hrms-app">
    <c:if test="${not empty sessionScope.ORG_ID}">
        <input type="hidden" id="globalOrgId" value="${sessionScope.ORG_ID}">
    </c:if>
    <input type="hidden" id="globalRole" value="${sessionScope.SYSTEM_ROLE}">
    <input type="hidden" id="globalUserId" value="${sessionScope.USER_ID}">

    <div class="hrms-wrapper">
        <c:import url="/WEB-INF/views/layout/sidebar.jsp"/>
        <div class="sidebar-overlay" id="sidebarOverlay"></div>

       <div id="toast-container" class="toast-container"></div>

        <div class="hrms-main">
            <c:import url="/WEB-INF/views/layout/header.jsp"/>

            <main class="hrms-content">
                <div class="container-fluid py-3">
                    <c:choose>
                        <c:when test="${not empty contentPage}">
                            <c:import url="/WEB-INF/views/${contentPage}.jsp" />
                        </c:when>
                        <c:otherwise>
                            <div class="card p-5 shadow-sm text-center">
                                <i class="fa-solid fa-house-user fa-3x text-primary mb-3"></i>
                                <h3>Welcome back, ${fn:escapeXml(sessionScope.USER_NAME)}</h3>
                                <p class="text-muted">Select a module from the sidebar to begin.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>

            <c:import url="/WEB-INF/views/layout/footer.jsp"/>
        </div>
    </div>

    <div id="errorModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon"></div>
            <h3 id="errorTitle" class="fw-bold"></h3>
            <p id="errorMessage" class="text-muted"></p>
            <div class="modal-actions">
                <button id="errorConfirm" class="btn-primary-hrms">Confirm</button>
                <button id="errorCancel" class="btn-secondary-hrms">Cancel</button>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>


    <script>
    // Global App Context - Shared across all HRMS modules
    window.APP = {
        USER_NAME: "${fn:escapeXml(sessionScope.USER_NAME)}",
        USER_ID: "${sessionScope.USER_ID}",
        EMPLOYEE_ID: "${sessionScope.EMP_ID}",
        ORG_ID: "${sessionScope.ORG_ID}",
        ROLE: "${sessionScope.SYSTEM_ROLE}",
        PERMISSIONS: "${sessionScope.PERMISSIONS}",
        LAST_LOGIN: "${sessionScope.LAST_LOGIN}",
        CONTEXT_PATH: "${pageContext.request.contextPath}"
    };

        console.log("%c🔧 SellsHRMS App Context Loaded", "color:#0ea5e9;font-weight:bold;");
        console.table(window.APP);

    </script>

    
        <!-- Utilities (must come before tasks.js) -->
        <script src="${pageContext.request.contextPath}/js/modalUtils.js"></script>
        <script src="${pageContext.request.contextPath}/js/apiClient.js"></script>



        <script src="${pageContext.request.contextPath}/js/utils/global-helper.js"></script>
        <script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
        <c:if test="${not empty pageScript}">
            <script src="${pageContext.request.contextPath}/js/${pageScript}.js"></script>
        </c:if>
</body>
</html>