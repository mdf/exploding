/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

/** Status of client in relation to server.
 * 
 * @author cmg
 *
 */
public enum ClientStatus {
	NEW, // started - no activity
	LOGGING_IN, // initial request
	GETTING_STATE,
	CANCELLED_BY_USER,
	IDLE,
	POLLING,
	PAUSED,
	STOPPED,
	ERROR_IN_SERVER_URL,
	ERROR_DOING_LOGIN,
	ERROR_GETTING_STATE,
	ERROR_AFTER_STATE
}
