/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

/**
 * @author cmg
 *
 */
public class LoginMessage {
	/** client ID (IMEI) */
	private String clientId;
	/** conversation ID */
	private String conversationId;
	/** cons */
	public LoginMessage() {		
	}
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * @return the conversationId
	 */
	public String getConversationId() {
		return conversationId;
	}
	/**
	 * @param conversationId the conversationId to set
	 */
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
}
