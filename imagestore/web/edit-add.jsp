<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>

<html>

    <head>
      <title><c:out value="${operation}"/>
             Image: <c:out value="${image.name}"/></title>
    </head>
    <body>
      <h1><c:out value="${operation}"/> Image:</h1>
      <c:if test="${!empty errors}">
        <ul>
          <c:forEach var="error" items="${errors}">
          <li><c:out value="${error}"/></li>
          </c:forEach>
        </ul>
      </c:if>
      <form method="POST">
        <table>
          <tr>
            <td><label for="name">Name:</label></td>
            <td><input type="text" name="name"
                       value="<c:out value="${image.name}"/>"/></td>
          </tr>
          <tr>
            <td>Display Name:</td>
            <td><input type="text" name="display_name"
                       value="<c:out value="${image.displayName}"/>"/></td>
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
        <c:if test="${!empty image}">
        <input type="hidden" name="id" value="${image.id}"/>
        </c:if>
        <input type="submit" value="Submit"/>
      </form>
    </body>
  </html>