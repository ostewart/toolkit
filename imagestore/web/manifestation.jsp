<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>

      <html>

    <head>
      <title>Image Manifestation: <c:out value="${image.name}"/></title>
    </head>
    <body>
<c:choose>
<c:when  test="${!empty errors}">
<ul>
<c:forEach var="error" items="${errors}">
  <li><c:out value="${error}"/></li>
</c:forEach>
  </ul>
</c:when>
<c:otherwise>
      <h2>Image:</h2>
      <table>
        <tr>
          <td>Image Name:</td>
          <td><c:out value="${image.name}"/></td>
        </tr>
        <tr>
          <td>Image Title:</td>
          <td><c:out value="${image.title}"/></td>
        </tr>
      </table>

      <c:if test="${!empty image.manifestations}">
      <h2>Image Manifestations:</h2>
      <c:forEach var="mf" items="${image.manifestations}">
      <ul>
        <li>Name: <c:out value="${mf.name}"/></li>
        <li>Original: <c:out value="${mf.original}"/></li>
      </ul>
      <img src="<c:url value="/mf/by-id/${mf.id}"/>"/>
      <hr/>
      </c:forEach>
      </c:if>
      <h2>Add Image Manifestation:</h2>
      <form method="POST" enctype="multipart/form-data">
      <input type="hidden" name="image_id"
      value="<c:out value="${image.id}"/>"/> 

      <table>  
        <tr>
          <td><label for="name"/>Name:</td>
          <td><input type="text" name="name"/></td>
        </tr>
        <tr>
          <td><label for="original"/>Original:</td>
          <td><input type="checkbox" name="original" value="true"/></td>
        </tr>
        <tr>
          <td><label for="file"/>Image File:</td>
          <td><input type="file" name="file"/></td>
        </tr>
    </table>
    <input type="submit" name="submit" value="Submit"/>
    
    </form>
      
      <p>Back to the <a href="<c:url
      value="/display/by-id/${image.id}"/>">image</a>.</p>
    
      </c:otherwise>
    </c:choose>
    </body>
  </html>