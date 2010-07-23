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
package uk.ac.horizon.ug.exploding.client.model;

import com.thoughtworks.xstream.XStream;

/**
 * @author cmg
 *
 */
public class ModelUtils {
	public static void addAliases(XStream xs) {
		xs.alias("zone", Zone.class);
		xs.alias("position", Position.class);
		xs.alias("player", Player.class);
		xs.alias("member", Member.class);
		xs.alias("msg", Message.class);
		xs.alias("game", Game.class);
	}
}
