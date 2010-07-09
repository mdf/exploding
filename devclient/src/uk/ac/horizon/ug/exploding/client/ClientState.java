/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.HashSet;
import java.util.Set;

import android.location.Location;
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
	/** status changed */
	private boolean statusChanged;
	/** last location */
	private Location lastLocation;
	/** location changed */
	private boolean locationChanged;
	/** current zone */
	private String zoneID;
	/** zone changed */
	private boolean zoneChanged;
	/** listener flags */
	public enum Part {
		STATUS(1), LOCATION(2), ZONE(4), ALL(7);
		private int flag;
		Part(int flag) {
			this.flag = flag;
		}
		public int flag() { return flag; }
	}
	/** server client - for access to cached state */
	private Client cache;
	/** cached types changed */
	private Set<String> changedTypes = new HashSet<String>();
	/** cons */
	public ClientState() {		
	}
	
	public ClientState(ClientStatus clientStatus, GameStatus gameStatus,
			Status loginStatus, String loginMessage, boolean statusChanged,
			Location lastLocation, boolean locationChanged, String zoneID,
			boolean zoneChanged, Client cache, Set<String> changedTypes) {
		super();
		this.clientStatus = clientStatus;
		this.gameStatus = gameStatus;
		this.loginStatus = loginStatus;
		this.loginMessage = loginMessage;
		this.statusChanged = statusChanged;
		this.lastLocation = lastLocation;
		this.locationChanged = locationChanged;
		this.zoneID = zoneID;
		this.zoneChanged = zoneChanged;
		this.cache = cache;
		// copy
		this.changedTypes.addAll(changedTypes);
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
		statusChanged = true;
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
		statusChanged = true;
	}
	
	public LoginReplyMessage.Status getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(LoginReplyMessage.Status loginStatus) {
		this.loginStatus = loginStatus;
		statusChanged = true;
	}
	public String getLoginMessage() {
		return loginMessage;
	}
	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
		statusChanged = true;
	}
	
	public boolean isStatusChanged() {
		return statusChanged;
	}
	public void setStatusChanged(boolean statusChanged) {
		this.statusChanged = statusChanged;
	}
	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
		locationChanged = true;
	}

	public boolean isLocationChanged() {
		return locationChanged;
	}

	public void setLocationChanged(boolean locationChanged) {
		this.locationChanged = locationChanged;
	}

	public String getZoneID() {
		return zoneID;
	}

	public void setZoneID(String zoneID) {
		this.zoneID = zoneID;
		zoneChanged = true;
	}

	public boolean isZoneChanged() {
		return zoneChanged;
	}

	public void setZoneChanged(boolean zoneChanged) {
		this.zoneChanged = zoneChanged;
	}

	public Client getCache() {
		return cache;
	}

	public void setCache(Client cache) {
		this.cache = cache;
	}

	public Set<String> getChangedTypes() {
		return changedTypes;
	}

	public void setChangedTypes(Set<String> changedTypes) {
		this.changedTypes = changedTypes;
	}

	public ClientState clone() {
		ClientState copy = new ClientState(clientStatus, gameStatus, loginStatus, loginMessage, statusChanged, lastLocation, locationChanged,zoneID, zoneChanged, cache, changedTypes);
		statusChanged = false;
		locationChanged = false;
		zoneChanged = false;
		changedTypes.clear();
		return copy;
	}

	@Override
	public String toString() {
		return "ClientState [cache=" + cache + ", changedTypes=" + changedTypes
				+ ", clientStatus=" + clientStatus + ", gameStatus="
				+ gameStatus + ", lastLocation=" + lastLocation
				+ ", locationChanged=" + locationChanged + ", loginMessage="
				+ loginMessage + ", loginStatus=" + loginStatus
				+ ", statusChanged=" + statusChanged + ", zoneChanged="
				+ zoneChanged + ", zoneID=" + zoneID + "]";
	}
	
}
