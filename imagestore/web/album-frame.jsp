<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for Album Frame display.  Displays a single ImageFrame of an
Album.

Model Requirements:
user: currently logged in user
album: the current album (ImageGroup of type album)
frame: the current ImageFrame
prev: the previous ImageFrame, or null
next: the next ImageFrame, or null


-->
<html>
  <head>
    <title><c:out value="${album.displayName}"/> :
        <c:out value="${frame.image.name}"/></title>
  </head>

  <body>
    <h1><c:out value="${album.displayName}" /> :
        <c:out value="${frame.image.name}" /></h1>

    <div class="navlinks">
      <tm:imageFrameLink frame="${prev}">Previous
      Image</tm:imageFrameLink>
      <tm:imageFrameLink frame="${next}">Previous Image</tm:imageFrameLink>
    </div>

    <tm:image obj="${image}"/>

    <div class="navlinks">
      <tm:imageFrameLink frame="${prev}">Previous
      Image</tm:imageFrameLink>
      <tm:imageFrameLink frame="${next}">Previous Image</tm:imageFrameLink>
    </div>

  </body>
</html>
