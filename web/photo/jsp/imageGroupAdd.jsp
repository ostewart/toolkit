<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for ImageGroup display.

Model Requirements:
user: currently logged in user
owner: the current album's owner

-->
<html>
  <head>
    <title>New Image Group</title>
  </head>

  <body>
    <h1>New Image Group</h1>
    <spring:hasBindErrors name="imageImportBean">
      <c:forEach var="error" items="${errors.allErrors}">
      <c:out value="${error}"/>
      </c:forEach>
    </spring:hasBindErrors>

    <spring:hasBindErrors name="com.trailmagic.image.image.ImageGroup">
      (full class name)
      <c:forEach var="error" items="${errors.allErrors}">
        <c:out value="${error}"/>
      </c:forEach>
    </spring:hasBindErrors>

    <form method="POST" action="." enctype="multipart/form-data">
      <table>
          <tr>
            <td><label for="name">Name:</label></td>
            <td><input type="text" name="name"/></td>
          </tr>
          <tr>
            <td><label for="displayName">Display Name:</label></td>
            <td><input type="text" name="displayName"/></td>
          </tr>
          <tr>
            <td><label for="description">Description:</label></td>
            <td><input type="text" name="description"/></td>
          </tr>
    </form>
  </body>
</html>
