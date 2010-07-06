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
	RUNNING,
	PAUSED,
	STOPPED,
	ERROR_BEFORE_LOGIN,
	ERROR_AFTER_LOGIN
}
