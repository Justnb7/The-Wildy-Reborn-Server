package com.model.game.character.player.skill.agility;

import java.util.ArrayList;
import java.util.List;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.ScriptManager;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Hit;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.location.Location;
import com.model.server.Server;
import com.model.task.ScheduledTask;

public class Agility {

	/**
     * Represents an agility obstacle.
     *
     * @author Michael
     */
    public enum Obstacle {

        WILDERNESS_DITCH(23271, Location.create(0, 0, 0), 1, 0, "wildernessDitch", true),

        TAVERLY_OBSTACLE_PIPE(16509, Location.create(2890, 9799, 0), 70, 0, "taverlyObstaclePipe", true),
        TAVERLY_OBSTACLE_PIPE2(16509, Location.create(2887, 9799, 0), 70, 0, "taverlyObstaclePipe", true),
		SHAMAN_ROCK_CLIMB(27362, Location.create(1455, 3690, 0), 0, 0, "shamanRockClimb1", true),
		SHAMAN_ROCK_CLIMB_TWO(27362, Location.create(1459, 3690, 0), 0, 0, "shamanRockClimb2", true),
		SHAMAN_ROCK_CLIMB_THREE(27362, Location.create(1471, 3687, 0), 0, 0, "shamanRockClimb3", true),
		SHAMAN_ROCK_CLIMB_FOUR(27362, Location.create(1475, 3687, 0), 0, 0, "shamanRockClimb4", true),

        //TAVERLY_GATE(2623, Location.create(0, 0, 0), 1, 0, "taverlyGate"),

        FALADOR_WESTERN_CRUMBLING_WALL(11844, Location.create(2935, 3355, 0), 5, 0.5, "faladorCrumblingWall", true),

		MOTHERLODE_DARK_TUNNEL(10047, Location.create(3760, 5670, 0), 54, 0.5, "motherlodeDarkTunnel", true),

		EDGE_DUNGEON_PIPE(16511, Location.create(3150, 9906, 0), 51, 0.5, "edgeDungeonPipe", true),
		EDGE_DUNGEON_PIPE_2(16511, Location.create(3153, 9906, 0), 51, 0.5, "edgeDungeonPipe", true),

		TAVERLY_SPIKE_JUMP(16510, Location.create(2879, 9813, 0), 80, 0.5, "taverlySpikeJump", true),

		FREMENIK_SPIKE_JUMP(16544, Location.create(2774, 10003, 0), 81, 0.5, "fremmySpikeJump", true),
		FREMENIK_SPIKE_JUMP_2(16544, Location.create(2769, 10002, 0), 81, 0.5, "fremmySpikeJump2", true),

		MOTHERLODE_DARK_TUNNEL_2(10047, Location.create(3764, 5671, 0), 54, 0.5, "motherlodeDarkTunnel2", true),

        ARDOUGNE_LOG_BALANCE(16548, Location.create(2601, 3336, 0), 33, 7, "ardougneLogBalance", true),
        ARDOUGNE_LOG_BALANCE_2(16546, Location.create(2599, 3336, 0), 33, 7, "ardougneLogBalance", true),
        ARDOUGNE_LOG_BALANCE_3(16547, Location.create(2600, 3336, 0), 33, 7, "ardougneLogBalance", true),

        /**
         * Gnome obstacle course
         */

        GNOME_COURSE_LOG_BALANCE(23145, Location.create(2474, 3435, 0), 1, 7, "gnomeLogBalance", false),

        GNOME_COURSE_OBSTACLE_NET_1(23144, Location.create(2471, 3425, 0), 1, 8, "gnomeObstacleNet", false),

        GNOME_COURSE_OBSTACLE_NET_2(2285, Location.create(2473, 3425, 0), 1, 8, "gnomeObstacleNet", false),

        GNOME_COURSE_OBSTACLE_NET_3(2285, Location.create(2475, 3425, 0), 1, 8, "gnomeObstacleNet", false),

        GNOME_COURSE_TREE_BRANCH(2313, Location.create(2473, 3422, 1), 1, 5, "gnomeTreeBranch", false),

        GNOME_COURSE_BALANCE_ROPE(2312, Location.create(2478, 3420, 2), 1, 7, "gnomeBalanceRope", false),

        GNOME_COURSE_TREE_BRANCH_2(2314, Location.create(2486, 3419, 2), 1, 5, "gnomeTreeBranch2", false),

        GNOME_COURSE_OBSTACLE_NET_4(2286, Location.create(2483, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

        GNOME_COURSE_OBSTACLE_NET_5(2286, Location.create(2485, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

        GNOME_COURSE_OBSTACLE_NET_6(2286, Location.create(2487, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

        GNOME_COURSE_OBSTACLE_PIPE_1(154, Location.create(2484, 3431, 0), 1, 8, "gnomeObstaclePipe", false),

        GNOME_COURSE_OBSTACLE_PIPE_2(154, Location.create(2487, 3431, 0), 1, 8, "gnomeObstaclePipe", false),

        SLAYER_CAVE_ROCK_CLIMB(26724, Location.create(2427, 9763, 0), 67, 5, "slayerRockClimb", true),

        SLAYER_CAVE_ROCK_CLIMB_2(26724, Location.create(2427, 9766, 0), 67, 5, "slayerRockClimb", true),


        /**
         * Draynor Village Rooftop Course
         */
        DRAYNOR_VILLAGE_ROCK_CLIMB(10073, Location.create(3103, 3279, 0), 10, 20, "draynorRockClimb", false),

        DRAYNOR_VILLAGE_TIGHT_ROPE_1(10074, Location.create(3098, 3277, 3), 10, 32, "draynorTightRope1", false),

        DRAYNOR_VILLAGE_TIGHT_ROPE_2(10075, Location.create(3092, 3276, 3), 10, 28, "draynorTightRope2", false),

        DRAYNOR_VILLAGE_NARROW_WALL(10077, Location.create(3089, 3264, 3), 10, 28, "draynorNarrowWall", false),

        DRAYNOR_VILLAGE_JUMP_WALL(10084, Location.create(3088, 3256, 3), 10, 40, "draynorJumpWall", false),

        DRAYNOR_VILLAGE_JUMP_GAP(10085, Location.create(3095, 3255, 3), 10, 16, "draynorJumpGap", false),

        DRAYNOR_VILLAGE_CRATE_JUMP(10086, Location.create(3102, 3261, 3), 10, 24, "draynorCrateJump", false),

        /**
         * Seers Village Rooftop Course
         */
        SEERS_WALL_CLIMB(11373, Location.create(2729, 3489, 0), 60, 65, "seersWallClimb", false),

        SEERS_GAP_JUMP(11374, Location.create(2720, 3492, 3), 60, 40, "seersGapJump", false),

        SEERS_TIGHT_ROPE(11378, Location.create(2710, 3489, 2), 60, 40, "seersTightRope", false),

        SEERS_GAP_JUMP_2(11375, Location.create(2710, 3476, 2), 60, 55, "seersGapJump2", false),

        SEERS_GAP_JUMP_3(11376, Location.create(2700, 3469, 3), 60, 35, "seersGapJump3", false),

        SEERS_GAP_JUMP_4(11377, Location.create(2703, 3461, 2), 60, 40, "seersGapJump4", false),

		/**
		 * Ardougne Rooftop Course
		 */
		ARDY_WOOD_CLIMB(11405, Location.create(2673, 3298, 0), 90, 65, "ardyWoodClimb", false),

		ARDY_GAP_JUMP(11406, Location.create(2670, 3310, 3), 90, 75, "ardyGapJump", false),

		ARDY_PLANK_WALK(11631, Location.create(2661, 3318, 3), 90, 65, "ardyPlankWalk", false),

		ARDY_GAP_JUMP_2(11429, Location.create(2653, 3317, 3), 90, 55, "ardyGapJump2", false),

		ARDY_GAP_JUMP_3(11430, Location.create(2653, 3308, 3), 90, 60, "ardyGapJump3", false),

		ARDY_STEP_ROOF(11633, Location.create(2654, 3300, 3), 90, 65, "ardyStepRoof", false),

		ARDY_GAP_JUMP_4(11630, Location.create(2656, 3296, 3), 90, 55, "ardyGapJump4", false),
//
//		/**
//		 * Al Kharid rooftop course
//		 */
//		ALKHARID_ROOF_WALL(10093, Location.create(3273, 3195, 0), 1, 5, "kharidWall", false),
//
//		ALKHARID_TIGHT_ROPE(10284, Location.create(3272, 3181, 3), 1, 5, "kharidRope", false),
//
//        ALKHARID_CABLE(10355, Location.create(3269, 3166, 3), 1, 5, "kharidCable", false),
//
//        ALKHARID_ZIPLINE(10356, Location.create(3302, 3163, 3), 1, 5, "kharidZipline", false),
//
//        ALKHARID_TREE(10357, Location.create(3318, 3166, 1), 1, 5, "kharidTree", false);
//		
//		/**
//		 * Barbarian agility course
//		 */
//		
//		BARBARIAN_COURSE_OBSTACLE_PIPE(2287, Location.create(2552, 3559, 0), 35, 0, "barbarianObstaclePipe"),
//		
//		BARBARIAN_COURSE_ROPE_SWING(2282, Location.create(2551, 3550, 0), 35, 22, "barbarianRopeSwing"),
//		
//		BARBARIAN_COURSE_LOG_BALANCE(2294, Location.create(2550, 3546, 0), 35, 13.7, "barbarianLogBalance"),
//		
//		BARBARIAN_COURSE_OBSTACLE_NET(2284, Location.create(2538, 3545, 0), 35, 8.2, "barbarianObstacleNet"),
//		
//		BARBARIAN_COURSE_LEDGE(2302, Location.create(2535, 3547, 1), 35, 22, "barbarianLedge"),
//		
//		BARBARIAN_COURSE_CRUMBLING_WALL_1(1948, Location.create(2536, 3553, 0), 35, 13.7, "barbarianCrumblingWall1"),
//		
//		BARBARIAN_COURSE_CRUMBLING_WALL_2(1948, Location.create(2539, 3553, 0), 35, 13.7, "barbarianCrumblingWall2"),
//		
//		BARBARIAN_COURSE_CRUMBLING_WALL_3(1948, Location.create(2542, 3553, 0), 35, 13.7, "barbarianCrumblingWall3"),
//		
        ;

        /**
         * The list of obstacles.
         */
        private static List<Obstacle> obstacles = new ArrayList<Obstacle>();

        /**
         * Populates the obstacle list
         */
        static {
            for (Obstacle obstacle : Obstacle.values()) {
                obstacles.add(obstacle);
            }
        }

        public Obstacle forId(int id) {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.getId() == id) {
                    return obstacle;
                }
            }
            return null;
        }

        public static Obstacle forLocation(Location location) {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.getLocation().equals(location)) {
                    return obstacle;
                }
            }
            return null;
        }

        /**
         * Object id.
         */
        private int id;

        /**
         * The location of this obstacle.
         */
        private Location location;

        /**
         * The level required to use this obstacle.
         */
        private int levelRequired;

        /**
         * The experience granted for tackling this obstacle.
         */
        private double experience;

        /**
         * The script that is executed for this obstacle.
         */
        private String scriptString;

        private boolean shortcut;

        private Obstacle(int id, Location location, int levelRequired, double experience, String scriptString, boolean shortcut) {
            this.id = id;
            this.location = location;
            this.levelRequired = levelRequired;
            this.experience = experience;
            this.scriptString = scriptString;
            this.shortcut = shortcut;
        }

        public int getId() {
            return id;
        }

        public Location getLocation() {
            return location;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public double getExperience() {
            return experience * 5;
        }

        public String getScriptString() {
            return scriptString;
        }

        public boolean isShortCut() {
            return shortcut;
        }
    }
	
	public static void tackleObstacle(Player player, Obstacle obstacle, int object) {
		if ((!obstacle.isShortCut() && player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired()) || (player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired() && obstacle.isShortCut())) {
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendMessage("You need an Agility level of " + obstacle.getLevelRequired() + " to tackle this obstacle.");
			player.playAnimation(Animation.create(-1));
			return;
		};
		//player.debug("Obstacle: "+obstacle.getScriptString()+" player: "+player.getName()+" object: "+object);
		player.getAttributes().put("busy", true);
		if(ScriptManager.getScriptManager().invokeWithFailTest(obstacle.getScriptString(), player, obstacle, object)) {
		} else {
			player.getAttributes().remove("busy");
			player.getActionSender().sendMessage("Nothing interesting happens.");
		}
	}
	
	public static void forceMovement(final Player player, final Animation animation, final int[] forceMovement, int ticks, final boolean removeAttribute) {
		Server.getTaskScheduler().submit(new ScheduledTask(ticks) {
			@Override
			public void execute() {
				player.playAnimation(animation);
				player.setForceWalk(forceMovement, removeAttribute);
				player.getUpdateFlags().flag(UpdateFlag.FORCE_MOVEMENT);
				this.stop();
			}
		});
	}
	
	public static void forceTeleport(final Player player, final Animation animation, final Location newLocation, int ticksBeforeAnim, int ticks) {
		if(animation != null) {
			if(ticksBeforeAnim < 1) {
				player.playAnimation(animation);
			} else {
				Server.getTaskScheduler().submit(new ScheduledTask(ticksBeforeAnim) {
					@Override
					public void execute() {
						player.playAnimation(animation);
						this.stop();
					}
				});			
			}
		}
		Server.getTaskScheduler().submit(new ScheduledTask(ticks) {
			@Override
			public void execute() {
				player.setTeleportTarget(newLocation);
				player.getAttributes().remove("busy");
				this.stop();
			}
		});
	}

	public static void forceWalkingQueue(final Player player, final Animation animation, final int x, final int y, int delayBeforeMovement, final int ticks, final boolean removeAttribute) {
		final int originalWalkAnimation = player.getWalkAnimation();
		final int originalRunAnimation = player.getRunAnimation();
		final int originalStandAnimation = player.getStandAnimation();
		final int originalStandTurn = player.getStandTurnAnimation();
		final int originalTurn90cw = player.getTurn90ClockwiseAnimation();
		final int originalTurn90ccw = player.getTurn90CounterClockwiseAnimation();
		final int originalTurn180 = player.getTurn90CounterClockwiseAnimation();

		ScheduledTask task = new ScheduledTask(delayBeforeMovement) {
			@Override
			public void execute() {
				if(animation != null) {
					player.setWalkAnimation(animation.getId());
					player.setRunAnimation(animation.getId());
					player.setStandAnimation(animation.getId());
					player.setStandTurnAnimation(animation.getId());
					player.setTurn90ClockwiseAnimation(animation.getId());
					player.setTurn90CounterClockwiseAnimation(animation.getId());
					player.setTurn180Animation(animation.getId());
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
				
				player.getWalkingQueue().setRunningToggled(false);
				
				player.getWalkingQueue().reset();
				player.getWalkingQueue().addStep(x, y);
				player.getWalkingQueue().finish();
				Server.getTaskScheduler().submit(new ScheduledTask(ticks) {
					@Override
					public void execute() {
						player.setWalkAnimation(originalWalkAnimation);
						player.setRunAnimation(originalRunAnimation);
						player.setStandAnimation(originalStandAnimation);
						player.setTurn90ClockwiseAnimation(originalTurn90cw);
						player.setTurn90CounterClockwiseAnimation(originalTurn90ccw);
						player.setTurn180Animation(originalTurn180);
						player.setStandTurnAnimation(originalStandTurn);
						player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						if(removeAttribute) {
							player.getAttributes().remove("busy");
						}
						this.stop();
					}
				});
				this.stop();
			}
		};
		if(delayBeforeMovement < 1) {
			task.execute();
		} else {
			Server.getTaskScheduler().submit(task);
		}
	}
	
	public static void setRunningToggled(final Player player, boolean toggled, int ticks) {
		final boolean originalToggledState = player.getWalkingQueue().isRunningToggled();
		player.getWalkingQueue().setRunningToggled(toggled);
		ScheduledTask task = new ScheduledTask(ticks) {
			@Override
			public void execute() {
				player.getWalkingQueue().setRunningToggled(originalToggledState);
				this.stop();
			}
		};
		if(task.getTickDelay() >= 1) {
			//Server.getTaskScheduler().submit(task);
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}
	
	public static void damage(final Player player, final int damage, int ticks) {		
		ScheduledTask task = new ScheduledTask(ticks) {
			@Override
			public void execute() {
				int dmg = damage;
				if(dmg > player.getSkills().getLevel(Skills.HITPOINTS)) {
					dmg = player.getSkills().getLevel(Skills.HITPOINTS);
				}
				player.damage(new Hit(damage));
				this.stop();
			}
		};
		if(task.getTickDelay() >= 1) {
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}
	
	//here is the ditch code
	public static void jumpDitch(final Player player, final int animation, final int[] forceMovement, int ticks, final boolean removeAttribute) {
		final int atY = forceMovement[3] == 3 ? 3520 : 3523;
		final int atX = forceMovement[3] == 3 ? 2995 : 2998;
		ScheduledTask task = new ScheduledTask(1) {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(animation));
				int amount = 0;
				int i = player.getLocation().getY();
				int x = player.getLocation().getX();
				if (atY == 3520) {
					while (i != 3523 && !(i > 3523)) {
						i++;
						amount++;
					}
					forceMovement[3] = amount;
				} else if (atY == 3523) {
					while (i != 3520 && !(i < 3520)) {
						i--;
						amount--;
					}
					forceMovement[3] = amount;
				} else if (atX == 2995) {
					while (x != 2998 && !(x > 2998)) {
						x++;
						amount++;
					}
					forceMovement[3] = amount;
				} else if (atX == 2998) {
					while (x != 2995 && !(x > 2995)) {
						x--;
						amount--;
					}
					forceMovement[3] = amount;
				}
				this.stop();
				//We're resting the walk here arent we? sec
				player.getWalkingQueue().reset();
				player.setForceWalk(forceMovement, removeAttribute);
				player.getUpdateFlags().flag(UpdateFlag.FORCE_MOVEMENT);
			}
		};
		if(task.getTickDelay() >= 1) {
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}
}