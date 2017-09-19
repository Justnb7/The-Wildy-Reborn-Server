package com.venenatis.game.content.skills.agility.rooftops;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the Al-kharid rooftop course.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class AlKharidRooftop {

	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		if (player.getSkills().getLevel(Skills.AGILITY) < 20) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 20 to use this course.");
			return false;
		}
		
		Object alKharidRooftop = player.getAttribute("al_kharid_rooftop");
		
		
		switch (object.getId()) {
		
		case 10093:
			Agility.forceTeleport(player, Animation.create(828), Location.create(3273, 3192, 3), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 10);
			return true;
			
		case 10284:
			if (player.getLocation().getX() != 3272) {
				player.removeAttribute("busy");
				return false;
			}
			
			if (alKharidRooftop == null) {
				player.setAttribute("al_kharid_rooftop", 1);
			}
			
			Agility.setRunningToggled(player, false, 10);
			Agility.forceWalkingQueue(player, Animation.create(762), 3272, 3172, 0, 10, true);
			player.getSkills().addExperience(Skills.AGILITY, 30);
			return true;
		}
		player.debug("return false statement");
		return false;
	}

}