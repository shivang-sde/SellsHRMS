<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <!doctype html>
            <html lang="en">

            <head>
                <meta charset="utf-8">
                <title>${fn:escapeXml(pageTitle)} | SellsHRMS</title>
                <meta name="viewport" content="width=device-width,initial-scale=1">

                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;800&display=swap"
                    rel="stylesheet">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
                <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
                <script
                    src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>

                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css" />


                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/toast.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/payroll.css">
                <c:if test="${not empty pageStyle}">
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/${pageStyle}.css">
                </c:if>



            </head>

            <body class="hrms-app">
                <c:if test="${not empty sessionScope.ORG_ID}">
                    <input type="hidden" id="globalOrgId" value="${sessionScope.ORG_ID}">
                </c:if>
                <input type="hidden" id="globalRole" value="${sessionScope.SYSTEM_ROLE}">
                <input type="hidden" id="globalUserId" value="${sessionScope.USER_ID}">
                <sec:authorize access="isAuthenticated()">
                    <input type="hidden" id="globalAuthorities"
                        value="<sec:authentication property='principal.authorities'/>">
                </sec:authorize>


                <div class="hrms-wrapper">
                    <c:import url="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay" id="sidebarOverlay"></div>

                    <div id="toast-container" class="toast-container"></div>

                    <div class="hrms-viewport">
                        <c:import url="/WEB-INF/views/layout/header.jsp" />

                        <div class="hrms-main">
                            <main class="hrms-content">
                                <div class="container-fluid p-0">
                                    <c:choose>
                                        <c:when test="${not empty contentPage}">
                                            <c:import url="/WEB-INF/views/${contentPage}.jsp" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="card border border-slate-200 bg-white"
                                                style="border-radius: var(--radius);">
                                                <div class="card-body text-center">
                                                    <div class="mb-4">
                                                        <i
                                                            class="fa-solid fa-house-user fa-4x text-muted opacity-25"></i>
                                                    </div>
                                                    <h3 class="fw-bold mb-2">Welcome back,
                                                        ${fn:escapeXml(sessionScope.USER_NAME)}</h3>
                                                    <p class="text-muted-foreground mx-auto mb-4"
                                                        style="max-width: 450px;">
                                                        You are logged in as <span
                                                            class="fw-semibold text-foreground">${fn:replace(sessionScope.SYSTEM_ROLE,
                                                            '_', ' ')}</span>.
                                                        Access your modules from the sidebar to manage organization
                                                        tasks.
                                                    </p>
                                                    <a href="${pageContext.request.contextPath}/org/dashboard"
                                                        class="btn btn-primary px-5">Go to Dashboard</a>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </main>

                            <c:import url="/WEB-INF/views/layout/footer.jsp" />
                        </div>
                    </div>
                </div>

                <div id="errorModal" class="modal-overlay">
                    <div class="modal-box border shadow-lg bg-white"
                        style="border-radius: var(--radius); padding: 2.5rem;">
                        <div class="modal-icon mb-4"></div>
                        <h4 id="errorTitle" class="fw-bold mb-2"></h4>
                        <p id="errorMessage" class="text-muted-foreground mb-5"></p>
                        <div class="modal-actions d-flex gap-3 justify-content-center">
                            <button id="errorConfirm" class="btn btn-primary px-4">Confirm Action</button>
                            <button id="errorCancel" class="btn btn-outline-secondary px-4">Cancel</button>
                        </div>
                    </div>
                </div>



                <!-- SweetAlert2 -->
                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>



                <script>
                    // ---- Global App Context ----
                    window.APP = window.APP || {};
                    Object.assign(window.APP, {
                        USER_NAME: "${fn:escapeXml(sessionScope.USER_NAME)}",
                        USER_ID: "${sessionScope.USER_ID}",
                        EMPLOYEE_ID: "${sessionScope.EMP_ID}",
                        ORG_ID: "${sessionScope.ORG_ID}",
                        ROLE: "${sessionScope.SYSTEM_ROLE}",
                        PERMISSIONS: "${sessionScope.PERMISSIONS}",
                        LAST_LOGIN: "${sessionScope.LAST_LOGIN}",
                        LOGO_URL: "${sessionScope.LOGO_URL}",
                        CONTEXT_PATH: "${pageContext.request.contextPath}",
                        BASE_URL: window.location.origin
                    });

                    console.log("%c🔧 SellsHRMS App Context Loaded", "color:#0ea5e9;font-weight:bold;");
                    console.table(window.APP);
                </script>

                <!-- New: Global Permission Helper (add this below) -->
                <script src="${pageContext.request.contextPath}/js/utils/permission-helper.js"></script>



                <!-- Utilities (must come before tasks.js) -->
                <script src="${pageContext.request.contextPath}/js/utils/modalUtils.js"></script>
                <script src="${pageContext.request.contextPath}/js/utils/apiClient.js"></script>
                <script src="${pageContext.request.contextPath}/js/utils/axios.js"></script>
                <script src="${pageContext.request.contextPath}/js/utils/exportToExcel.js"></script>

                <script src="${pageContext.request.contextPath}/js/monitor/common.js"></script>
                <script src="${pageContext.request.contextPath}/js/monitor/monitorAPI.js"></script>

                <script src="${pageContext.request.contextPath}/js/utils/permission-helper.js"></script>
                <script src="${pageContext.request.contextPath}/js/utils/global-helper.js"></script>
                <script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
                <c:if test="${not empty pageScript}">
                    <script src="${pageContext.request.contextPath}/js/${pageScript}.js"></script>
                </c:if>


                <!-- for overriding the already existing styles -->
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">

            </body>

            </html>