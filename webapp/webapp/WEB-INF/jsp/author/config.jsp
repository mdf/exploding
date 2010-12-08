<html>
<body>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<h1>New Game Configuration</h1>
	
		<form action="new_config.html" method="post">	
		
			<p>
				<input type="text" name="name">configuration name<br>
			</p>
			
			<p>
				<input type="checkbox" name="death" value="true"> enable death<br>
				
				<input type="checkbox" name="event" value="true"> enable event effects<br>
				
				<input type="checkbox" name="sole" value="true"> enable sole member zone effects<br>
				
				<input type="checkbox" name="multiple" value="true"> enable multiple member zone effects<br>
				
				<input type="checkbox" name="assimilation" value="true"> enable assimilation<br>
				
				<input type="checkbox" name="offspring" value="true"> enable offspring<br>
				
				<input type="checkbox" name="authoring" value="true"> enable authoring<br>
				
				<input type="checkbox" name="authoringquota" value="true"> enable authoring quota<br>
				
				<input type="checkbox" name="member" value="true"> enable member creation<br>
				
				<input type="checkbox" name="memberquota" value="true"> enable member quota<br>
			</p>
			
			<p>
				<input type="text" name="spawn"> spawn radius (degrees)<br>
				
				<input type="text" name="proximity"> proximity radius (degrees)<br>
				
				<input type="text" name="maxmembers"> maximum members per player<br>
				
				<input type="text" name="starttime"> start time (time units)<br>
				
				<input type="text" name="endtime"> end time (time units)<br>
			</p>
			
			<p>
				<input type="text" name="contextMsgAssimilated"> message on assimilation<br>
				<input type="text" name="contextMsgAssimilatedTitle"> message title on assimilation<br>
				
				<input type="text" name="contextMsgAssimilate"> message on assimilation of other member<br>
				<input type="text" name="contextMsgAssimilateTitle"> message title on assimilation of other member<br>
				
				<input type="text" name="contextMsgBirth"> message on member birth<br>
				<input type="text" name="contextMsgBirthTitle"> message title on member birth<br>
				
				<input type="text" name="contextMsgDeath"> message on member death<br>
				<input type="text" name="contextMsgDeathTitle"> message title on member death<br>
				
				<input type="text" name="contextMsgEnd"> message on game end<br>
				<input type="text" name="contextMsgEndTitle"> message title on game end<br>
				
				<input type="text" name="contextMsgScare"> message on health scare<br>
				<input type="text" name="contextMsgScareTitle"> message title on health scare<br>
			</p>

			<input type="submit" value="create">
			<input type="reset">
		</form>
	</p>

</body>
</html>
