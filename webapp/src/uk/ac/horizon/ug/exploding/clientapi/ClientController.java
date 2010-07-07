/**
 * 
 */
package uk.ac.horizon.ug.exploding.clientapi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.clientapi.LoginReplyMessage.Status;
import uk.ac.horizon.ug.exploding.db.ClientConversation;
import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Player;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;

/**
 * @author cmg
 *
 */
public class ClientController {
	private static final int CLIENT_VERSION = 1;

	static Logger logger = Logger.getLogger(ClientController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	private XStream getXStream() {
    	XStream xs = new XStream(new DomDriver());
		xs.alias("login", LoginMessage.class);
		xs.alias("reply", LoginReplyMessage.class);
		return xs;
	}
	/** client login 
	 * @throws IOException */
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	XStream xs = getXStream();
		LoginMessage login = (LoginMessage)xs.fromXML(request.getReader());
    	logger.info("Login: "+login);

    	if (!"AndroidDevclient".equals(login.getClientType())) {
    		returnLoginError(LoginReplyMessage.Status.UNSUPPORTED_CLIENT_TYPE, "Client not supported ("+login.getClientType()+")", response);
    		return null;
    	}
    	if (login.getClientVersion()<CLIENT_VERSION) {
    		returnLoginError(LoginReplyMessage.Status.OLD_CLIENT_VERSION, "Client is too old (needs version "+CLIENT_VERSION+")", response);
    		return null;
    	}
    	if (login.getClientVersion()!=CLIENT_VERSION) {
    		returnLoginError(LoginReplyMessage.Status.BAD_CLIENT_VERSION, "Client is unknown (needs version "+CLIENT_VERSION+")", response);
    		return null;
    	}

    	ISession session = getDataspace().getSession();
    	session.begin();
    	
    	// look for ClientConversation(s) with this client
    	QueryTemplate q = new QueryTemplate(ClientConversation.class);
    	q.addConstraintEq("clientID", login.getClientId());

    	Object ccs [] = session.match(q);

    	// first look for an association between this client and an active game to reuse...
    	Player player = null;
    	Game game = null;
    	boolean welcomeBack = false;
    	for (int cci=0; cci<ccs.length; cci++) {
    		ClientConversation cc = (ClientConversation)ccs[cci];
    		if (cc.getID().equals(login.getConversationId())) {
    			logger.warn("Received login for duplicate converation ID "+login.getConversationId());
    			session.end();
    			returnLoginError(LoginReplyMessage.Status.FAILED, "Please try again (duplicate conversation ID).", response);
    			return null;

    		}
    		if (cc.isSetActive() && cc.getActive()) {
    			// mark inactive!
    			cc.setActive(false);
    			logger.debug("Mark conversation "+cc.getID()+" with "+cc.getClientID()+" inactive on new login");
    			Game g = (Game) session.get(Game.class, cc.getGameID());
    			if (g.isSetActive() && g.getActive()) {
    				// jack pot
    				game = g;
    				player = (Player) session.get(Player.class, cc.getPlayerID());
    				welcomeBack = true;
    				logger.debug("Converation "+login.getConversationId()+" taking over Player "+player.getID()+" in game "+game.getID());
    			}
    		}
    	}
    	if (game==null) {
    		// look for an active game
    		Object games [] = session.match(new QueryTemplate(Game.class));
    		for (int gi=0; gi<games.length; gi++) {
    			Game g = (Game)games[gi];
    			if (g.isSetActive() && g.getActive()) {
    				game = g;
    				logger.debug("Client "+login.getClientId()+" joining active game "+game.getID());
    			}
    		}
    		if (game==null) {
    			logger.warn("No active game: client "+login.getClientId()+" rejected");
    			session.end();
    			returnLoginError(LoginReplyMessage.Status.GAME_NOT_FOUND, "No game available right now.", response);
    			return null;
    		}
    	}
    	if (player==null) {
    		// create a player
    		player = new Player();
    		player.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.Player.class, "P", null));
    		player.setName(login.getPlayerName());
    		player.setGameID(game.getID());
    		player.setCanAuthor(true); // ??
    		player.setPoints(0);
    		// TODO
    		//player.setPosition(position);
    		session.add(player);
    		logger.info("Created player "+player.getID()+" ("+player.getName()+") for client "+login.getClientId());
    	}
    	
    	// create conversation
    	ClientConversation cc = new ClientConversation();
    	cc.setID(login.getConversationId());
    	cc.setActive(true);
    	cc.setClientID(login.getClientId());
    	cc.setClientType(login.getClientType());
    	cc.setClientVersion(login.getClientVersion());
    	cc.setCreationTime(System.currentTimeMillis());
    	cc.setNextSeqNo(0);
    	cc.setLastContactTime(0L);
    	cc.setGameID(game.getID());
    	cc.setPlayerID(player.getID());
    	session.add(cc);
    	logger.info("Created converation "+cc.getID()+" for client "+cc.getClientID()+" as player "+player.getID()+" in game "+game.getID());
    	
    	session.end();
    	
    	LoginReplyMessage reply = new LoginReplyMessage();
    	
    	reply.setGameStatus(GameStatus.ACTIVE);
    	reply.setGameId(game.getID());
    	reply.setMessage(welcomeBack ? "Welcome back" : "Welcome");
    	reply.setStatus(LoginReplyMessage.Status.OK);

    	response.setStatus(200);
    	PrintWriter pw = response.getWriter();
    	xs.toXML(reply, pw);
    	pw.close();
    	return null;
    }
    private void returnLoginError(Status status, String message, HttpServletResponse response) throws IOException {
    	LoginReplyMessage reply = new LoginReplyMessage();
    	
    	reply.setGameStatus(GameStatus.UNKNOWN);
    	reply.setMessage(message);
    	reply.setStatus(status);

    	XStream xs = getXStream();
    	response.setStatus(200);
    	PrintWriter pw = response.getWriter();
    	xs.toXML(reply, pw);
    	pw.close();
	}

	/** client messages exchange */
    public ModelAndView messages(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();

		
    	return mav;
    }
}
