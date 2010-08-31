
package uk.ac.horizon.ug.exploding.orchestration;

import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.spectator.Constants;
import uk.ac.horizon.ug.exploding.spectator.EquipObjectView;


import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;


public class OrchestrationController
{		
	static Logger logger = Logger.getLogger(OrchestrationController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
	
    public ModelAndView games(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		if (mav.getViewName() != null)
		{
			return mav;
		}
    	
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate cqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.ContentGroup.class);
		
		Object [] cgs = session.match(cqt);
		
		Vector<uk.ac.horizon.ug.exploding.db.ContentGroup> contentGroups = new Vector<uk.ac.horizon.ug.exploding.db.ContentGroup>();
		
		for(int i=0; i<cgs.length; i++)
		{
			uk.ac.horizon.ug.exploding.db.ContentGroup dbc = (uk.ac.horizon.ug.exploding.db.ContentGroup) cgs[i];
			contentGroups.add(dbc);
		}
		
		model.put("contentGroups", contentGroups);
		
		QueryTemplate gqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Game.class);
		gqt.addOrder("timeCreated");
		
		Object [] gs = session.match(gqt);
		
		Vector<uk.ac.horizon.ug.exploding.db.Game> games = new Vector<uk.ac.horizon.ug.exploding.db.Game>();
		
		for(int i=0; i<gs.length; i++)
		{
			uk.ac.horizon.ug.exploding.db.Game dbg = (uk.ac.horizon.ug.exploding.db.Game) gs[i];
			games.add(dbg);
		}
		
		model.put("games", games);

		session.end();
		
		mav.setViewName("/orchestration/games");
    	return mav;
    }
	
    /** active game list query operation - for lobberservice.
     * Return XML/JSON-encoding list of non-ENDED games.
     * 
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView game_list(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
    	
    	String gameTag = request.getParameter("tag");

		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate gqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Game.class);
		gqt.addConstraintIn("state", new Object[] {Game.ACTIVE, Game.ENDING, Game.NOT_STARTED});
		gqt.addOrder("timeCreated");
		if (gameTag!=null && gameTag.length()>0)
			gqt.addConstraintEq("tag", gameTag);
		
		Object [] gs = session.match(gqt);
		
		model.put(Constants.OBJECT_MODEL_NAME, gs);

		session.end();
		
		// EquipObjectView allows request parameter 'encoding'='json' to force JSON return
		mav.setView(new EquipObjectView());
    	return mav;
    }
	
    /** active game list query operation - for lobberservice.
     * Return XML/JSON-encoding list of ContentGruops.
     * 
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView content_group_list(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
    	
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
		mav.setView(new EquipObjectView());
    	return mav;
    }
	
    
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
    	HttpSession httpSession = request.getSession();
    	String contentGroupID = request.getParameter("contentGroupID");
    	String gameName = request.getParameter("name");
    	String gameTag = request.getParameter("tag");

    	if(contentGroupID!=null && contentGroupID.length()>0)
    	{    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);
    		
    		uk.ac.horizon.ug.exploding.db.GameTime gameTime = new uk.ac.horizon.ug.exploding.db.GameTime();
			gameTime.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.GameTime.class, "GT", null));
			
			//gameTime.setGameTime(0.0f);
			gameTime.setGameTime(-1000.0f); // about 5 minutes

			session.add(gameTime);
			
    		uk.ac.horizon.ug.exploding.db.Game game = new uk.ac.horizon.ug.exploding.db.Game();
			
			game.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.Game.class, "GA", null));
			game.setName(gameName);
			if (gameTag!=null && gameTag.length()>0)
				game.setTag(gameTag);
			game.setGameTimeID(gameTime.getID());
			game.setState(Game.NOT_STARTED);
			game.setTimeCreated(System.currentTimeMillis());
			game.setContentGroupID(contentGroupID);
			
			session.add(game);

    		session.end();
    	}
    	
    	mav.setViewName("redirect:/orchestration/games.html");
    	return mav;
    }
    
    public ModelAndView play(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
    	HttpSession httpSession = request.getSession();
    	String gameID = request.getParameter("gameID");

    	if(gameID!=null && gameID.length()>0)
    	{    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);

    		uk.ac.horizon.ug.exploding.db.Game game =
    			(uk.ac.horizon.ug.exploding.db.Game) session.get(uk.ac.horizon.ug.exploding.db.Game.class, gameID);
    		
    		if(game!=null)
    		{
    			game.setState(Game.ACTIVE);
    		}    		

    		session.end();
    	}
    	
    	mav.setViewName("redirect:/orchestration/games.html");
    	return mav;
    }
   
    
    public ModelAndView finish(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
    	HttpSession httpSession = request.getSession();
    	String gameID = request.getParameter("gameID");

    	if(gameID!=null && gameID.length()>0)
    	{    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);

    		uk.ac.horizon.ug.exploding.db.Game game =
    			(uk.ac.horizon.ug.exploding.db.Game) session.get(uk.ac.horizon.ug.exploding.db.Game.class, gameID);
    		
    		if(game!=null)
    		{
    			game.setState(Game.ENDING);
    		}    		

    		session.end();
    	}
    	
    	mav.setViewName("redirect:/orchestration/games.html");
    	return mav;
    }
    
    public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
    	HttpSession httpSession = request.getSession();
    	String gameID = request.getParameter("gameID");

    	if(gameID!=null && gameID.length()>0)
    	{    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);

    		uk.ac.horizon.ug.exploding.db.Game game =
    			(uk.ac.horizon.ug.exploding.db.Game) session.get(uk.ac.horizon.ug.exploding.db.Game.class, gameID);
    		
    		if(game!=null)
    		{
    			game.setState(Game.ENDED);
    		}    		

    		session.end();
    	}
    	
    	mav.setViewName("redirect:/orchestration/games.html");
    	return mav;
    }
}

