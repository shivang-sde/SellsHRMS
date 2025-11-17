<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <h2>Welcome, ${user}</h2>

    <c:forEach items="${modules}" var="m">

        <c:choose>

            <c:when test="${m == 'superadmin'}">
                <%@ include file="modules/superadmin.jsp" %>
            </c:when>

            <c:when test="${m == 'organisation'}">
                <%@ include file="modules/organisation.jsp" %>
            </c:when>

            <c:when test="${m == 'employees'}">
                <%@ include file="modules/employees.jsp" %>
            </c:when>

            <c:when test="${m == 'attendance'}">
                <%@ include file="modules/attendance.jsp" %>
            </c:when>

            <c:when test="${m == 'leave'}">
                <%@ include file="modules/leave.jsp" %>
            </c:when>

            <c:when test="${m == 'profile'}">
                <%@ include file="modules/profile.jsp" %>
            </c:when>

        </c:choose>

    </c:forEach>