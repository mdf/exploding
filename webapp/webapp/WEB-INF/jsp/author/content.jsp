<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>Manage Content</h1>

	<form action="upload.html" method="post" enctype="multipart/form-data">
		<input type="file" name="file">
		<input type="submit" value="Upload">
		<input type="reset">
	</form>

	<h2>Content Groups</h2>

	<p>
		<c:out value="${fn:length(requestScope.contentGroups)}"/> content groups
	</p>

	<p>
		<table border="0" cellpadding="5" cellspacing="2" width="80%">
			<tbody>
		        <c:forEach var="cg" items="${requestScope.contentGroups}">
					<tr bgcolor="#eeeeee">
						<td>
							<c:out value="${cg.ID}"/> <c:out value="${cg.name}"/> <c:out value="${cg.location}"/> <c:out value="${cg.version}"/>
						</td>
						<td>
							<a href="delete.html?contentGroupID=<c:out value="${cg.ID}"/>">delete</a>
						</td>
					</tr>
		        </c:forEach>
	        </tbody>
        </table>
	</p>


</body>
</html>
