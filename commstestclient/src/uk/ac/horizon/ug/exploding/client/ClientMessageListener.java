/**
 * Copyright 2010 The University of Nottingham
 * 
 * This file is part of genericandroidclient.
 *
 *  genericandroidclient is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  genericandroidclient is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with genericandroidclient.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package uk.ac.horizon.ug.exploding.client;

/**
 * @author cmg
 *
 */
public interface ClientMessageListener {
	/** message response.
	 * @param status Status, e.g. OK
	 * @param errorMessage error message returned from server (if any), or null
	 * @param value value returned from server, if any, else null
	 */
	public void onMessageResponse(MessageStatusType status, String errorMessage, Object value);
}
