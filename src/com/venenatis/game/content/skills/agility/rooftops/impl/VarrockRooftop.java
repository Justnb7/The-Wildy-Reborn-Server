package com.venenatis.game.content.skills.agility.rooftops.impl;

import java.util.Random;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.rooftops.Rooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.StoppingTick;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.PathFinder;
import com.venenatis.game.world.pathfinder.impl.DefaultPathFinder;

/**
 * The class which represents functionality for the Draynor village rooftop course.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class VarrockRooftop {
	
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
		if (player.getSkills().getLevel(Skills.AGILITY) < 30) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 30 to use this course.");
			return false;
		}
		
		Object varrock_rooftop_course = player.getAttribute("varrock_rooftop");
		
		//TODO It's possible to fail during Cross Clothes Line and Balance Wall and get inflicted with 5-7 and 3-4 damage respectively.
		
		boolean fail = false;
		
		switch(object.getId()) {
		/* Climb Rough Wall */
		case 10586:
			if (varrock_rooftop_course == null) {
				player.setAttribute("varrock_rooftop", 1);
			}
			
			Agility.forceTeleport(player, Animation.create(828), Location.create(3220, 3414, 3), 0, 2);
			Agility.forceTeleport(player, Animation.create(2585), Location.create(3219, 3414, 3), 4, 5);
			Agility.delayedAnimation(player, Animation.create(-1), 1);
			
			World.getWorld().schedule(new StoppingTick(1) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 12);
					Rooftop.marks_of_grace(player, new Location(3219, 3412, 3));
				}
				
			});
			return true;
			
		/* Cross Clothes Line */	
		case 10587:
			if (varrock_rooftop_course == null) {
				player.setAttribute("varrock_rooftop", 2);
			}
			
			if (random.nextInt(10) < 3) {
				fail = true;
			}
			
			final boolean fall_down = fail;
			World.getWorld().schedule(new Task(1) {
				public int tick = 0;
	            @Override
	            public void execute() {
	            	//Start instantly
	            	if (tick == 0) {
	            		Agility.forceTeleport(player, Animation.create(741), Location.create(3212, 3414, 3), 0, 0);
					}
	            	
	            	//1 second later
					if (tick == 3) {
						//When we fail we take 5 to 7 damage
						if(fall_down) {
							Agility.forceTeleport(player, new Animation(1332), new Location(3210, 3414, 0), 0, 0);
							player.take_hit_generic(player, Utility.random(5, 7), HitType.NORMAL).send();
							this.stop();
						}
						
						//We only continue to move forward if we did not fail
						if (!fall_down)
							Agility.forceTeleport(player, Animation.create(741), Location.create(3210, 3414, 3), 0, 1);
					}

					//1 second later
					if (tick == 5) {
						Agility.forceTeleport(player, Animation.create(741), Location.create(3208, 3414, 3), 0, 1);
					}
					
					//Reward
					if(tick == 6) {
						player.getSkills().addExperience(Skills.AGILITY, 21);
						Rooftop.marks_of_grace(player, new Location(3205, 3414, player.getZ()));
						this.stop();
					}
		            tick++;
	            }
	        });
			return true;
		/* Leap Gap 1 */	
		case 10642:
			player.face(new Location(3201, 3416, 3));
			if (varrock_rooftop_course == null) {
				player.setAttribute("varrock_rooftop", 2);
			}
			Agility.forceTeleport(player, Animation.create(2586), Location.create(3197, 3416, 1), 0, 1);
			Agility.delayedAnimation(player, Animation.create(-1), 1);
			
			World.getWorld().schedule(new StoppingTick(2) {

				@Override
				public void executeAndStop() {
					player.getSkills().addExperience(Skills.AGILITY, 17);
					Rooftop.marks_of_grace(player, new Location(3195, 3416, 1));
				}
				
			});
			return true;
		/* Balance Wall */	
		case 10777:
			if (varrock_rooftop_course == null) {
				player.setAttribute("varrock_rooftop", 2);
			}
			
			//startX
			//startY
			//endX
			//endY
	        //durationX
			//durationY
			//direction
			player.face(player.getLocation().transform(-1, 0));
			
			final boolean fall = fail;
			World.getWorld().schedule(new Task(1) {
				public int tick = 0;
				int[] forceMovementVars_1 = { 0, 0, -1, 0, 15, 45, 3 };
				int[] forceMovementVars_2 = { 0, 0, -3, -2, 25, 30, 3 };
				int[] forceMovementVars_3 = { 0, 0, 0, -1, 34, 52, 3 };
				int[] forceMovementVars_4 = { 0, 0, 2, -1, 5, 30, 2 };
	            @Override
	            public void execute() {
	            	
	            	if (tick == 0) {
	        			//Agility.forceMovement(player, new Animation(1995, 15), forceMovementVars_1, 0, true);
					}
	            	
	            	if (tick == 1) {
						//Agility.forceMovement(player, new Animation(2583, 20), forceMovementVars_2, 0, true);
					}

	            	//handlebars one
					if (tick == 2) {
						//Agility.forceMovement(player, new Animation(1122), forceMovementVars_3, 0, true);
					}
					
					//handlebars two
					if (tick == 4) {
						//Agility.forceMovement(player, new Animation(1122), forceMovementVars_3, 0, true);
					}
					
					//handlebars three
					if (tick == 5) {
						//Agility.forceMovement(player, new Animation(1122), forceMovementVars_3, 0, true);
					}
					
					//handlebars four
					if (tick == 8) {
						//Agility.forceMovement(player, new Animation(1122), forceMovementVars_3, 0, true);
					}
					
					//handlebars five
					if (tick == 10) {
						//Agility.forceMovement(player, new Animation(1122), forceMovementVars_3, 0, true);
						// suspecting 0 value for ticks might break this
					}
					
					if(tick == 12) {
						player.renderAnimations(757, 757, 757, 756, 756, 756, -1);
						player.playAnimation(new Animation(753));
						player.face(player.getLocation().transform(0,  -1));
					}
					
					if (tick == 13) {
						player.doPath(new DefaultPathFinder(), player, 3190, 3407);
					}
					if (tick == 15) {
						player.setDefaultAnimations(); // TODO send weapon again to make 2h anims work etc
						//Agility.forceMovement(player, new Animation(-1), forceMovementVars_4, 4, true);
						player.anim(741);
					}
					
					if(tick == 16) {
						player.setTeleportTarget(new Location(3192, 3406, 3));
						player.getSkills().addExperience(Skills.AGILITY, 21);
						Rooftop.marks_of_grace(player, new Location(3205, 3414, player.getZ()));
						this.stop();
					}
		            tick++;
	            }
	        });
			return true;
		}
		
		return false;
	}

}