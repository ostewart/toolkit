<!DOCTYPE html>
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
-->
<html lang="en">
  <head>
    <title>Login</title>
<!--    <base href="/"/> -->
  </head>
  
  <body>
    <c:if test="${param['failure']}">
      <h2 style="color: red">Login Failed!</h2>
    </c:if>
    
    <h1>Please login:</h1>
    <form method="POST" action="j_spring_security_check">
      <table>
          <tr>
            <td><label for="j_username">Username:</label></td>
            <td><input id="username" type="text" name="j_username" autofocus/></td>
          </tr>
          <tr>
            <td><label for="j_password">Password:</label></td>
            <td><input type="password" name="j_password"/></td>
          </tr>
      </table>
      <input type="submit"/>
    </form>
    <script>
        if (!("autofocus" in document.createElement("input"))) {
          document.getElementById("username").focus();
        }
      </script>
  </body>
</html>
