<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>

      <html>

    <head>
      <title>Image Display: <c:out value="${image.name}"/></title>
    </head>
    <body>
      <h1>Display Image:</h1>
      <table>
        <tr>
          <td>Name:</td>
          <td><c:out value="${image.name}"/></td>
        </tr>
        <tr>
          <td>Title:</td>
          <td><c:out value="${image.title}"/></td>
        </tr>
        <tr>
          <td>Caption:</td>
          <td><c:out value="${image.caption}"/></td>
        </tr>
        <tr>
          <td>Creator:</td>
          <td><c:out value="${image.creator}"/></td>
        </tr>
        <tr>
          <td>Copyright:</td>
          <td><c:out value="${image.copyright}"/></td>
        </tr>
        <c:if test="${isPhoto}">
        <tr>
          <td>Notes:</td>
          <td><c:out value="${image.notes}"/></td>
        </tr>
        <tr>
          <td>Capture Date:</td>
          <td><fmt:formatDate value="${image.captureDate}"
                pattern="${datePattern}"/></td>
        </tr>
        </c:if>
      </table>
    </body>
  </html>