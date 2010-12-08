
package uk.ac.horizon.ug.exploding.author;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

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