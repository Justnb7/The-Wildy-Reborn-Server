package com.venenatis.game.content.skills.agility.rooftops.impl;

import java.util.Random;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.StoppingTick;
import com.venenatis.game.util.Utility;
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

		Integer alKharidRooftop = player.getAttribute("al_kharid_rooftop");
		
		boolean fail = false;
		
		switch (object.getId()) {
		
		/* Climb Rough Wall */
		case 10093:
			if (player.getLocation().getX() != 3273) {
				player.removeAttribute("busy");
				return false;
			}
			Agility.forceTeleport(player, Animation.create(828), Location.create(3273, 3192, 3), 0, 2);
			player.getSkills().addExperience(Skills.AGILITY, 10);
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
			
			if (random.nextInt(10) < 3) {
				fail = true;
			}

			Agility.setRunningToggled(player, false, 10);
			player.forceWalk(Animation.create(762), 3272, 3172, 0, fail ? 5 : 10, fail ? true : false);
			
			if(fail) {
				World.getWorld().schedule(new StoppingTick(4) {

					@Override
					public void executeAndStop() {
						Agility.forceTeleport(player, new Animation(1332), new Location(3272, 3177, 0), 1, 1);
						player.take_hit_generic(player, Utility.random(2), HitType.NORMAL).send();
					}
					
				});
				return false;
			}
			
			player.getSkills().addExperience(Skills.AGILITY, 30);
			return true;

		/* Swing-across Cable */
		case 10355:
			if (player.getLocation().getY() != 3166) {
				player.removeAttribute("busy");
				player.debug("nope");
				return false;
			}
			if (alKharidRooftop == null) {
				player.setAttribute("kharidAgilityCourse", 2);
			}
			
			World.getWorld().schedule(new Task(1) {
				public int tick;
	            @Override
	            public void execute() {
	            	
	            	if(tick == 1) {
	            		player.getActionSender().sendMessage("You begin an almighty run-up...");
	            		//Run towards sling
	        			player.forceWalk(Animation.create(1995), player.getX() + 2, player.getY(), 0, 2, false);
	            	}
					
					if(tick == 4) {
						player.getActionSender().sendMessage("You gained enough momentum to swing to the other side!");
						
						//Swing
						player.playAnimation(new Animation(751));
						
						//Sent player to correct tile with forcemovement
						player.forceMove(new ForceMovement(0, 0, 17, 0, 30, 50, 7, Direction.NORTH), false);
					}
					
					if(tick == 6) {
						player.getSkills().addExperience(Skills.AGILITY, 40);
						this.stop();
					}
		            tick++;
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
			
			if (random.nextInt(10) < 3) {
				fail = true;
			}
			
			final boolean fall_down = fail;
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
						player.forceWalk(Animation.create(1602), player.getX() + 14, player.getY(), 2, fall_down ? 5 : 12, true);
						
						if(fall_down) {
							World.getWorld().schedule(new StoppingTick(4) {

								@Override
								public void executeAndStop() {
									Agility.forceTeleport(player, new Animation(1332), new Location(3307, 3163, 0), 1, 1);
									player.take_hit_generic(player, Utility.random(2), HitType.NORMAL).send();
								}
								
							});
							this.stop();
						}
					}

					if (tick == 14) {
						player.face(new Location(3316, 3163));
						player.getSkills().addExperience(Skills.AGILITY, 40);
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
			player.forceWalk(Animation.create(762), 3302, 3186, 0, 13, true);
			player.getSkills().addExperience(Skills.AGILITY, 15);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 40);
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