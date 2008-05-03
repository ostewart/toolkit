<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View for ImageGroup display.

Model Requirements:
user: currently logged in user
imageGroup: the current ImageGroup
owner: the current album's owner

-->
<html>
  <head>
    <title><c:out value="${imageGroup.displayName}"/></title>
  </head>

  <body>
    <h1><c:out value="${imageGroup.displayName}" /> Added</h1>

        <tm:imageGroupLink imageGroup="${imageGroup}"><c:out value="${imageGroup.displayName}"/></tm:imageGroupLink>
  </body>
</html>
