<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>

      <html>

    <head>
      <title>Image Delete: Error</title>
    </head>
    <body>
      <h1>Delete Image:</h1>

      <p>Error Deleting Image.</p>
      <ul>
        <c:forEach var="error" items="${errors}">
        <li><c:out value="${error}"/></li>
        </c:forEach>
      </ul>
    </body>
  </html>