package com.venenatis.game.content.skills.agility.course;

import com.venenatis.game.content.skills.agility.course.impl.GnomeCourse;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;

public class Course {
	
	/**
	 * Execute a agility course based on object clicks.
	 * 
	 * @param player
	 *            The player attempting to perform a course
	 * @param object
	 *            The object being clicked
	 */
	public static boolean execute(Player player, GameObject object) {
		if(GnomeCourse.start(player, object)) {
			return true;
		}
		return false;
	}

}
