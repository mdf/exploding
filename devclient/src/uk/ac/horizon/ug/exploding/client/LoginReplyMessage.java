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
	private String /*GameStatus*/ gameStatus;
	/** status enum */
	public static enum Status {
		NOT_DONE,
		FAILED,
		OK, 
		OLD_CLIENT_VERSION, BAD_CLIENT_VERSION, 
		UNSUPPORTED_CLIENT_TYPE,
		GAME_NOT_FOUND, 
		FORBIDDEN,
		SERVER_CLOSED
	};
	/** status response */
	private String /*Status*/ status = Status.NOT_DONE.name(); /*Status.NOT_DONE*/
	/** message (to user) */
	private String message;
	/** detail message */
	private String detail;
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
//	/**
//	 * @return the gameStatus
//	 */
//	public GameStatus getGameStatus() {
//		return gameStatus;
//	}
//	/**
//	 * @param gameStatus the gameStatus to set
//	 */
//	public void setGameStatus(GameStatus gameStatus) {
//		this.gameStatus = gameStatus;
//	}
//	public Status getStatus() {
//		return status;
//	}
//	public void setStatus(Status status) {
//		this.status = status;
//	}
	public String getMessage() {
		return message;
	}
	public String getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	@Override
	public String toString() {
		return "LoginReplyMessage [detail=" + detail + ", gameId=" + gameId
				+ ", gameStatus=" + gameStatus + ", message=" + message
				+ ", status=" + status + "]";
	}
	
}
