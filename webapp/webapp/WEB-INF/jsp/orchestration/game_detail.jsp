<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Game Detail</h1>
	
	<h2>Game</h2>
	
	<p>
		id: <c:out value="${requestScope.game.ID}"/><br>
		content group: <c:out value="${requestScope.game.contentGroupID}"/><br>
		name: <c:out value="${requestScope.game.name}"/><br>
		year: <c:out value="${requestScope.game.year}"/><br>
		state: <c:out value="${requestScope.game.state}"/><br>
	</p>
	
	<h2><c:out value="${fn:length(requestScope.players)}"/> Players</h2>
	
	<c:forEach var="p" items="${requestScope.players}">
		<p>
			id: <c:out value="${p.id}"/><br>
			name: <c:out value="${p.name}"/><br>
			members: <c:out value="${p.memberCount}"/><br>
			position: <c:out value="${p.latitude}"/>, <c:out value="${p.longitude}"/><br>
			founderMember: <c:out value="${p.founderMember}"/><br>
			founderZone: <c:out value="${p.founderZone}"/><br>
			canAuthor <c:out value="${p.canAuthor}"/><br>
			newMemberQuota: <c:out value="${p.newMemberQuota}"/><br>
			average health: <c:out value="${p.health}"/> wealth: <c:out value="${p.wealth}"/> action: <c:out value="${p.action}"/> brains: <c:out value="${p.brains}"/><br>
		</p>
		<c:forEach var="m" items="${p.members}">
			<p>
			member id: <c:out value="${m.ID}"/> name: <c:out value="${m.name}"/> parent: "<c:out value="${m.parentMemberID}"/>" zone: <c:out value="${m.zone}"/> position: <c:out value="${m.position.latitude}"/>, <c:out value="${m.position.longitude}"/>
			health: <c:out value="${m.health}"/> wealth: <c:out value="${m.wealth}"/> action: <c:out value="${m.action}"/> brains: <c:out value="${m.brains}"/>
			</p>
		</c:forEach>
	</c:forEach>
	
</body>
</html>
