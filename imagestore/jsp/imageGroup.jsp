<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for ImageGroup display.

Model Requirements:
user: currently logged in user
imageGroup: the current ImageGroup
groupType: the type of ImageGroup
groupTypeDisplay: the (singular) display name of the type
owner: the current album's owner
frames: List of ImageFrames for this group

-->
<html>
  <head>
    <title><c:out value="${imageGroup.displayName}"/></title>
  </head>

  <body>
    <h1><c:out value="${imageGroup.displayName}" /></h1>
<center>
<!--    <ul>-->
      <c:forEach var="frame" items="${frames}">
<!--        <li>-->
          <tm:imageFrameLink frame="${frame}">
             <tm:image image="${frame.image}" sizeLabel="thumbnail"/>
<!--             ${frame.image.displayName}-->
          </tm:imageFrameLink>
<!--        </li>-->
      </c:forEach>
<!--    </ul>-->
</center>
  </body>
</html>
