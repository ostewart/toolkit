<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>
<c:choose>
  <c:when test="${empty image}">
    <c:set var="operation" value="Edit"/>
  </c:when>
  <c:otherwise>
    <c:set var="operation" value="Add"/>
  </c:otherwise>
</c:choose>
   <html>

    <head>
      <title><c:out value="${operation}"/>
             Image: <c:out value="${image.name}"/></title>
    </head>
    <body>
      <h1><c:out value="${operation}"/> Image:</h1>
      <form method="POST">
        <table>
          <tr>
            <td><label for="name">Name:</label></td>
            <td><input type="text" name="name"
                       value="<c:out value="${image.name}"/>"/></td>
          </tr>
          <tr>
            <td>Title:</td>
            <td><input type="text" name="title"
                       value="<c:out value="${image.title}"/>"/></td>
          </tr>
          <tr>
            <td>Caption:</td>
            <td><input type="text" name="caption"
                       value="<c:out value="${image.caption}"/>"/></td>
          </tr>
          <tr>
            <td>Creator:</td>
            <td><input type="text" name="creator"
                       value="<c:out value="${image.creator}"/>"/></td>
          </tr>
          <tr>
            <td>Copyright:</td>
            <td><input type="text" name="copyright"
                       value="<c:out value="${image.copyright}"/>"/></td>
        </tr>
        <c:if test="${isPhoto}">
        <tr>
          <td>Notes:</td>
            <td><input type="text" name="notes"
                       value="<c:out value="${image.notes}"/>"/></td>
          </tr>
          <tr>
            <td>Capture Date:</td>
            <td><input type="text" name="notes"
                       value="<fmt:formatDate value="${image.captureDate}"
                                          pattern="${datePattern}"/>"/></td>
            <td></td>
          </tr>
          </c:if>
        </table>
      </form>
    </body>
  </html>