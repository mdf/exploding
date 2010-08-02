<html><head><title>Spectator Replay Control</title></head>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<h1>Spectator Replay Control</h1>

<p>Status: <c:out value="${requestScope.status}"/></p>

<p>Games: 
 	<c:forEach var="g" items="${requestScope.games}">
 		<c:out value="${g.ID}"/>
 	</c:forEach>
 </p>
<p>Players: 
 	<c:forEach var="p" items="${requestScope.players}">
 		<c:out value="${p.ID}"/>
 		<c:out value="${p.name}"/>
 	</c:forEach>
</p>

<h2>Configure</h2>

<form action="configure.html" enctype="application/x-www-form-urlencoded">
<table>
<tr><td>Logfile</td><td><input type="text" name="logfile" value="${requestScope.logfile}"/></td></tr>
<tr><td>Game</td><td><input type="text" name="game" value="${requestScope.game}"/></td></tr>
<tr><td><input type="submit" value="Submit"/></td></tr>
</table>
</form>

<h2>Advance Replay Time</h2>

<form action="advance.html" enctype="application/x-www-form-urlencoded">
<table>
<tr><td>Advance by time (ms)</td><td><input type="text" name="advanceBy" value="15000"/></td></tr>
<tr><td>Advance to time (ms)</td><td><input type="text" name="advanceTo" value=""/></td></tr>
<tr><td><input type="submit" value="Submit"/></td></tr>
</table>
</form>


</body></html>