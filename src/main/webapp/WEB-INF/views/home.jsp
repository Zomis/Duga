<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Home page</title>
</head>
<body>
<h1>Duga</h1>
<p>This is a bot for Stack Exchange chat. If you think the bot misbehaves, please tell @SimonAndréForsberg in <a href="http://chat.stackexchange.com/rooms/8595/the-2nd-monitor">The 2nd Monitor</a></p>
<ul>
	<li><a href="http://codereview.stackexchange.com/users/51786/duga">Stack Exchange user</a></li>
	<li><a href="http://codereview.stackexchange.com/users/31562/simon-andr%C3%A9-forsberg">Master</a></li>
	<li><a href="<c:url value="/manage" />">Login and manage your github repositories</a></li>
	<li><a href="<c:url value="/signup" />">Signup</a></li>
</ul>
</body>
</html>