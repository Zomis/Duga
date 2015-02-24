<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Duga manage repositories</title>
</head>
<body>
<h1>Manage page</h1>
<form method="post" action="<c:url value="/j_spring_security_logout" />"><input type="submit" value="Logout"></input></form>
<p>This is the manage page. It's available for all users. Here you can see an overview of your repositories that are tracked by the bot<br/>
<a href="${pageContext.request.contextPath}/index.html">Home page</a><br/></p>
</body>
</html>