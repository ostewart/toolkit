<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>
<c:set var="datePattern" value="hh:mm a dd-MMM-yyyy"/>

View to display a single Image.

Model Requirements:
user: currently logged in user
image: Image to display

Optional Attributes:
album: ImageGroup
frame: current ImageFrame
nextFrame: ImageFrame
prevFrame: ImageFrame
nextImage: Image
prevImage: Image

-->
<html>
  <head>
    <title><c:out value="${album.displayName}"/><c:if test="${!empty
    album}"> : </c:if><c:out value="${image.displayName}"/></title>
  </head>

  <body>
    <div style="clear: both; float: none; text-align: center">
      <h1><c:out value="${album.displayName}"/><c:if test="${!empty
            album}"> : </c:if><c:out
            value="${image.displayName}"/></h1>
      <c:choose>
      <c:when test="${!empty image}">
        <tm:image image="${image}"/>
      </c:when>
      <c:otherwise>
        <c:if test="${!empty frame}">
          <tm:image image="${frame.image}"/>
        </c:if>
      </c:otherwise>
        </c:choose>
      <div>
      <c:out value="${image.caption}"/><br>
      <c:out value="${image.copyright}"/>
      <c:out value="${image.creator}"/><br>
              Owned by <c:out value="${image.owner.screenName}"/>
    </div>

    <!-- Photo properties -->
    <c:if test="${image.class.name == \"com.trailmagic.image.Photo\"}">
    <div style="clear: both; text-align: center">
      <c:if test="${!empty image.notes}">
        Image Notes: <c:out value="${image.notes}"/>
        </c:if>
        <c:if test="${!empty image.captureDate}">
        Capture Date: <fmt:formatDate value="${image.captureDate}"
            pattern="${datePattern}"/>
        </c:if>
     </div>
      </c:if>
      <!-- Previous Image/Frame Link
      <c:if test="${!empty prevImage}">
        <c:set var="prev" value="${prevFrame}"/>
      </c:if>
      <c:if test="${!empty prevFrame}">
        <c:set var="prev" value="${prevFrame}"/>
      </c:if>
      -->
      <c:if test="${!empty prev}">
        <tm:imageFrameLink frame="${prev}">&lt;--
        Previous</tm:imageFrameLink>
      </c:if>

      <!-- Next Image/Frame Link
      <c:if test="${!empty nextImage}">
        <c:set var="next" value="${nextFrame}"/>
      </c:if>
      <c:if test="${!empty nextFrame}">
        <c:set var="next" value="${nextFrame}"/>
      </c:if>
      -->
      <c:if test="${!empty next}">
        <tm:imageFrameLink frame="${next}">Next --&gt;</tm:imageFrameLink>
      </c:if>
  </body>
</html>
