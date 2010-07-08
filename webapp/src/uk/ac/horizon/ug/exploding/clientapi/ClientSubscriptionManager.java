/**
 * 
 */
package uk.ac.horizon.ug.exploding.clientapi;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import uk.ac.horizon.ug.exploding.db.ClientConversation;
import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.MessageToClient;
import uk.ac.horizon.ug.exploding.db.Zone;

import equip2.core.DataspaceObjectEvent;
import equip2.core.DataspaceObjectsEvent;
import equip2.core.IDataspace;
import equip2.core.IDataspaceObjectsListener;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;

/**
 * @author cmg
 *
 */
public class ClientSubscriptionManager implements IDataspaceObjectsListener {
	static Logger logger = Logger.getLogger(ClientSubscriptionManager.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
		initialiseListeners();
	}

	private void initialiseListeners() {
		// TODO Auto-generated method stub
		logger.info("Intialising ClientSubscriptionManager listener(s)");
		// unhandled messages
		QueryTemplate q= new QueryTemplate(uk.ac.horizon.ug.exploding.db.Message.class);
		// TODO unhandled
		// add only!
		dataspace.getEventManagement().addIDataspaceObjectsListener(this, q, DataspaceObjectsEvent.OBJECT_ADDED_MASK | DataspaceObjectsEvent.LISTENER_ADDED_MASK );
		// Members
		q = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Member.class);
		// add/update/delete
		dataspace.getEventManagement().addIDataspaceObjectsListener(this, q);
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	/** insert a new MessageToClient */
	static void insertMessageToClient(ClientConversation conversation,
			int type, boolean clientLifetime, Object oldVal, Object newVal, ISession session) {
		try {
			MessageToClient msg = new MessageToClient();
			int seqNo = conversation.getNextSeqNo();
			msg.setID(IDAllocator.getNewID(session, MessageToClient.class, "MC", null));
			msg.setClientID(conversation.getClientID());
			msg.setConversationID(conversation.getID());
			msg.setGameID(conversation.getGameID());
			msg.setSeqNo(seqNo);
			msg.setClientLifetime(clientLifetime);
			msg.setTime(System.currentTimeMillis());
			msg.setType(type);
			if (oldVal!=null) {
				if (oldVal instanceof String)
					// alredy marshalled
					msg.setOldVal((String)oldVal);
				else
					msg.setOldVal(marshallFact(oldVal));
			}
			if (newVal!=null) {
				if (newVal instanceof String)
					// alredy marshalled
					msg.setNewVal((String)newVal);
				else
					msg.setNewVal(marshallFact(newVal));
			}
			msg.setAckedByClient(0L);
			msg.setSentToClient(0L);
			session.add(msg);
			conversation.setNextSeqNo(seqNo+1);
			//em.merge(conversation);
			logger.info("Added message: "+msg);
		} catch (Exception e)  {
			logger.error("Unable to create/insert message to client", e);			
		}
	}

	private static String marshallFact(Object oldVal) {
		XStream xs = ClientController.getXStream();
		return xs.toXML(oldVal);
	}

	public void handleConversationEnd(ClientConversation cc, ISession session) {
		// remove old messages (unless lifetime = client)
		QueryTemplate q = new QueryTemplate(MessageToClient.class);
		q.addConstraintEq("conversationID", cc.getID());
		q.addConstraintEq("clientLifetime", false);
		Object mtcs [] = session.match(q);
		for (int i=0; i<mtcs.length; i++) {
			MessageToClient mtc = (MessageToClient)mtcs[i];
			logger.debug("Remove old MessageToClient "+mtc.getID());
			session.remove(mtcs[i]);
		}
		if (mtcs.length>0)
			logger.info("Removed "+mtcs.length+" messages from now inactive conversation "+cc.getID());
	}

	public void handleConversationStart(ClientConversation cc, ISession session) {
		// TODO Auto-generated method stub
    	insertMessageToClient(cc, MessageType.NEW_CONV.ordinal(), false, null, null, session);
    	
    	// adopt any old messages for this clientId and sessionId
		QueryTemplate q = new QueryTemplate(MessageToClient.class);
		q.addConstraintEq("gameID", cc.getGameID());
		q.addConstraintEq("clientID", cc.getClientID());
		q.addConstraintEq("clientLifetime", true);
		Object mtcs [] = session.match(q);
		for (int i=0; i<mtcs.length; i++) {
			MessageToClient mtc = (MessageToClient)mtcs[i];
			logger.debug("Adopt old MessageToClient "+mtc.getID());
			mtc.setConversationID(cc.getID());
		}
		if (mtcs.length>0)
			logger.info("Adopted "+mtcs.length+" messages from inactive conversations into conversation "+cc.getID());
    	
		handleSubscriptions(cc, session);
	}
	private void handleSubscriptions(ClientConversation cc, ISession session) {

		// TODO abstract/generalise
    	// replicate Zones...
    	Game game = (Game) session.get(Game.class, cc.getGameID());
    	//ContentGroup contentgame.getContentGroupID()
    	QueryTemplate q = new QueryTemplate(Zone.class);
    	q.addConstraintEq("contentGroupID", game.getContentGroupID());
    	sendInitialValues(cc, session, q);
    	
    	// initial messages
    	// TODO unhandled only, time ordered
    	q = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Message.class);
    	q.addConstraintEq("playerID", cc.getPlayerID());
    	// TODO mark as handled
    	sendInitialValues(cc, session, q);
    	
    	// initial members of game
    	q = new QueryTemplate(Member.class);
    	q.addConstraintEq("gameID", cc.getGameID());
    	sendInitialValues(cc, session, q);
	}

	private void sendInitialValues(ClientConversation cc, ISession session,
			QueryTemplate q) {
    	Object match[] = session.match(q);
    	for (int zi=0; zi<match.length; zi++) {
    		Object m = match[zi];
    		insertMessageToClient(cc, MessageType.FACT_EX.ordinal(), false, null, m, session);
    	}
    	logger.info("Sent "+match.length+" "+q.getQueryClass().getName()+" on new conversation");
	}

	public static Object unmarshallFact(String newVal) {
		XStream xs = ClientController.getXStream();
		return xs.fromXML(newVal);
	}

	@Override
	public void objectsChanged(DataspaceObjectsEvent dose) {
		Enumeration en = dose.getDataspaceObjectEvents();
		while (en.hasMoreElements()) {
			DataspaceObjectEvent doe = (DataspaceObjectEvent)en.nextElement();
			switch(doe.getApparentChange()) {
			case DataspaceObjectEvent.ADDED:
				handleSessionEvent(MessageType.FACT_ADD, null, doe.getNewValue());
				break;
			case DataspaceObjectEvent.MODIFIED:
				handleSessionEvent(MessageType.FACT_UPD, doe.getOldValue(), doe.getNewValue());
				break;
			case DataspaceObjectEvent.REMOVED:
				handleSessionEvent(MessageType.FACT_DEL, doe.getOldValue(), null);
				break;				
			}
		}
	}

	private void handleSessionEvent(MessageType type, Object oldObject,
			Object newObject) {
		// TODO Auto-generated method stub
		ISession session = dataspace.getSession();
		session.begin();
		try {
			// not sure how enums are mapped...
    		QueryTemplate q = new QueryTemplate(ClientConversation.class);
    		//em.createQuery ("SELECT x FROM ClientConversation x WHERE x.status = :status");
    		q.addConstraintEq("active", true);
    		//q.setParameter("status", ConversationStatus.ACTIVE);
    		Object conversations [] = session.match(q);
			
			// each client type...
    		String oldValue = null, newValue = null;

    		// client independent match first
    		// should only get relevant classes!
    		//if (!matches(pattern, oldObject!=null ? oldObject : newObject, null, droolsSession.getKsession().getKnowledgeBase()))
    		//continue;
					
    		// each client...
    		for (int ci=0; ci<conversations.length; ci++) {
    			ClientConversation conversation  = (ClientConversation)conversations[ci];
    			// client-dependent match
    			Object matchObject = oldObject!=null ? oldObject : newObject;
				if (!matches(matchObject, conversation))
						continue;
				// marshall on demand
				if (oldObject!=null && oldValue==null) 
					oldValue = marshallFact(oldObject);
				if (newObject!=null && newValue==null)
					newValue = marshallFact(newObject);
				// add message
				boolean clientLifetime = false;
				// APPLICATION-SPECIFIC
				if (matchObject instanceof uk.ac.horizon.ug.exploding.db.Message) {
					uk.ac.horizon.ug.exploding.db.Message message = (uk.ac.horizon.ug.exploding.db.Message)matchObject;
					// TODO handled
					session.addOrUpdate(message);
					clientLifetime = true;
				}
				// END APPLICATION-SPECIFIC
				insertMessageToClient(conversation, type.ordinal(), clientLifetime, oldValue, newValue, session);
    		}
    		session.end();
		}
		catch (Exception e) {
			session.abort();
			logger.warn("Problem handling session event", e);
		}
	}

	private boolean matches(Object matchObject, ClientConversation conversation) {
		// APPLICATION-SPECIFIC
		if (matchObject instanceof uk.ac.horizon.ug.exploding.db.Message) {
			uk.ac.horizon.ug.exploding.db.Message message = (uk.ac.horizon.ug.exploding.db.Message)matchObject;
			if (message.getPlayerID()!=null && message.getPlayerID().equals(conversation.getPlayerID()))
				return true;
			return false;
		}
		if (matchObject instanceof Member) {
			Member member = (Member)matchObject;
			if (member.getGameID()!=null && member.getGameID().equals(conversation.getGameID()))
				return true;
			return false;
		}
		logger.warn("Matches for unknown class "+matchObject.getClass().getName());
		return false;
	}

}
