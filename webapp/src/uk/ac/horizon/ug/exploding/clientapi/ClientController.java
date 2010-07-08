/**
 * 
 */
package uk.ac.horizon.ug.exploding.clientapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.clientapi.LoginReplyMessage.Status;
import uk.ac.horizon.ug.exploding.db.ClientConversation;
import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.MessageToClient;
import uk.ac.horizon.ug.exploding.db.Player;
import uk.ac.horizon.ug.exploding.db.Position;
import uk.ac.horizon.ug.exploding.db.Zone;

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
	
	protected ClientSubscriptionManager clientSubscriptionManager;
	
	
	public ClientSubscriptionManager getClientSubscriptionManager() {
		return clientSubscriptionManager;
	}

	public void setClientSubscriptionManager(
			ClientSubscriptionManager clientSubscriptionManager) {
		this.clientSubscriptionManager = clientSubscriptionManager;
	}

	static XStream getXStream() {
    	XStream xs = new XStream(/*new DomDriver()*/);
		xs.alias("login", LoginMessage.class);
		xs.alias("reply", LoginReplyMessage.class);
		xs.alias("list", LinkedList.class);    	
		xs.alias("message", Message.class);
		// game-specific types
		xs.alias("zone", Zone.class);
		xs.alias("position", Position.class);
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
    			clientSubscriptionManager.handleConversationEnd(cc, session);
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
    	cc.setNextSeqNo(1);
    	cc.setLastContactTime(0L);
    	cc.setGameID(game.getID());
    	cc.setPlayerID(player.getID());
    	session.add(cc);
    	logger.info("Created converation "+cc.getID()+" for client "+cc.getClientID()+" as player "+player.getID()+" in game "+game.getID());

    	clientSubscriptionManager.handleConversationStart(cc, session);

    	session.end();
    	
    	LoginReplyMessage reply = new LoginReplyMessage();
    	
    	reply.setGameStatus(GameStatus.ACTIVE);
    	reply.setGameId(game.getID());
    	reply.setMessage(welcomeBack ? "Welcome back" : "Welcome");
    	reply.setStatus(LoginReplyMessage.Status.OK);

    	response.setStatus(200);
    	PrintWriter pw = response.getWriter();
    	//xs.toXML(reply, pw);
    	String xml = xs.toXML(reply);
    	logger.info("Sent login reply: "+xml+" (from "+reply+")");
    	pw.print(xml);
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

	/** client messages exchange 
	 * @throws IOException */
    public ModelAndView messages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String conversationID = request.getParameter("conversationID");
    	if (conversationID==null || conversationID.length()==0)
    	{
    		logger.warn("No conversationID parameter");
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No conversationID parameter");
    		return null;
    	}
		XStream xs = getXStream();
		List<Message> messages = (List<Message>)xs.fromXML(request.getReader());
		logger.info("Got "+messages.size()+" messages: "+messages);
		
		ISession session = dataspace.getSession();
		session.begin();
		
		ClientConversation conversation = (ClientConversation) session.get(ClientConversation.class, conversationID);
		Game game = conversation!=null ? (Game)session.get(Game.class, conversation.getGameID())  : null;
		session.end();

		if (conversation==null) {
			logger.warn("conversation "+conversationID+" unknown");
    		response.sendError(HttpServletResponse.SC_NOT_FOUND, "conversation "+conversationID+" unknown");			
    		return null;
		}
		if (game==null) {
			logger.warn("conversation "+conversationID+" references unknown game "+conversation.getGameID());
    		response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY, "conversation "+conversationID+" references unknown game "+conversation.getGameID());			
    		return null;			
		}
		if (!game.isSetActive() || !game.getActive()) {
			logger.warn("Game "+game.getID()+" (conversation "+conversationID+") now inactive");
    		response.sendError(HttpServletResponse.SC_GONE, "game "+game.getID()+" now inactive");			
    		return null;			
		}
		if (!conversation.isSetActive() || !conversation.getActive()) {
			logger.warn("conversation "+conversationID+" now inactive");
    		response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY, "conversation "+conversationID+" now inactive");			
    		return null;			
		}
		
		List<Message> responses = handleMessages(conversation, messages);

		
		response.setStatus(200);
    	PrintWriter pw = response.getWriter();
    	xs.toXML(responses, pw);
    	pw.close();
    	
    	return null;
    }
    
	/** internal implementation */
	public List<Message> handleMessages(ClientConversation conversation, List<Message> messages) {
		List<Message> responses = new LinkedList<Message>();
		for (Message message : messages) 
			handleMessage(conversation, message, responses);
		return responses;
	}

	private void handleMessage(ClientConversation conversation, Message message, List<Message> responses) {
		if (message.getType()==null) {
			responses.add(createErrorMessage(message, MessageStatusType.INVALID_REQUEST, "No message type"));
			return;
		}
		if (!message.getType().toServer()) {
			responses.add(createErrorMessage(message, MessageStatusType.INVALID_REQUEST, "Message type "+message.getType()+" is not valid to server"));
			return;
		}
		ISession session = dataspace.getSession();
		session.begin();
		try {
			LinkedList<Message> newResponses = new LinkedList<Message>();

			switch(message.getType()) {

			//case NEW_CONV:// new conversation 
			//case FACT_EX: // fact already exists (matching a subscription)
			//case FACT_ADD: // fact added (matching a subscription)
			//case FACT_UPD: // fact updated (matching a subscription)
			//case FACT_DEL: // fact deleted (matching a subscription)
			//case POLL_RESP: // response to poll (e.g. no. messages still unsent)
			case POLL: // poll request 
				handlePoll(conversation, message, newResponses, session);
				break;
			case ACK: // acknowledge message

			case ADD_FACT:// request to add fact
			case UPD_FACT:// request to update fact
			case DEL_FACT:// request to delete fact
				handleFactOperation(conversation, message, newResponses, session);
				break;
				//ERROR(false, true), // error response, e.g. to add/update/delete request
			case SUBS_EN:// enable a subscription
			case SUBS_DIS:// disable a subscription
				// TODO implement
			default:
				newResponses.add(createErrorMessage(message, MessageStatusType.INTERNAL_ERROR, "Message type "+message.getType()+" not supported"));
				break;
			}

			session.end();
			// OK!
			responses.addAll(newResponses);
		}
		catch (Exception e) {
			logger.warn("Handling message "+message, e);
			responses.add(createErrorMessage(message, MessageStatusType.INTERNAL_ERROR, "Exception: "+e));			
		}
	}
	private void handleFactOperation(ClientConversation conversation, Message message, List<Message> responses, ISession session) throws IllegalArgumentException, ClientAPIException, InstantiationException, IllegalAccessException {

		// ....
		Object oldVal = message.getOldVal();//!=null ? ClientSubscriptionManager.unmarshallFact(message.getOldVal()) : null;
		Object newVal = message.getNewVal();//!=null ? ClientSubscriptionManager.unmarshallFact(message.getNewVal()) : null;

		// APPLICATION-SPECIFIC SPECIAL CASES
		if (newVal instanceof Player) {
			Player newPlayer = (Player)newVal;
			if (newPlayer.getID()==null) {
				// assume this player
				Player player = (Player)session.get(Player.class, conversation.getPlayerID());
				if (player==null) {
					throw new ClientAPIException(MessageStatusType.INTERNAL_ERROR, "conversation Player "+conversation.getPlayerID()+" not found");
				}
				// position update?!
				if (newPlayer.getPosition()!=null) {
					logger.info("Updating position for player "+player.getID()+" to "+newPlayer.getPosition());
					player.setPosition(newPlayer.getPosition());
					player.setPositionUpdateTime(System.currentTimeMillis());
				}
				// nothing else should be updated!
			}
		}

		// generic
		switch (message.getType()) {
		case ADD_FACT: {
			if (oldVal!=null || newVal==null)
				throw new ClientAPIException(MessageStatusType.INVALID_REQUEST, "ADD_FACT with incorrect parameters: old="+oldVal+", new="+newVal);
			session.add(newVal);
			responses.add(createAckMessage(message));
			break;
		}
		case DEL_FACT: {
			if (oldVal==null || newVal!=null)
				throw new ClientAPIException(MessageStatusType.INVALID_REQUEST, "DEL_FACT with incorrect parameters: old="+oldVal+", new="+newVal);
			session.remove(oldVal);
			responses.add(createAckMessage(message));
			break;
		}
		case UPD_FACT: {
			if (oldVal==null || newVal==null)
				throw new ClientAPIException(MessageStatusType.INVALID_REQUEST, "UPD_FACT with incorrect parameters: old="+oldVal+", new="+newVal);
			session.remove(oldVal);
			session.add(newVal);
			// TODO use update if fixed in JPA drools
			responses.add(createAckMessage(message));
			break;
		}
		default:
			throw new RuntimeException("handleFactOperation called for message type "+message.getType());
		}
		// TODO record of client action
	}

	private void handlePoll(ClientConversation conversation, Message message, List<Message> responses, ISession session) {

		long time = System.currentTimeMillis();
		int ackSeq = message.getAckSeq()!=null ? message.getAckSeq() : 0;

		if (ackSeq>0) {
			// ack
			int removed = 0;
			QueryTemplate q = new QueryTemplate(MessageToClient.class);
			//("SELECT x FROM MessageToClient x WHERE x.clientId = :clientId AND x.ackedByClient = 0 AND x.seqNo <= :ackSeq");
			q.addConstraintEq("clientID", conversation.getClientID());
			q.addConstraintEq("ackedByClient", 0); 
			q.addConstraintLe("seqNo", ackSeq);
			Object mtcs[] = session.match(q);
			for (int mi=0; mi<mtcs.length; mi++) {
				MessageToClient mtc = (MessageToClient)mtcs[mi];
				mtc.setAckedByClient(time);

				// delete on ack? - we only have clientLifetime or not at the mo.
				if (!mtc.isSetClientLifetime() || mtc.getClientLifetime()==false) {
					session.remove(mtc);
					removed ++;
				}
			}
			if (mtcs.length>0) {
				logger.info("Acked "+mtcs.length+" messages to "+conversation.getClientID()+" (seq<="+ackSeq+") - "+removed+" removed");
			}
		}

		//ClientConversation cc = em.find(ClientConversation.class, conversation.getConversationId());

//		boolean checkToFollow = true;
		int sentCount = 0;
		int toFollow = 0;
		if (message.getToFollow()==null || message.getToFollow()>0) {
			// actually get some messages...
			// TODO from
			//Query q = em.createQuery ("SELECT x FROM MessageToClient x WHERE x.clientId = :clientId AND x.seqNo> :ackSeq ORDER BY x.seqNo ASC");
			QueryTemplate q = new QueryTemplate(MessageToClient.class);
			q.addConstraintEq("clientID", conversation.getClientID());
			q.addConstraintGt("seqNo", ackSeq);
			q.addOrder("seqNo", false);
			if (message.getToFollow()!=null)
				q.setMaxResults(message.getToFollow());
			Object mtcs [] = session.match(q);
			sentCount = mtcs.length;
			if (message.getToFollow()==null || mtcs.length<message.getToFollow())
				// can't be any left
				; //checkToFollow = false;
			else {
				QueryTemplate q1 = new QueryTemplate(MessageToClient.class);
				q1.addConstraintEq("clientID", conversation.getClientID());
				q1.addConstraintGt("seqNo", ackSeq);
				//Query q = em.createQuery ("SELECT COUNT(x) FROM MessageToClient x WHERE x.clientId = :clientId AND x.seqNo> :ackSeq ");
				int countResult = session.count(q1);
				toFollow = countResult-sentCount;				
			}
			int removed = 0;
			for (int mi=0; mi<mtcs.length; mi++) {
				MessageToClient mtc = (MessageToClient)mtcs[mi];

				Message msg = new Message();
				//int seqNo = cc.getNextSeqNo();
				msg.setSeqNo(mtc.getSeqNo());
				//cc.setNextSeqNo(seqNo+1);
				msg.setType(MessageType.values()[mtc.getType()]);
				if (mtc.getOldVal()!=null)
					msg.setOldVal(ClientSubscriptionManager.unmarshallFact(mtc.getOldVal()));
				if (mtc.getNewVal()!=null)
					msg.setNewVal(ClientSubscriptionManager.unmarshallFact(mtc.getNewVal()));
				//msg.setSubsIx(mtc.getSubsIx());
				//if (mtc.getHandle()!=null)
				//msg.setHandle(mtc.getHandle());
				responses.add(msg);
				// mark sent
				// delete on send?
				//if (mtc.getLifetime()!=null && mtc.getLifetime()==ClientSubscriptionLifetimeType.UNTIL_SENT) {
				//	em.remove(mtc);
				//	removed ++;
				//}
				//else
				mtc.setSentToClient(time);				
			}
			if (removed>0) {
				logger.info("Sent "+mtcs.length+" messages to "+conversation.getClientID()+" (seq>"+ackSeq+") - "+removed+" removed");
			}
		}

		// response
		Message pollResponse = new Message();
		pollResponse.setAckSeq(message.getSeqNo());
		if (message.getSubsIx()!=null)
			pollResponse.setSubsIx(message.getSubsIx());
		pollResponse.setType(MessageType.POLL_RESP);
		pollResponse.setToFollow(toFollow);
		responses.add(pollResponse);
	}
	private Message createErrorMessage(Message message,
			MessageStatusType status, String errorMessage) {
		Message response = new Message();
		response.setAckSeq(message.getSeqNo());
		response.setStatus(status);
		response.setErrorMsg(errorMessage);
		response.setType(MessageType.ERROR);
		logger.warn("Error response to client: " +response);
		return response;
	}
	private Message createAckMessage(Message message/*, String handle*/) {
		Message response = new Message();
		response.setAckSeq(message.getSeqNo());
		response.setType(MessageType.ACK);
//		if (handle!=null)
//			response.setHandle(handle);
//		logger.log(Level.WARNING, "Error response to client: " +response);
		return response;
	}

}
