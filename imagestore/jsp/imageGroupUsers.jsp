<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for Album Frame display.  Displays a single ImageFrame of an
Album.

Model Requirements:
user: currently logged in user
owners: list of Users who own image groups of groupType
groupType: the type of ImageGroup
groupTypeDisplay: the (singular) display name of the type


-->
<html>
  <head>
    <title><c:out value="${groupTypeDisplay}"/> Owners</title>
  </head>

  <body>
    <h1><c:out value="${groupTypeDisplay}"/> Owners:</h1>

    <ul>
      <c:forEach var="owner" items="${owners}">
        <li>
          <tm:imageGroupLink owner="${owner}"
             groupType="${groupType}">${owner.screenName}</tm:imageGroupLink>
        </li>
      </c:forEach>
    </ul>
  </body>
</html>
