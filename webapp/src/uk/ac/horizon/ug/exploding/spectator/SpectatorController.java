package uk.ac.horizon.ug.exploding.spectator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Message;
import uk.ac.horizon.ug.exploding.db.Player;
import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;


public class SpectatorController
{
	static Logger logger = Logger.getLogger(SpectatorController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}

	
    public ModelAndView messages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ModelAndView mav = new ModelAndView();
        
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		String timeStr = request.getParameter("lastCheck");
		long time = 0;
		
		if(timeStr!=null && timeStr.length()>0)
		{		
			try
			{
				time = Long.parseLong(timeStr);
			}
			catch(NumberFormatException e)
			{
				
			}
		}
		
		QueryTemplate mqt = new QueryTemplate(Message.class);
		mqt.addConstraintGt("createTime", time);
		mqt.addOrder("createTime", false);

		Object [] ms = session.match(mqt);
		
		Vector<Message> msgs = new Vector<Message>();
				
		for(int i=0; i<ms.length; i++)
		{
			Message m = (Message) ms[i];
			msgs.add(m);
		}
		
		session.end();
		
		MessageBean data = new MessageBean();
		data.setMessages(msgs.toArray(new Message[msgs.size()]));
		
        mav.setView(new EquipObjectView());
        mav.getModel().put(Constants.OBJECT_MODEL_NAME, msgs);
        
        return mav;
    }
    
    public ModelAndView game(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ModelAndView mav = new ModelAndView();
        
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate gqt = new QueryTemplate(Game.class);
		gqt.addConstraintEq("state", Game.ACTIVE);
		gqt.addOrder("timeCreated", true);
		
		Object [] gs = session.match(gqt);
		
		GameBean data = new GameBean();
		
		if(gs.length>0)
		{
			Game game = (Game) gs[0];
			
			data.setGame(game);
						
			List<Player> players = new ArrayList<Player>();
			
			QueryTemplate pqt = new QueryTemplate(Player.class);
			pqt.addConstraintEq("gameID", game.getID());
			
			Object [] ps = session.match(pqt);
			
			for(int i=0; i<ps.length; i++)
			{
				Player p = (Player) ps[i];
				players.add(p);
			}
			
			data.setPlayers(players.toArray(new Player[players.size()]));

			List<Member> members = new ArrayList<Member>();
			
			QueryTemplate mqt = new QueryTemplate(Member.class);
			mqt.addConstraintEq("gameID", game.getID());
			
			Object [] ms = session.match(mqt);
			
			for(int i=0; i<ms.length; i++)
			{
				Member m = (Member) ms[i];
				members.add(m);
			}

			data.setMembers(members.toArray(new Member[members.size()]));
		}
		
		session.end();
		
        mav.setView(new EquipObjectView());
        mav.getModel().put(Constants.OBJECT_MODEL_NAME, data);
        
        return mav;
    }	
}
