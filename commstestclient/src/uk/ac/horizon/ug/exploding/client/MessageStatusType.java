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
public enum MessageStatusType {
	OK, // not an error
	INVALID_REQUEST, // request not well-formed
	NOT_PERMITTED, // no permission, probably not going to change
	INTERNAL_ERROR, // e.g. exception
	NOT_FOUND, // probably not going to change
	TOO_EARLY, // e.g. before start of session
	TOO_LATE, // e.g. after close of session
	SERVER_BUSY, // overload - hopefully temporary
	REDIRECT_SERVER, // to a fail-over server?!
	REDIRECT_LOBBY, // go back to the lobby and restart...
	CANCELLED_BEFORE_SEND, // client-side cancel - cannot have reached server
	CANCELLED_AFTER_SEND, // client-side cancel, may or may not have reached server
	NETWORK_ERROR, // client-side error, may or may not have reached server
}
