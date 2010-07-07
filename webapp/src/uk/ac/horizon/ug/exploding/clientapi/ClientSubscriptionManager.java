/**
 * 
 */
package uk.ac.horizon.ug.exploding.clientapi;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import uk.ac.horizon.ug.exploding.db.ClientConversation;
import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.MessageToClient;
import uk.ac.horizon.ug.exploding.db.Zone;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;

/**
 * @author cmg
 *
 */
public class ClientSubscriptionManager {
	static Logger logger = Logger.getLogger(ClientSubscriptionManager.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
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
    	
    	// TODO initialise initial message/value(s)
    	// TODO abstract/generalise
    	// replicate Zones...
    	Game game = (Game) session.get(Game.class, cc.getGameID());
    	//ContentGroup contentgame.getContentGroupID()
    	QueryTemplate q = new QueryTemplate(Zone.class);
    	q.addConstraintEq("contentGroupID", game.getContentGroupID());
    	Object zones [] = session.match(q);
    	
    	for (int zi=0; zi<zones.length; zi++) {
    		Zone zone = (Zone)zones[zi];
    		insertMessageToClient(cc, MessageType.FACT_EX.ordinal(), false, null, zone, session);
    	}
    	logger.info("Sent "+zones.length+" zones on new conversation");
	}

	public static Object unmarshallFact(String newVal) {
		XStream xs = ClientController.getXStream();
		return xs.fromXML(newVal);
	}

}
