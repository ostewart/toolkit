<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
  <head>
    <title>Image Database</title>
  </head>
  
  <body>
    <h2>Image Groups:</h2>
    <c:if test="${!empty errors}">
      <ul>
        <c:forEach var="error" items="${errors}">
          <li><c:out value="${error}"/></li>
        </c:forEach>
      </ul>
    </c:if>
    <c:if test="${empty groups}">
      <p>No groups.</p>
    </c:if>
    <table>
      <c:forEach var="group" items="${groups}">
      <tr>
        <td><a href="<c:url
            value="/groups/by-id/${group.id}"/>">
            <c:out value="${group.name}"/></a>
        </td>
        <td><c:out value="${group.description}"/></td>
      </tr>
    </c:forEach>
    </table>
      <a href="<c:url value="/groups/new"/>">new group</a>
  </body>
</html>
