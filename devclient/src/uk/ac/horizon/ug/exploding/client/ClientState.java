/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

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
	/** cons */
	public ClientState() {		
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
	public ClientState clone() {
		ClientState copy = new ClientState(clientStatus, gameStatus);
		return copy;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClientState [clientStatus=" + clientStatus + ", gameStatus="
				+ gameStatus + "]";
	}
}
