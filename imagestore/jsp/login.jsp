<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
-->
<html>
  <head>
    <title>Login</title>
    <base href="/"/>
  </head>
  
  <body>
    <h1>Please login:</h1>
    <form method="POST" action="j_acegi_security_check">
      <table>
          <tr>
            <td><label for="j_username">Username:</label></td>
            <td><input type="text" name="j_username"/></td>
          </tr>
          <tr>
            <td><label for="j_password">Password:</label></td>
            <td><input type="password" name="j_password"/></td>
          </tr>
      </table>
      <input type="submit"/>
    </form>
  </body>
</html>
