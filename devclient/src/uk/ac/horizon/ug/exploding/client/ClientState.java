/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import uk.ac.horizon.ug.exploding.client.LoginReplyMessage.Status;

/** Client State bundle.
 * 
 * @author cmg
 *
 */
public class ClientState {
	/** client status */
	private ClientStatus clientStatus;
	/** game status */
	private GameStatus gameStatus;
	/** login status */
	private LoginReplyMessage.Status loginStatus = LoginReplyMessage.Status.NOT_DONE;
	/** login message */
	private String loginMessage;
	/** cons */
	public ClientState() {		
	}
	public ClientState(ClientStatus clientStatus, GameStatus gameStatus,
			Status loginStatus, String loginMessage) {
		super();
		this.clientStatus = clientStatus;
		this.gameStatus = gameStatus;
		this.loginStatus = loginStatus;
		this.loginMessage = loginMessage;
	}
	
	public ClientState(ClientStatus clientStatus, GameStatus gameStatus) {
		super();
		this.clientStatus = clientStatus;
		this.gameStatus = gameStatus;
	}
	/**
	 * @return the clientStatus
	 */
	public ClientStatus getClientStatus() {
		return clientStatus;
	}
	/**
	 * @param clientStatus the clientStatus to set
	 */
	public void setClientStatus(ClientStatus clientStatus) {
		this.clientStatus = clientStatus;
	}
	/**
	 * @return the gameStatus
	 */
	public GameStatus getGameStatus() {
		return gameStatus;
	}
	/**
	 * @param gameStatus the gameStatus to set
	 */
	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}
	
	public LoginReplyMessage.Status getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(LoginReplyMessage.Status loginStatus) {
		this.loginStatus = loginStatus;
	}
	public String getLoginMessage() {
		return loginMessage;
	}
	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}
	public ClientState clone() {
		ClientState copy = new ClientState(clientStatus, gameStatus, loginStatus, loginMessage);
		return copy;
	}
	@Override
	public String toString() {
		return "ClientState [clientStatus=" + clientStatus + ", gameStatus="
				+ gameStatus + ", loginMessage=" + loginMessage
				+ ", loginStatus=" + loginStatus + "]";
	}
	
}
