<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Manage Games</h1>
	
	<h2>Start Game</h2>

	<p>
		<form action="start.html" method="post">	
			<select name="contentGroupID">
				<c:forEach var="cg" items="${requestScope.contentGroups}">
					<option value="<c:out value="${cg.ID}"/>">
						<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/>
					</option>
		        </c:forEach>
			</select>
			<input type="text" name="name">	
			<input type="submit" value="start game">
		</form>
	</p>

	<h2>Current Games</h2>

	<p>
		<c:out value="${fn:length(requestScope.games)}"/> games
	</p>

	<p>
		<table border="0" cellpadding="5" cellspacing="2" width="80%">
			<tbody>
		        <c:forEach var="g" items="${requestScope.games}">
					<tr bgcolor="#eeeeee">
						<td>
							<c:out value="${g.ID}"/> <c:out value="${g.name}"/> <c:out value="${g.contentGroupID}"/> <c:out value="${g.gameTime}"/> <c:out value="${g.dateStarted}"/> <c:out value="${g.active}"/>
						</td>
						<td>
							<a href="stop.html?gameID=<c:out value="${g.ID}"/>">stop</a>
						</td>
					</tr>
		        </c:forEach>
	        </tbody>
        </table>
	</p>


</body>
</html>
