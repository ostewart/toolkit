<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<html>
  <head>
    <title>New Image Group</title>
  </head>
  
  <body>
    <h2>New Image Group:</h2>

    <!-- print errors if any -->
    <c:if test="${!empty errors}">
      <ul>
        <c:forEach var="error" items="${errors}">
          <li><c:out value="${error}"/></li>
        </c:forEach>
      </ul>
    </c:if>

    <!-- print the form -->
    <form method="POST">    
      <table>
        <tr>
        <td>Name:</td>
          <td><input type="text" name="name"
              value="<c:out value="${group.name}"/>"/></td>
        </tr>
        <tr>        
          <td>Description:</td>
          <td><input type="text" name="description"
              value="<c:out value="${group.description}"/>"/></td>
        </tr>
        <tr>        
          <td>Type:</td>
          <td><input type="text" name="type"
              value="<c:out value="${group.type}"/>"/></td>
        </tr>
      </table>
      <input type="submit" value="Submit"/>      
    </form>
  </body>
</html>
