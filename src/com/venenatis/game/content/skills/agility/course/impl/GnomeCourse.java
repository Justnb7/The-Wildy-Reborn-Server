package com.venenatis.game.content.skills.agility.course.impl;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.world.object.GameObject;

public class GnomeCourse {
	
	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		
		Object gnome_agility_course = player.getAttribute("gnome_course");
		
		switch (object.getId()) {
		case 23145:
			if (player.getX() != 2474 && player.getY() != 3436)
				return false;
			
			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 1);
			}
			player.setAttribute("busy", true);
			player.debug("set busy");
			
			player.getActionSender().sendMessage("You walk carefully across the slippery log...");
			player.sendDelayedMessage(8, "... and make it safely to the other side.");
			Agility.setRunningToggled(player, false, 7);
			player.forceWalk(Animation.create(762), 2474, 3429, 1, 7, true);
			player.getSkills().addExperience(Skills.AGILITY, 7.5);
			return true;

		case 23134:
			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 2);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3424, 1), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 7.5);
			return true;
			
		case 23559:
			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 3);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(2473, 3420, 2), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 5);
			return true;
			
		case 23557:
			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 4);
			}
			Agility.setRunningToggled(player, false, 7);
			player.forceWalk(Animation.create(762), 2483, 3420, 1, 7, true);
			player.getSkills().addExperience(Skills.AGILITY, 7.5);
			return true;
			
		case 23560:
			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 5);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(2485, 3419, 0), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 5);
			return true;
			
		case 23135:
			if (player.getLocation().getY() != 3425) {
				player.removeAttribute("busy");
				player.getActionSender().sendMessage("You can't go over the net from here.");
				return false;
			}

			if (gnome_agility_course == null) {
				player.setAttribute("gnome_course", 6);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3427, 0), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 7.5);
			return true;
			
		case 23138:
		case 23139:
			if (player.getAttribute("gnome_course") != null) {
				if ((Integer) player.getAttribute("gnome_course") == 6) {
					player.getActionSender().sendMessage("You completed the course!");
					player.getSkills().addExperience(Skills.AGILITY, 39);
				}
				player.removeAttribute("gnome_course");
			}

			player.playAnimation(new Animation(746));
			player.forceMove(new ForceMovement(0, 2, 0, 5, 45, 100, 1, Direction.NORTH), false);//3

			player.playAnimation(new Animation(748));
			player.forceMove(new ForceMovement(0, 0, 0, 2, 0, 15, 5, Direction.NORTH), true);//1
			
			player.getSkills().addExperience(Skills.AGILITY, 7.5);
			return true;
		}
		
		return false;
	}

}
