<%@page contentType="text/html; charset=iso-8859-1" language="java" session="true" %>
<%
  response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/josso_login/"));
%>