<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for Album display.

Model Requirements:
user: currently logged in user
album: the current album (ImageGroup of type album)
owner: the current album's owner
frames: List of ImageFrames for this Album

-->
<html>
  <head>
    <title><c:out value="${album.displayName}"/></title>
  </head>

  <body>
    <h1><c:out value="${album.displayName}" /></h1>
    <ul>
      <c:forEach var="frame" items="${frames}">
        <li>
          <c:out value="${frame.caption}"/>
          <tm:image image="${frame.image}"/>
        </li>
      </c:forEach>
    </ul>
  </body>
</html>
