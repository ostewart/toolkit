<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for Album Frame display.  Displays a single ImageFrame of an
Album.

Model Requirements:
user: currently logged in user
owners: list of Users who own albums

-->
<html>
  <head>
    <title>Album Owners</title>
  </head>

  <body>
    <h1>Album Owners:</h1>

    <ul>
      <c:forEach var="owner" items="${owners}">
        <li><c:out value="${owner.screenName}"/></li>
      </c:forEach>
    </ul>
  </body>
</html>
