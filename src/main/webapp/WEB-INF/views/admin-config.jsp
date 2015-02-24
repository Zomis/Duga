<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>First Admin page</title>
</head>
<body>
<h1>First Admin page</h1>
<form method="post" action="<c:url value="/j_spring_security_logout" />"><input type="submit" value="Logout"></input></form>
<p>
<a href="<c:url value="/" />">Home page</a><br/>
<a href="<c:url value="/manage" />">Manage repositories</a><br/>
</p>
</body>
</html>