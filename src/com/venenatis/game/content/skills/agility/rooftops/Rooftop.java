package com.venenatis.game.content.skills.agility.rooftops;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;

/**
The class which represents functionality for the rooftop agility courses.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public abstract class Rooftop {

	/**
	 * Checks if we have the requirements to start the rooftop course.
	 */
	public abstract boolean requirements(Player player);

	/**
	 * Starts the rooftop course.
	 */
	public abstract boolean start(Player player, GameObject object);

}
