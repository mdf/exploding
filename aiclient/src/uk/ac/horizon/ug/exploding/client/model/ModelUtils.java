/**
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
	}
}
