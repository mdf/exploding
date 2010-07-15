<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Upload dumped Players/Members into active game</h1>
	
		<form action="bulkupload.html" method="post" enctype="multipart/form-data">	
			<select name="gameID">
				<c:forEach var="g" items="${requestScope.games}">
					<option value="<c:out value="${g.ID}"/>">
						<c:out value="${g.ID}"/> <c:out value="${g.name}"/> <c:out value="${g.timeCreated}"/> <c:out value="${g.state}"/>
					</option>
		        </c:forEach>
			</select>
			<input type="file" name="file">
			<input type="submit" value="upload">
			<input type="reset">
		</form>
	</p>

</body>
</html>
