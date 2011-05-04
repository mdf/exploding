<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Import Report</h1>

	<p>Imported:</p>
	<ul>
	<li>
		Content Group: 
		<c:choose>
			<c:when test="${requestScope.contentGroup!=null}">
				<c:out value="${requestScope.contentGroup.ID}"/>
			</c:when>
			<c:otherwise>None</c:otherwise>
		</c:choose> 
	</li>

	<li>
		Game Config
		<c:choose>
			<c:when test="${requestScope.gameConfig!=null}">
				<c:out value="${requestScope.gameConfig.ID}"/>
			</c:when>
			<c:otherwise>None</c:otherwise>
		</c:choose> 
	</li>
    </ul>
    
    <p><a href="index.html">Back to Author index</a></p>
	
</body>
</html>
