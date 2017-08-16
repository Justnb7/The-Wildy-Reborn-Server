package com.venenatis.game.content.skills.agility;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;

public class GnomeAgilityCourse extends Agility {
	
	public static void obstacle(GameObject object) {
		switch(object.getId()) {
		
		}
	}
	
	private final void gnomeLogBalance(Player player) {
		if(player.getLocation().getX() != 2474) {
			player.removeAttribute("busy");
			return;
		}
		int gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 0) {
			player.setAttribute("gnomeAgilityCourse", 1);
		}
		Agility.setRunningToggled(player, false, 7);
		Agility.forceWalkingQueue(player, Animation.create(762), 2474, 3429, 1, 7, true);
		player.getSkills().addExperience(Skills.AGILITY, 7);
	}
	
	/*function gnomeLogBalance(player, obstacle, object) {
		if(player.getLocation().getX() != 2474) {
			player.removeAttribute("busy");
			return;
		}
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == null) {
			player.setAttribute("gnomeAgilityCourse", 1);
		}
		Agility.setRunningToggled(player, false, 7);
		Agility.forceWalkingQueue(player, Animation.create(762), 2474, 3429, 1, 7, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeObstacleNet(player, obstacle, object) {
		player.face(player, Location.create(player.getLocation().getX(), 0, 0));
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 1) {
			player.setAttribute("gnomeAgilityCourse", 2);
		}
		Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3424, 1), 0, 2);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeTreeBranch(player, obstacle, object) {
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 2) {
			player.setAttribute("gnomeAgilityCourse", 3);
		}
		Agility.forceTeleport(player, Animation.create(828), Location.create(2473, 3420, 2), 0, 2);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeBalanceRope(player, obstacle, object) {
		if(!player.getLocation().equals(Location.create(2477, 3420, 2))) {
			player.removeAttribute("busy");
			return;
		}
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 3) {
			player.setAttribute("gnomeAgilityCourse", 4);
		}
		Agility.setRunningToggled(player, false, 7);
		Agility.forceWalkingQueue(player, Animation.create(762), 2483, 3420, 1, 7, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeTreeBranch2(player, obstacle, object) {
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 4) {
			player.setAttribute("gnomeAgilityCourse", 5);
		}
		Agility.forceTeleport(player, Animation.create(828), Location.create(2485, 3419, 0), 0, 2);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeObstacleNet2(player, obstacle, object) {
		if(player.getLocation().getY() != 3425) {
			player.removeAttribute("busy");
			player.getActionSender().sendMessage("You can't go over the net from here.");
			return;
		}
		player.face(player, Location.create(player.getLocation().getX(), 9999, 0));
		var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
		if(gnomeAgilityCourseLvl == 5) {
			player.setAttribute("gnomeAgilityCourse", 6);
		}
		Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3427, 0), 0, 2);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}

	function gnomeObstaclePipe(player, obstacle, object) {
		if(!player.getLocation().equals(Location.create(2484, 3430, 0)) && !player.getLocation().equals(Location.create(2487, 3430, 0))) {
			player.removeAttribute("busy");
			return;
		}
		if(player.getAttribute("gnomeAgilityCourse") != null) {
			var courseLevel = player.getAttribute("gnomeAgilityCourse");
			if(courseLevel == 6) {
				player.getActionSender().sendMessage("You completed the course!");
				player.getSkills().addExperience(Skills.AGILITY, 40);
			}
	        player.removeAttribute("gnomeAgilityCourse");
		}
		var forceMovementVars =  [ 0, 2, 0, 5, 45, 100, 0, 3 ];
		var forceMovementVars2 =  [ 0, 0, 0, 2, 0, 15, 0, 1 ];
		Agility.forceMovement(player, Animation.create(746), forceMovementVars, 1, false);
		Agility.forceMovement(player, Animation.create(748), forceMovementVars2, 5, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	}*/

}