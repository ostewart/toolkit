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
imageGroup: ImageGroup
frame: current ImageFrame
nextFrame: ImageFrame
prevFrame: ImageFrame
nextImage: Image
prevImage: Image
groupsContainingImage: ImageGroups that contain frame.image

-->
<html>
  <head>
    <title><c:out value="${imageGroup.displayName}"/><c:if test="${!empty
    imageGroup}"> : </c:if><c:out value="${image.displayName}"/></title>
  </head>

  <body>
    <div style="clear: both">
      <div style="float: left">
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
      </div>
      <div style="float: right">

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
      </div>
    </div>

    <div style="clear: both; float: none; text-align: center">
      <h1><tm:imageGroupLink imageGroup="${imageGroup}"><c:out value="${imageGroup.displayName}"/></tm:imageGroupLink><c:if test="${!empty
            imageGroup}"> : </c:if><c:out
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
      <div class="image-sizes">
        | <c:forEach var="imf" items="${image.manifestations}">
          <a href="?size=<c:out value="${imf.area}"/>">
        <c:out value="${imf.width}"/>x<c:out
          value="${imf.height}"/></a> |
        </c:forEach>
      </div>
      <div>
      <c:out value="${image.caption}"/><br>
      <c:out value="${image.copyright}"/>
      <c:out value="${image.creator}"/><br>
      Owned by <tm:imageGroupLink owner="${image.owner}" groupType="${imageGroup.type}">${image.owner.screenName}</tm:imageGroupLink><br/>
    </div>

    <!-- Photo properties -->
    <c:if test="${image.class.name == \"com.trailmagic.image.Photo\"}">
    <div style="clear: both; text-align: center">
      <c:if test="${!empty image.notes}">
        Image Notes: <c:out value="${image.notes}"/><br/>
        </c:if>
        <c:if test="${!empty image.captureDate}">
        Capture Date: <fmt:formatDate value="${image.captureDate}"
            pattern="${datePattern}"/>
        </c:if>
     </div>
  </c:if>

<br/>
        Set default image size: 
        <a href="?defaultLabel=thumbnail">thumbnail</a> |
        <a href="?defaultLabel=small">small</a> |
        <a href="?defaultLabel=medium">medium</a> |
        <a href="?defaultLabel=large">large</a> |
        <a href="?defaultLabel=huge">huge</a>

    <c:if test="${!empty groupsContainingImage}">
        </c:if>
    <!-- put this back when we have permissions -->
    <c:if test="${false}">
<br/>
    This image is also in: 
    <c:forEach var="group" items="${groupsContainingImage}">
    <tm:imageGroupLink imageGroup="${group}"><c:out
    value="${group.name} (${group.type})"/></tm:imageGroupLink>
    </c:forEach>
    </c:if>

    <div style="clear: both">
      <div style="float: left">
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
      </div>
      <div style="float: right">

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
      </div>
    </div>
  </body>
</html>
