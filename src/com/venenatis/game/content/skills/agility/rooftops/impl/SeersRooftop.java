package com.venenatis.game.content.skills.agility.rooftops.impl;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the Seers village rooftop course.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class SeersRooftop {
	
	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		if (player.getSkills().getLevel(Skills.AGILITY) < 60) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 60 to use this course.");
			return false;
		}
		
		Integer seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
		
		switch (object.getId()) {
		case 11373:
			if (seersAgilityCourseLvl == null) {
				player.setAttribute("seersAgilityCourse", 1);
			}
			Agility.forceTeleport(player, Animation.create(737), Location.create(2729, 3488, 1), 0, 2);
			Agility.forceTeleport(player, Animation.create(1118), Location.create(2729, 3490, 3), 2, 4);
			Agility.delayedAnimation(player, Animation.create(-1), 4);
			player.getSkills().addExperience(Skills.AGILITY, 45);
			return true;

		case 11374:
			if (seersAgilityCourseLvl != null) {
				player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
			}
			Agility.forceTeleport(player, Animation.create(2586), Location.create(2719, 3495, 2), 0, 2);
			Agility.delayedAnimation(player, Animation.create(2588), 2);
			Agility.forceTeleport(player, Animation.create(2586), Location.create(2713, 3494, 2), 4, 5);
			Agility.delayedAnimation(player, Animation.create(2588), 5);
			player.getSkills().addExperience(Skills.AGILITY, 20);
			return true;

		case 11378:
			if (seersAgilityCourseLvl != null) {
				player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
			}
			Agility.setRunningToggled(player, false, 9);
			player.forceWalk(Animation.create(762), 2710, 3481, 0, 9, true);
			player.getSkills().addExperience(Skills.AGILITY, 20);
			return true;

		case 11375:
			if (seersAgilityCourseLvl != null) {
				player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
			}
			Agility.setRunningToggled(player, false, 4);
			Agility.forceTeleport(player, Animation.create(-1), Location.create(2710, 3474, 3), 0, 0);
			Agility.forceTeleport(player, Animation.create(2585), Location.create(2710, 3472, 3), 1, 3);
			player.getSkills().addExperience(Skills.AGILITY, 35);
			return true;

		case 11376:
			if (seersAgilityCourseLvl != null) {
				player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
			}
			Agility.setRunningToggled(player, false, 2);
			Agility.forceTeleport(player, Animation.create(2586), Location.create(2702, 3465, 2), 0, 2);
			Agility.delayedAnimation(player, Animation.create(2588), 2);
			player.getSkills().addExperience(Skills.AGILITY, 15);
			return true;

		case 11377:
			Agility.setRunningToggled(player, false, 2);
			Agility.forceTeleport(player, Animation.create(2586), Location.create(2704, 3464, 0), 0, 2);
			Agility.delayedAnimation(player, Animation.create(2588), 2);
			if (player.getAttribute("seersAgilityCourse") != null) {
				Integer courseLevel = player.getAttribute("seersAgilityCourse");
				if (courseLevel == 5) {
					player.getActionSender().sendMessage("You completed the course!");
					player.getSkills().addExperience(Skills.AGILITY, 435);
				}
				player.removeAttribute("seersAgilityCourse");
			}
			return true;
		}
		return false;
	}

}
