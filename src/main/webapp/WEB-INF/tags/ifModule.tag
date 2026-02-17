<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="code" required="true" type="java.lang.String" %>

<c:if test="${fn:contains(MODULES, code)}">
  <jsp:doBody />
</c:if>
