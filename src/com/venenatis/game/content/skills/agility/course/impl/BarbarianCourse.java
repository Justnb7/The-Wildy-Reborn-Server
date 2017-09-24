package com.venenatis.game.content.skills.agility.course.impl;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.world.object.GameObject;

public class BarbarianCourse {

	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		if(player.getSkills().getLevel(Skills.AGILITY) < 35) {
			SimpleDialogues.sendStatement(player, "You need an Agility level of 35 to enter this area.");
			return false;
		}
		
		Object barbarian_agility_course = player.getAttribute("barbarian_course");
		
		switch (object.getId()) {
		case 20210:
			if (!player.getLocation().equals(Location.create(2552, 3561, 0)) && !player.getLocation().equals(Location.create(2552, 3558, 0))) {
				player.removeAttribute("busy");
				return false;
			}
			player.setAttribute("busy", true);
			int y = -3;
			Direction dir = Direction.NORTH;
			if (player.getLocation().getY() <= 3558) {
				y = 3;
			}
			player.playAnimation(new Animation(749));
			player.forceMove(new ForceMovement(0, 0, 0, y, 0, 60, 2, dir), true);
			return true;

		case 23131:
			if (!player.getLocation().equals(Location.create(2551, 3554, 0))) {
				player.removeAttribute("busy");
				return false;
			}
			if (barbarian_agility_course == null) {
				player.setAttribute("barbarian_course", 1);
			}
			player.setAttribute("busy", true);
			Agility.animateObject(object, Animation.create(54), 1);
			Agility.animateObject(object, Animation.create(55), 2);
			player.playAnimation(new Animation(751));
			player.forceMove(new ForceMovement(0, 0, 0, -5, 30, 50, 2, Direction.NORTH), true);
			player.getSkills().addExperience(Skills.AGILITY, 22);
			return true;
			
		case 23144:
			if (player.getLocation().getY() != 3546) {
				player.removeAttribute("busy");
				return false;
			}

			if (barbarian_agility_course == null) {
				player.setAttribute("barbarian_course", 2);
			}
			Agility.setRunningToggled(player, false, 12);
			player.forceWalk(Animation.create(762), 2541, 3546, 1, 11, true);
			player.getSkills().addExperience(Skills.AGILITY, 13.7);
			return true;
			
		case 20211:
			if (player.getLocation().getX() != 2539) {
				player.removeAttribute("busy");
				return false;
			}
			if (player.getLocation().getY() >= 3547) {
				player.removeAttribute("busy");
				return false;
			}
			
			if (barbarian_agility_course == null) {
				player.setAttribute("barbarian_course", 3);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX() - 2, 3546, 1), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 8.2);
			return true;
			
		case 23547:
			if (player.getLocation().getY() != 3547) {
				player.removeAttribute("busy");
				return false;
			}

			if (barbarian_agility_course == null) {
				player.setAttribute("barbarian_course", 4);
			}
			
			player.playAnimation(Animation.create(753));
			Agility.setRunningToggled(player, false, 8);
			player.forceWalk(null, 2532, 3546, 4, 2, false);
			player.forceWalk(Animation.create(756), 2532, 3547, 0, 4, false);
			Agility.forceTeleport(player, Animation.create(828), Location.create(2532, 3546, 0), 7, 8);
			player.getSkills().addExperience(Skills.AGILITY, 22);
			return true;
			
		case 1948:
			player.playAnimation(new Animation(839));
			player.forceMove(new ForceMovement(0, 0, 2, 0, 0, 60, 1, Direction.NORTH), true);
			player.getSkills().addExperience(Skills.AGILITY, 13.7);
			
			if (barbarian_agility_course == null && player.getLocation().equals(new Location(2535, 3553, 0))) {
				player.setAttribute("barbarian_course", 5);
			} else if(barbarian_agility_course == null && player.getLocation().equals(new Location(2538, 3553, 0))) {
				player.setAttribute("barbarian_course", 6);
			} else if(barbarian_agility_course == null && player.getLocation().equals(new Location(2541, 3553, 0))) {
				player.setAttribute("barbarian_course", 7);
			}
			if ((Integer)barbarian_agility_course == 7) {
				player.getActionSender().sendMessage("You completed the course!");
				player.getSkills().addExperience(Skills.AGILITY, 46.2);
				player.removeAttribute("barbarianAgilityCourse");
			}
			return true;
			
		}
		
		return false;
	}
	
}
