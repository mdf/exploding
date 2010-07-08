/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import uk.ac.horizon.ug.exploding.client.model.ModelUtils;
import uk.ac.horizon.ug.exploding.client.model.Player;
import uk.ac.horizon.ug.exploding.client.model.Position;
import uk.ac.horizon.ug.exploding.client.model.Zone;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

//import uk.ac.horizon.ug.bluetoothex.testclient.Main;
//import uk.ac.horizon.ug.exserver.clientapi.JsonUtils;
//import uk.ac.horizon.ug.exserver.clientapi.protocol.Message;
//import uk.ac.horizon.ug.exserver.clientapi.protocol.MessageStatusType;
//import uk.ac.horizon.ug.exserver.clientapi.protocol.MessageType;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/** Client stub
 * 
 * @author cmg
 *
 */
public class Client {
	private static final String TAG = "Client";
	static Logger logger = Logger.getLogger(Client.class.getName());
	protected URI conversationUrl;
	protected String clientId;
	protected HttpClient httpClient;
	/**
	 * @param conversationUrl
	 * @param clientId
	 */
	public Client(HttpClient httpClient, URI conversationUrl, String clientId) {
		super();
		this.httpClient = httpClient;
		this.conversationUrl = conversationUrl;
		this.clientId = clientId;
	}
	/**
	 * @param conversationUrl
	 * @param clientId
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	public Client(HttpClient httpClient, String conversationUrl, String clientId) throws MalformedURLException, URISyntaxException {
		super();
		this.httpClient = httpClient;
		this.conversationUrl = new URI(conversationUrl);
		this.clientId = clientId;
	}
	
	public URI getConversationUrl() {
		return conversationUrl;
	}
	public String getClientId() {
		return clientId;
	}

	protected int ackSeq = 0;
	protected int seqNo = 1;
	
	/** connect */
//	public boolean connect(/*List<String> classNames*/) throws /*JSONException,*/ IOException {
//		
//		List<Message> messages = new LinkedList<Message>();
//		// we are a content display device!
//		for (String className : classNames) {
//			int ix = className.lastIndexOf('.');
//			String namespace = ix>=0 ? className.substring(0, ix) : null;
//			String typeName = className.substring(ix+1);
//			JSONObject val = new JSONObject();
//			val.put("typeName", typeName);
//			if(namespace!=null)
//				val.put("namespace", namespace);
//			val.put("id", clientId);
//			
//			messages.add(addFactMessage(val.toString()));
//		}
//		sendMessages(messages);
//		return true;
//	}	
	/** add fact message */
	public Message addFactMessage(Object object) {
		Message msg = new Message();
		msg.setType(MessageType.ADD_FACT.name());
		msg.setSeqNo(seqNo++);
		msg.setNewVal(object);
		return msg;
	}
	/** add fact message */
	public Message updateFactMessage(Object oldVal, Object newVal) {
		Message msg = new Message();
		msg.setType(MessageType.UPD_FACT.name());
		msg.setSeqNo(seqNo++);
		msg.setOldVal(oldVal);
		msg.setNewVal(newVal);
		return msg;
	}
	/** internal async send */
	public List<Message> sendMessage(Message msg) throws IOException {
		List<Message> messages = new LinkedList<Message>();
		messages.add(msg);
		return sendMessages(messages);
	}
	/** internal async send */
	public List<Message> sendMessages(List<Message> messages) throws IOException {
		HttpPost request  = new HttpPost(conversationUrl);
		logger.info("SendMessages to "+request.getURI()+", requestline="+request.getRequestLine());
		request.setHeader("Content-Type", "application/xml");
		//HttpURLConnection conn = (HttpURLConnection) conversationUrl.openConnection();
		XStream xs = new XStream(/*new DomDriver()*/);
		xs.alias("list", LinkedList.class);    	
		xs.alias("message", Message.class);

		// game specific
		ModelUtils.addAliases(xs);
		
		String xml = xs.toXML(messages);
		Log.d(TAG, "Sent: "+xml);
		request.setEntity(new StringEntity(xml));
		HttpResponse reply = httpClient.execute(request);

		StatusLine statusLine = reply.getStatusLine();
		int status = statusLine.getStatusCode();
		if (status!=200) {
			if (reply.getEntity()!=null)
				reply.getEntity().consumeContent();
			throw new IOException("Error response ("+status+") from server: "+statusLine.getReasonPhrase());
		}
		//Log.d(TAG, "Http status on login: "+statusLine);
		messages = (List<Message>)xs.fromXML(reply.getEntity().getContent());
		reply.getEntity().consumeContent();

		logger.info("Response "+messages.size()+" messages: "+messages);

		// check status(es)
		for (Message response : messages) {
			if (response.getType().equals(MessageType.ERROR.name())) {
				throw new IOException("Error response "+response.getStatus()+": "+response.getErrorMsg()+" for request "+response.getAckSeq());
			}
		}

		return messages;
	}
	protected List<Object> facts = new LinkedList<Object>();
	public List<Object> getFacts() {
		return facts;
	}
	public List<Object> getFacts(String typeName) {
		LinkedList<Object> fs = new LinkedList<Object>();
		for (Object fact : facts) {
			if (fact.getClass().getName().equals(typeName))
				fs.add(fact);
		}
		return fs;
	}
	/** poll 
	 * @throws JSONException */
	public List<Message> poll() throws IOException {
		Message msg = new Message();
		msg.setSeqNo(seqNo++);
		msg.setType(MessageType.POLL.name());
		//msg.setToFollow(0);
		msg.setAckSeq(ackSeq);
		
		List<Message> messages = sendMessage(msg);
		if (messages==null)
			return messages;
		
		for (Message message : messages) {
			if (message.getSeqNo()>0 && message.getSeqNo()>ackSeq)
				ackSeq = message.getSeqNo();
			MessageType messageType = MessageType.valueOf(message.getType());
			if (messageType==MessageType.FACT_EX || messageType==MessageType.FACT_ADD) {
				Object val = message.getNewVal();
				String typeName = val.getClass().getName();
				facts.add(val);
			} else if (messageType==MessageType.FACT_UPD || messageType==MessageType.FACT_DEL) {
				Object val = message.getOldVal();
				boolean found = false;
				for (int i=0; i<facts.size(); i++) {
					if (val.equals(facts.get(i))) {
						facts.remove(i);
						found = true;
						logger.info("Removing old fact "+val);
						break;
					}
				}
				if (!found)
					logger.log(Level.WARNING, "Did not find old fact to remove: "+val);
				if (messageType==MessageType.FACT_UPD) {
					val = message.getNewVal();
					facts.add(val);
				}
			}
		}
		return messages;
	}
}
