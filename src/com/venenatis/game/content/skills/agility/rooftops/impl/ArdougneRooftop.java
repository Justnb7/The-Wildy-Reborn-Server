package com.venenatis.game.content.skills.agility.rooftops.impl;

import com.venenatis.game.content.skills.agility.rooftops.Rooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the Ardougne rooftop course.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class ArdougneRooftop {

	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		if (player.getSkills().getLevel(Skills.AGILITY) < 90) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 90 to use this course.");
			return false;
		}

		Integer ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
		
		Rooftop.marks_of_grace(player, "ARDOUGNE");

		switch (object.getId()) {

		/* Climb-up Wooden Beams */
		case 11405:
			
			if (ardyAgilityCourseLvl == null) {
				player.setAttribute("ardyAgilityCourse", 1);
			}
			
			player.forceTeleport(Animation.create(740), Location.create(2673, 3298, 1), 0, 1);
			player.forceTeleport(Animation.create(740), Location.create(2673, 3298, 2), 1, 2);
			player.forceTeleport(Animation.create(740), Location.create(2673, 3298, 3), 2, 3);
			player.forceTeleport(Animation.create(2588), Location.create(2671, 3299, 3), 4, 4);
			player.delayedAnimation(Animation.create(-1), 5);
			player.getSkills().addExperience(Skills.AGILITY, 43);
			return true;

		/* Jump Gap */
		case 11406:

			if (ardyAgilityCourseLvl != null) {
				player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
			}
			
			player.forceTeleport(Animation.create(2586), Location.create(2667, 3311, 1), 0, 2);
			player.delayedAnimation(Animation.create(2588), 2);
			player.forceTeleport(Animation.create(2586), Location.create(2665, 3315, 1), 4, 5);
			player.delayedAnimation(Animation.create(2588), 5);
			player.forceTeleport(Animation.create(2586), Location.create(2665, 3318, 3), 6, 8);
			player.delayedAnimation(Animation.create(2588), 8);
			player.getSkills().addExperience(Skills.AGILITY, 65);
			return true;

		/* Walk-on Plank */
		case 11631:
			
			if (ardyAgilityCourseLvl != null) {
				player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
			}
			
			player.setRunningToggled(false, 6);
			player.forceWalk(Animation.create(762), 2656, 3318, 0, 6, true);
			player.getSkills().addExperience(Skills.AGILITY, 50);
			return true;

		/* Jump Gap */
		case 11429:
			
			if (ardyAgilityCourseLvl != null) {
				player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
			}
			
			player.face(Location.create(2653, 3314, 3));
			player.forceTeleport(Animation.create(2586), Location.create(2653, 3314, 3), 0, 2);
			player.delayedAnimation(Animation.create(2588), 2);
			player.getSkills().addExperience(Skills.AGILITY, 21);
			return true;

		/* Jump Gap */
		case 11430:
			
			if (ardyAgilityCourseLvl != null) {
				player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
			}
			
			player.face(Location.create(2651, 3309, 3));
			player.forceTeleport(Animation.create(2586), Location.create(2651, 3309, 3), 0, 2);
			player.delayedAnimation(Animation.create(2588), 2);
			player.getSkills().addExperience(Skills.AGILITY, 28);
			return true;

		/* 	Balance-across Steep roof */	
		case 11633:
			
			if (ardyAgilityCourseLvl != null) {
				player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
			}
			
			player.setRunningToggled(false, 4);
			player.forceWalk(Animation.create(756), 2655, 3297, 0, 3, false);
			player.forceWalk(Animation.create(756), 2656, 3297, 3, 1, true);
			player.delayedAnimation(Animation.create(759), 4);
			player.getSkills().addExperience(Skills.AGILITY, 57);
			return true;

		/* Jump Gap */	
		case 11630:

			player.setRunningToggled(false, 16);
			player.forceTeleport(Animation.create(2586), Location.create(2658, 3298, 1), 0, 2);
			player.delayedAnimation(Animation.create(2588), 2);
			player.forceWalk(Animation.create(819), 2661, 3298, 3, 3, false);
			player.forceTeleport(Animation.create(2586), Location.create(2663, 3297, 1), 6, 8);
			player.delayedAnimation(Animation.create(2588), 8);
			player.forceWalk(Animation.create(819), 2666, 3297, 9, 4, false);
			player.forceTeleport(Animation.create(2586), Location.create(2667, 3297, 1), 12, 13);
			player.delayedAnimation(Animation.create(2588), 13);
			player.forceTeleport(Animation.create(2586), Location.create(2668, 3297, 0), 15, 16);
			player.delayedAnimation(Animation.create(2588), 16);
			Integer courseLevel = player.getAttribute("ardyAgilityCourse");
			if (player.getAttribute("ardyAgilityCourse") != null) {
				if (courseLevel == 6) {
					player.getActionSender().sendMessage("You completed the course!");
					player.getSkills().addExperience(Skills.AGILITY, 529);
				}
				player.removeAttribute("ardyAgilityCourse");
			}
			return true;
		}
		return false;
	}

}
