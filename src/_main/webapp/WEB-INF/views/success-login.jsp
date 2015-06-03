<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Welcome page</title>
</head>
<body>
<h1>Welcome page</h1>
<p>You have successfully logged in.<br/>
	<a href="<c:url value="/" />">Home page</a><br/>
	<a href="<c:url value="/manage" />">Manage</a><br/>
	<a href="<c:url value="/logout" />">Log out</a><br/>
</p>
</body>
</html>