<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Moderate Content</h1>
	
	<p>
		<c:out value="${fn:length(requestScope.events)}"/> events authored by players
	</p>

	<p>
		<table border="0" cellpadding="5" cellspacing="2" width="80%">
			<tbody>
		        <c:forEach var="e" items="${requestScope.events}">
					<tr
						<c:choose>
							<c:when test="${e.enabled == 0}">bgcolor="#eeaaaa"></c:when>
							<c:otherwise>bgcolor="#aaeeaa"></c:otherwise>
						</c:choose>
						<td>
							<c:out value="${e.ID}"/>
							<c:out value="${e.contentGroupID}"/>
							<c:out value="${e.playerID}"/>
							<c:out value="${e.name}"/>
							start <c:out value="${e.startTime}"/>
							end <c:out value="${e.endTime}"/>
							<c:out value="${e.description}"/>
							health <c:out value="${e.health}"/>
							wealth <c:out value="${e.wealth}"/>
							action <c:out value="${e.action}"/>
							brains <c:out value="${e.brains}"/>
						</td>
						<td>
						<c:choose>
							<c:when test="${e.enabled == 0}"><a href="enable.html?contentID=<c:out value="${e.ID}"/>&enable=1">enable</a></c:when>
							<c:otherwise><a href="enable.html?contentID=<c:out value="${e.ID}"/>&enable=0">disable</a></c:otherwise>
						</c:choose>
						</td>					
					</tr>
		        </c:forEach>
	        </tbody>
        </table>
	</p>


</body>
</html>
