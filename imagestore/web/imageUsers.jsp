<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View to display a list of random Images.

Model Requirements:
user: currently logged in user
images: list of Images to display

-->
<html>
  <head>
    <title>Images</title>
  </head>

  <body>
    <c:forEach var="image" items="${images}">
      <tm:image image="${image}"/>
    </c:forEach>
  </body>
</html>
