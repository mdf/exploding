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

/** Status of client in relation to server.
 * 
 * @author cmg
 *
 */
public enum ClientStatus {
	CONFIGURING, // before starting 
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
