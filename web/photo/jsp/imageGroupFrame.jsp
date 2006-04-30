<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for ImageGroup Frame display.  Displays a single ImageFrame of an
ImageGroup.

Model Requirements:
user: currently logged in user
imageGroup: the current imageGroup
frame: the current ImageFrame
prev: the previous ImageFrame, or null
next: the next ImageFrame, or null
groupType: the type of ImageGroup
groupTypeDisplay: the (singular) display name of the type



-->
<html>
  <head>
    <title><c:out value="${imageGroup.displayName}"/> :
        <c:out value="${frame.image.name}"/></title>
  </head>

  <body>
    <h1><c:out value="${imageGroup.displayName}" /> :
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
