<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Manage Games</h1>
	
	<h2>Start Game</h2>

	<p>
		<form action="create.html" method="post">	
			<select name="contentGroupID">
				<c:forEach var="cg" items="${requestScope.contentGroups}">
					<option value="<c:out value="${cg.ID}"/>">
						<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/> <c:out value="${cg.location}"/> <c:out value="${cg.version}"/>
					</option>
		        </c:forEach>
			</select>
			<select name="gameConfigID">
				<c:forEach var="cg" items="${requestScope.gameConfigs}">
					<option value="<c:out value="${cg.ID}"/>">
						<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/>
					</option>
		        </c:forEach>
			</select>			
			Name: <input type="text" name="name">	
			Tag: <input type="text" name="tag">	
			<input type="submit" value="create game">
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
							<c:out value="${g.ID}"/> <c:out value="${g.name}"/> <c:out value="${g.gameConfigID}"/> <c:out value="${g.tag}"/> <c:out value="${g.contentGroupID}"/> <c:out value="${g.timeCreated}"/> <c:out value="${g.state}"/>
						</td>
						<td>
							<a href="play.html?gameID=<c:out value="${g.ID}"/>">play</a>
						</td>
						<td>
							<a href="finish.html?gameID=<c:out value="${g.ID}"/>">finish</a>
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
