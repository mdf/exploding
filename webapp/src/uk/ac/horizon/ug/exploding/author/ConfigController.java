
package uk.ac.horizon.ug.exploding.author;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.db.GameConfig;
import uk.ac.horizon.ug.exploding.spectator.Constants;
import uk.ac.horizon.ug.exploding.spectator.EquipObjectView;
import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;

public class ConfigController
{
	static Logger logger = Logger.getLogger(ConfigController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
    public ModelAndView config(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		mav.setViewName("/author/config");

    	return mav;
    }
	
    public ModelAndView new_config(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		GameConfig gc = new GameConfig();
		
		gc.setName(request.getParameter("name"));

		gc.setContextMsgAssimilated(request.getParameter("contextMsgAssimilated"));
		gc.setContextMsgAssimilatedTitle(request.getParameter("contextMsgAssimilatedTitle"));

		gc.setContextMsgAssimilate(request.getParameter("contextMsgAssimilate"));
		gc.setContextMsgAssimilateTitle(request.getParameter("contextMsgAssimilateTitle"));

		gc.setContextMsgBirth(request.getParameter("contextMsgBirth"));
		gc.setContextMsgBirthTitle(request.getParameter("contextMsgBirthTitle"));

		gc.setContextMsgDeath(request.getParameter("contextMsgDeath"));
		gc.setContextMsgDeathTitle(request.getParameter("contextMsgDeathTitle"));

		gc.setContextMsgEnd(request.getParameter("contextMsgEnd"));
		gc.setContextMsgEndTitle(request.getParameter("contextMsgEndTitle"));

		gc.setContextMsgScare(request.getParameter("contextMsgScare"));
		gc.setContextMsgScareTitle(request.getParameter("contextMsgScareTitle"));
		
		try
		{
			gc.setSpawnRadius(Float.parseFloat(request.getParameter("spawn")));
		}
		catch(NumberFormatException e)
		{
			gc.setSpawnRadius(0.0f);
		}
		
		try
		{
			gc.setProximityRadius(Float.parseFloat(request.getParameter("proximity")));
		}
		catch(NumberFormatException e)
		{
			gc.setProximityRadius(0.0f);
		}
		
		try
		{
			gc.setStartTime(Float.parseFloat(request.getParameter("starttime")));
		}
		catch(NumberFormatException e)
		{
			gc.setStartTime(0.0f);
		}
		
		try
		{
			gc.setEndTime(Float.parseFloat(request.getParameter("endtime")));
		}
		catch(NumberFormatException e)
		{
			gc.setEndTime(0.0f);
		}
		
		try
		{
			gc.setMaxMembers(Integer.parseInt(request.getParameter("maxmembers")));
		}
		catch(NumberFormatException e)
		{
			gc.setMaxMembers(20);
		}
		
		
		/*
		 * 
			
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

				*/
		
		/*
    	
		// optional filter paramters
		String location = request.getParameter("location");
		String version = request.getParameter("version");
		
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate gqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.ContentGroup.class);
		if (location!=null && location.length()>0)
			gqt.addConstraintEq("location", location);
		if (version!=null && version.length()>0)
			gqt.addConstraintEq("version", version);
		
		Object [] gs = session.match(gqt);
		
		model.put(Constants.OBJECT_MODEL_NAME, gs);

		session.end();
		
		// EquipObjectView allows request parameter 'encoding'='json' to force JSON return
		mav.setView(new EquipObjectView()); */
		
		mav.setViewName("redirect:/author/index.html");
		return mav;
    }
}