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
	/** player name */
	private String playerName;
	/** client version */
	private int clientVersion;
	/** client type */
	private String clientType;
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
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(int clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	
}
