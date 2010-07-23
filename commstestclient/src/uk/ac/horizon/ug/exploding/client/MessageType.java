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
public enum MessageType {
	NEW_CONV(false, true), // new conversation 
	FACT_EX(false, true), // fact already exists (matching a subscription)
	FACT_ADD(false, true), // fact added (matching a subscription)
	FACT_UPD(false, true), // fact updated (matching a subscription)
	FACT_DEL(false, true), // fact deleted (matching a subscription)
	POLL_RESP(false, true), // response to poll (e.g. no. messages still unsent)
	POLL(true, false), // poll request 
	ACK(true, true), // acknowledge message
	
	ADD_FACT(true, false), // request to add fact
	UPD_FACT(true, false), // request to update fact
	DEL_FACT(true, false), // request to delete fact
	ERROR(false, true), // error response, e.g. to add/update/delete request
	SUBS_EN(true, false), // enable a subscription
	SUBS_DIS(true, false), // disable a subscription
	
	QUERY(true,false), // query/select request (giving template to query object)
	QUERY_RESP(false,true) // query response (giving list of matches)
	;
	MessageType(boolean toServer, boolean toClient) {
		this.toServer = toServer;
		this.toClient = toClient;
	}
	private boolean toServer;
	public boolean toServer() { return toServer; }
	private boolean toClient;
	public boolean toClient() { return toClient; }
}
