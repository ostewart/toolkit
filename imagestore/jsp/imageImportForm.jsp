<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

Image Import Form

Model Requirements:
none

-->
<html>
  <head>
    <title>Import Images</title>
  </head>
  
  <body>
    <h1>Import Images</h1>

<spring:hasBindErrors name="imageImportBean">
  <c:forEach var="error" items="${errors.allErrors}">
        <c:out value="${error}"/>
  </c:forEach>
</spring:hasBindErrors>

<spring:hasBindErrors name="com.trailmagic.image.ui.ImageImportBean">
(full class name)
  <c:forEach var="error" items="${errors.allErrors}">
        <c:out value="${error}"/>
  </c:forEach>
</spring:hasBindErrors>
<c:if test="${empty imageImportBean}">
imageImportBean is empty
</c:if>
<c:if test="${empty imageImportBean.imagesData}">
imageImportBean.imagesData is empty
</c:if>

    <form method="POST" action="/images/import" enctype="multipart/form-data">
      <label for="imagesData">Images Data File:</label>
      <input name="imagesData" type="file"/>
      <input name="submit" type="submit"/>
    </form>
  </body>
</html>
