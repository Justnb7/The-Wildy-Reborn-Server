package com.venenatis.game.content.skills.agility.rooftops;

import com.venenatis.game.content.skills.agility.rooftops.impl.AlKharidRooftop;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the all the rooftop courses.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class Rooftop {
	
	/**
	 * Execute a single rooftop course based on object clicks.
	 * 
	 * @param player
	 *            The player attempting to perform a course
	 * @param object
	 *            The object being clicked
	 */
	public static boolean execute(Player player, GameObject object) {
		if(AlKharidRooftop.start(player, object)) {
			return true;
		}
		return false;
	}

}
