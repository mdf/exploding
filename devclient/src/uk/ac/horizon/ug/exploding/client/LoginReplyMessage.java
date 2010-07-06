/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

/**
 * @author cmg
 *
 */
public class LoginReplyMessage {
	/** game ID (server internal) - for info */
	private String gameId;
	/** game status */
	private GameStatus gameStatus;
	/** cons */
	public LoginReplyMessage() {		
	}
	/**
	 * @return the gameId
	 */
	public String getGameId() {
		return gameId;
	}
	/**
	 * @param gameId the gameId to set
	 */
	public void setGameId(String gameId) {
		this.gameId = gameId;
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
	
}
