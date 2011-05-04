<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h2>Export Game</h2>
	
	<p>
		<form action="export.xml" method="post">	
			<select name="contentGroupId">
				<c:forEach var="cg" items="${requestScope.contentGroups}">
					<option value="<c:out value="${cg.ID}"/>">
						<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/> <c:out value="${cg.location}"/> <c:out value="${cg.version}"/>
					</option>
		        </c:forEach>
			</select>
			<select name="gameConfigId">
				<c:forEach var="cg" items="${requestScope.gameConfigs}">
					<option value="<c:out value="${cg.ID}"/>">
						<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/>
					</option>
		        </c:forEach>
			</select>			
			<input type="submit" value="Export">
		</form>
	</p>

    <h2>Import Game</h2>
    
    <form action="import.html" method="post" enctype="multipart/form-data">
		<input type="file" name="file">
		<input type="submit" value="Import">
		<input type="reset">
	</form>

    <p><a href="index.html">Back to Author index</a></p>
	
</body>
</html>
