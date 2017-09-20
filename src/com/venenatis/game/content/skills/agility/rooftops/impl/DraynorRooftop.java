package com.venenatis.game.content.skills.agility.rooftops.impl;

import java.util.Random;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.rooftops.Rooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.impl.StoppingTick;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the Draynor village rooftop course.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class DraynorRooftop {
	
	/**
	 * The random number generator
	 */
	private final static Random random = new Random();
	
	/**
	 * The initialize method
	 * 
	 * @param player
	 *            The player attempting to climb the roof
	 * @param object
	 *            The object being clicked
	 */
	public static boolean start(Player player, GameObject object) {
		if (player.getSkills().getLevel(Skills.AGILITY) < 10) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 10 to use this course.");
			return false;
		}
		
		Object draynor_village_rooftop = player.getAttribute("draynor_rooftop");
		
        boolean fail = false;
		
		if(random.nextInt(10) < 3) {
			fail = true;
		}
		
		//TODO It's possible to fail the Cross Tightrope 1 and Balance Narrow Wall obstacles during the course.
		
		switch(object.getId()) {
		/* Climb Rough Wall */
		case 10073:
			if (draynor_village_rooftop == null) {
				player.setAttribute("draynor_rooftop", 1);
			}
			
			Agility.forceTeleport(player, Animation.create(828), Location.create(3102, 3279, 3), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 5);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;
			
		/* Cross Tightrope 1 */
		case 10074:
			if (player.getLocation().getY() != 3277) {
				player.removeAttribute("busy");
				return false;
			}

			if (draynor_village_rooftop != null) {
				player.setAttribute("draynor_rooftop", (Integer)draynor_village_rooftop + 1);
			}
			
			Agility.setRunningToggled(player, false, 12);
			Agility.forceWalkingQueue(player, Animation.create(762), 3090, 3277, 0, 10, false);
			Agility.forceWalkingQueue(player, Animation.create(762), 3090, 3276, 10, 2, true);
			player.getSkills().addExperience(Skills.AGILITY, 8);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3089, 3274, player.getZ()));
				}
				
			});
			return true;
			
		/* Cross Tightrope 2 */
		case 10075:
			if (player.getLocation().getY() != 3276) {
				player.removeAttribute("busy");
				return false;
			}
			
			if (draynor_village_rooftop != null) {
				player.setAttribute("draynorAgilityCourse", (Integer)draynor_village_rooftop + 1);
			}
			
			Agility.setRunningToggled(player, false, 12);
			Agility.forceWalkingQueue(player, Animation.create(762), 3092, 3276, 0, 2, false);
			Agility.forceWalkingQueue(player, Animation.create(762), 3092, 3267, 2, 10, true);
			player.getSkills().addExperience(Skills.AGILITY, 7);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3094, 3267, player.getZ()));
				}
				
			});
			return true;
			
		/* Balance Narrow Wall */
		case 10077:
			if (player.getLocation().getX() != 3089) {
				player.removeAttribute("busy");
				return false;
			}

			if (draynor_village_rooftop != null) {
				player.setAttribute("draynorAgilityCourse", (Integer)draynor_village_rooftop + 1);
			}
			
			Agility.setRunningToggled(player, false, 4);
			Agility.forceWalkingQueue(player, Animation.create(756), 3089, 3262, 0, 3, false);
			Agility.forceWalkingQueue(player, Animation.create(756), 3088, 3261, 3, 1, true);
			Agility.delayedAnimation(player, Animation.create(759), 4);
			player.getSkills().addExperience(Skills.AGILITY, 7);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3094, 3267, player.getZ()));
				}
				
			});
			return true;
			
		/* Jump-up Wall */
		case 10084:
			if (player.getLocation().getY() != 3257) {
				player.removeAttribute("busy");
				return false;
			}

			if (draynor_village_rooftop != null) {
				player.setAttribute("draynorAgilityCourse", (Integer)draynor_village_rooftop + 1);
			}
			
			Agility.setRunningToggled(player, false, 4);
			Agility.forceWalkingQueue(player, Animation.create(2585), 3088, 3256, 0, 2, false);
			Agility.forceWalkingQueue(player, Animation.create(2585), 3088, 3255, 2, 1, true);
			player.getSkills().addExperience(Skills.AGILITY, 10);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;
			
		/* Jump Gap */
		case 10085:
			if (player.getLocation().getX() != 3094) {
				player.removeAttribute("busy");
				return false;
			}

			if (draynor_village_rooftop != null) {
				player.setAttribute("draynorAgilityCourse", (Integer)draynor_village_rooftop + 1);
			}
			
			Agility.delayedAnimation(player, Animation.create(2588), 0);
			Agility.forceTeleport(player, Animation.create(-1), Location.create(3096, 3256, 3), 0, 0);
			player.getSkills().addExperience(Skills.AGILITY, 4);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;
			
		/* Climb-down Crate */
		case 10086:
			if (player.getLocation().getY() != 3261) {
				player.removeAttribute("busy");
				return false;
			}
			
			if (player.getAttribute("draynorAgilityCourse") != null) {
				if ((Integer) player.getAttribute("draynorAgilityCourse") == 6) {
					player.getActionSender().sendMessage("You completed the course!");
				}
				player.removeAttribute("draynorAgilityCourse");
			}
			
			Agility.forceTeleport(player, Animation.create(2586), Location.create(3102, 3261, 1), 0, 2);
			Agility.delayedAnimation(player, Animation.create(2588), 2);
			Agility.forceTeleport(player, Animation.create(2586), Location.create(3103, 3261, 0), 4, 5);
			Agility.delayedAnimation(player, Animation.create(2588), 5);
			player.getSkills().addExperience(Skills.AGILITY, 79);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;
		}
		
		return false;
	}

}
