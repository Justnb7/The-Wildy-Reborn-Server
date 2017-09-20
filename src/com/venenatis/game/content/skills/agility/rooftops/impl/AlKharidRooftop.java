package com.venenatis.game.content.skills.agility.rooftops.impl;

import java.util.Random;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.rooftops.Rooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.StoppingTick;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the Al-kharid rooftop course.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class AlKharidRooftop {
	
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
		if (player.getSkills().getLevel(Skills.AGILITY) < 20) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 20 to use this course.");
			return false;
		}

		Object alKharidRooftop = player.getAttribute("al_kharid_rooftop");
		
		boolean fail = false;
		
		if(random.nextInt(10) < 3) {
			fail = true;
		}
		
		//TODO It is possible to fail the Cross Tightrope 1 and Teeth-grip Zip Line obstacles during the course, taking 1-2 damage each time.
		
		switch (object.getId()) {
		
		/* Climb Rough Wall */
		case 10093:
			if (player.getLocation().getX() != 3273) {
				player.removeAttribute("busy");
				return false;
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(3273, 3192, 3), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 10);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;

		/* Cross Tightrope 1 */
		case 10284:
			if (player.getLocation().getX() != 3272) {
				player.removeAttribute("busy");
				player.message("bad poss: "+player.getLocation());
				return false;
			}

			if (alKharidRooftop == null) {
				player.setAttribute("al_kharid_rooftop", 1);
			}

			Agility.setRunningToggled(player, false, 10);
			Agility.forceWalkingQueue(player, Animation.create(762), 3272, 3172, 0, 10, true);
			player.getSkills().addExperience(Skills.AGILITY, 30);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					Rooftop.marks_of_grace(player, new Location(3101, 3280, player.getZ()));
				}
				
			});
			return true;

		/* Swing-across Cable */
		case 10355:
			if (player.getLocation().getY() != 3166) {
				player.removeAttribute("busy");
				return false;
			}
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 2);
			}
			int[] forceMovementVars = { 0, 0, 14, 0, 30, 50, 1, 2 };
			int[] forceMovementVars2 = { 0, 0, -2, 0, 30, 50, 1, 1 };
			Agility.forceMovement(player, new Animation(player.getWalkAnimation()), forceMovementVars2, 2, false);
			Agility.forceWalkingQueue(player, Animation.create(1995), player.getX() + 2, player.getY(), 4, 5, true);
			Agility.forceMovement(player, Animation.create(751), forceMovementVars, 7, false);
			
			World.getWorld().schedule(new StoppingTick(14) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 40);
					Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
				}
				
			});
			return true;

		/* Teeth-grip Zip Line */
		case 10356:
			if (player.getLocation().getY() != 3163) {
				player.removeAttribute("busy");
				return false;
			}
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 3);
			}
			
			World.getWorld().schedule(new Task(1) { // <-- here
				public int tick; // now 'tick' is a variable that belongs to the Task instance
	            @Override
	            public void execute() {
	            	if (tick == 1) {
	            		player.playAnimation(new Animation(2586));
					}

					if (tick == 2) {
						player.playAnimation(new Animation(1601));
						player.setTeleportTarget(new Location(3303, 3163, 1));
					}

					if (tick == 3) {
						Agility.forceWalkingQueue(player, Animation.create(1602), player.getX() + 14, player.getY(), 2, 12, true);
					}

					if (tick == 14) {
						player.face(new Location(3316, 3163));
						player.getSkills().addExperience(Skills.AGILITY, 40);
						Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
						this.stop();
					}
		            tick++;
	            }
	        });
			return true;

		/* Swing-across Tropical Tree */
		case 10357:
			//There is none
			player.debug("click");
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 4);
			}
			
			World.getWorld().schedule(new Task(1) { // <-- but inside the { } of Task
				public int tick; // goes outside override.
				
				@Override// <-- outside of this
				public void execute() {
					player.message("balls "+tick);
					if (tick == 1) {
						player.setTeleportTarget(new Location(3317, 3169, 1));
						player.face(new Location(3320, 3169));
					}

					if (tick == 2) {
						player.playAnimation(new Animation(1122));
						player.face(new Location(3317, 3170));
					}

					if (tick == 3) {
						player.playAnimation(new Animation(1124));
					}

					if (tick == 4) {
						player.face(new Location(3317, 3174));
					}

					if (tick == 5) {
						player.playAnimation(new Animation(2586));
					}

					if (tick == 6) {
						player.setTeleportTarget(new Location(3317, 3174, 2));
						stop();
					}
					tick++;
				}

				@Override
				public void onStop() {
					player.getSkills().addExperience(Skills.AGILITY, 10);
					Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
				}
			});
			return true;

		/* Climb Roof Top Beams */
		case 10094:
			if (player.getLocation().getY() != 3179) {
				player.removeAttribute("busy");
				return false;
			}
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 5);
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(3316, 3180, 3), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 5);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 40);
					Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
				}
				
			});
			return true;

		/* Cross Tightrope 2 */
		case 10583:
			if (player.getLocation().getX() != 3313) {
				player.removeAttribute("busy");
				return false;
			}
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 6);
			}
			Agility.setRunningToggled(player, false, 10);
			Agility.forceWalkingQueue(player, Animation.create(762), 3302, 3186, 0, 13, true);
			player.getSkills().addExperience(Skills.AGILITY, 15);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 40);
					Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
				}
				
			});
			return true;

		/* Jump Gap */
		case 10352:
			if (player.getLocation().getY() != 3192) {
				player.removeAttribute("busy");
				return false;
			}
			if (player.getAttribute("kharidAgilityCourse") != null) {
				if ((Integer) player.getAttribute("kharidAgilityCourse") == 6) {
					player.getActionSender().sendMessage("You completed the course!");
				}
				player.removeAttribute("kharidAgilityCourseLvl");
			}
			Agility.forceTeleport(player, Animation.create(2586), Location.create(3299, 3194, 0), 0, 2);
			Agility.delayedAnimation(player, Animation.create(-1), 2);
			player.getSkills().addExperience(Skills.AGILITY, 30);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 40);
					Rooftop.marks_of_grace(player, mark_of_grace_locations(player));
				}
				
			});
			return true;
		}
		
		return false;
	}
	
	
	
	private final static Location mark_of_grace_locations(Player player) {
		Location mark_of_grace_locations[] = { Location.create(3273, 3189, 3),
				Location.create(3267, 3170, 3), Location.create(3286, 3163, 3),
				Location.create(3318, 3163, 1), Location.create(3315, 3176, 2),
				Location.create(3314, 3182, 3), Location.create(3303, 3190, 3),
				Location.create(3301, 3197, 0) };
		
		return mark_of_grace_locations[(int) (Math.random() * mark_of_grace_locations.length)];
	}

}