package uk.ac.horizon.ug.exploding.orchestration;

import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.db.Player;
import uk.ac.horizon.ug.exploding.db.TimelineEvent;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;

public class ModerationController
{
	static Logger logger = Logger.getLogger(ModerationController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
    public ModelAndView moderate(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		if (mav.getViewName() != null)
		{
			return mav;
		}
    	
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate tqt = new QueryTemplate(TimelineEvent.class);
		tqt.addConstraintNotNull("playerID");
		tqt.addOrder("startTime");
		tqt.addOrder("enabled");
		
		Vector<TimelineEvent> events = new Vector<TimelineEvent>();
		
		Object [] ts = session.match(tqt);
		
		for(int i=0; i<ts.length; i++)
		{
			TimelineEvent e = (TimelineEvent) ts[i];
			
			Player p = (Player) session.get(Player.class, e.getPlayerID());
			
			if(p!=null)
			{
				e.setPlayerID(p.getName());
			}
						
			events.add((TimelineEvent)ts[i]);
		}
		
		session.end();
		
		model.put("events", events);
		
		mav.setViewName("/orchestration/moderate");
    	return mav;
    }
    

    public ModelAndView enable(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		if (mav.getViewName() != null)
		{
			return mav;
		}
		
    	String contentID = request.getParameter("contentID");
    	String enableString = request.getParameter("enable");
    	
    	int enabled = 0;
    	
    	try
    	{
    		enabled = Integer.parseInt(enableString);
    	}
    	catch(NumberFormatException e)
    	{
    		
    	}

    	if(contentID!=null && contentID.length()>0)
    	{    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);

    		TimelineEvent event = (TimelineEvent) session.get(TimelineEvent.class, contentID);
    		event.setEnabled(enabled);
    		
    		session.end();
    	}

    	mav.setViewName("redirect:/orchestration/moderate.html");
    	return mav;    	
    }
}
