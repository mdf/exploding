/**
 * Copyright 2010 The University of Nottingham
 * 
 * This file is part of GenericAndroidClient.
 *
 *  GenericAndroidClient is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GenericAndroidClient is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with GenericAndroidClient.  If not, see <http://www.gnu.org/licenses/>.
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
