<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
  <c:set var="datePattern" value="dd-MMM-yyyy"/>

      <html>

    <head>
      <title>Image Display: <c:out value="${image.name}"/></title>
    </head>
    <body>
<c:if test="${empty images}">
<h2>No images found.</h2>
</c:if>
<c:forEach var="image" items="${images}">
      <h1>Display Image:</h1>
      <table>
        <tr>
          <td>Name:</td>
          <td><c:out value="${image.name}"/></td>
        </tr>
        <tr>
          <td>Display Name:</td>
          <td><c:out value="${image.displayName}"/></td>
        </tr>
        <tr>
          <td>Caption:</td>
          <td><c:out value="${image.caption}"/></td>
        </tr>
        <tr>
          <td>Creator:</td>
          <td><c:out value="${image.creator}"/></td>
        </tr>
        <tr>
          <td>Copyright:</td>
          <td><c:out value="${image.copyright}"/></td>
        </tr>
        <c:if test="${isPhoto}">
        <tr>
          <td>Notes:</td>
          <td><c:out value="${image.notes}"/></td>
        </tr>
        <tr>
          <td>Capture Date:</td>
          <td><fmt:formatDate value="${image.captureDate}"
                pattern="${datePattern}"/></td>
        </tr>
        </c:if>
        <c:choose>
        <c:when test="${empty image.manifestations}">
        <p>No image manifestations.</p>
        </c:when>
        <c:otherwise>
        <c:forEach var="mf" items="${image.manifestations}">
        <tr>
          <th>Image Manifestation: <a href="<c:url
            value="/mf/by-id/${mf.id}"/>">(edit)</a></th>
        </tr>
        <tr>
          <td>Name:</td>
          <td><c:out value="${mf.name}"/></td>
        </tr>

        <tr>
          <td>Format:</td>
          <td><c:out value="${mf.format}"/></td>
        </tr>
        <tr>
          <td>Original:</td>
          <td><c:out value="${mf.original}"/></td>
        </tr>

        </c:forEach>
        </c:otherwise>
        </c:choose>
        <tr>
          <td><a href="<c:url value="/manifestation">
          <c:param name="image_id" value="${image.id}"/>
          </c:url>">Add manifestation</a></td>
        </tr>
        <tr>
          <td><a href="<c:url value="/edit">
            <c:param name="id" value="${image.id}"/>
            </c:url>">edit</a></td>
            <td><a href="<c:url value="/delete">
                           <c:param name="id" value="${image.id}"/>
                         </c:url>">delete</a></td>
          <td><a href="<c:url
            value="/display/by-id/${image.id}"/>">display</a></td>
          </tr>
      </table>
      </c:forEach>
      <p><a href="<c:url value="/edit"/>">Create</a> a new Image.</p>
    </body>
  </html>