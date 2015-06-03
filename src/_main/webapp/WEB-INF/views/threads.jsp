<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<title>Debug Threads</title>
</head>
<body>
<table>
<c:forEach items="${threads.entrySet()}" var="thread" >
	<tr>
		<td>${thread.getKey().getName()}</td>
		<td>
			<table>
			<c:forEach items="${thread.getValue()}" var="trace" >
				<tr>
					<td>${trace}</td>
				</tr>
			</c:forEach>
			</table>
		</td>
	</tr>
</c:forEach>
</table>
