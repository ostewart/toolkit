<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

Image Import Form

Model Requirements:
none

-->
<html>
  <head>
    <title>Import Images</title>
  </head>
  
  <body>
    <h1>Import Images</h1>
    <form method="POST" action="/import"
      enc-type="multipart/form-data">
      <label for="imagesData">Images Data File:</label>
      <input name="imagesData" type="file"/>
      <input name="submit" type="submit"/>
    </form>
  </body>
</html>
