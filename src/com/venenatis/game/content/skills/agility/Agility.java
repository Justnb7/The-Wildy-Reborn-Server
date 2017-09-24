package com.venenatis.game.content.skills.agility;

import java.util.ArrayList;
import java.util.List;

import com.venenatis.ScriptManager;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.region.RegionStore;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

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

        SLAYER_CAVE_ROCK_CLIMB(26724, Location.create(2427, 9763, 0), 67, 5, "slayerRockClimb", true),

        SLAYER_CAVE_ROCK_CLIMB_2(26724, Location.create(2427, 9766, 0), 67, 5, "slayerRockClimb", true),

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
    
    public static void animateObject(final GameObject gameObject, final Animation animation, int ticks) {
    	Task tick = new Task(ticks) {
            @Override
            public void execute() {
                for (RegionStore r : RegionStoreManager.get().getSurroundingRegions(gameObject.getLocation())) {
                    for (Player player : r.getPlayers()) {
                        player.getActionSender().animateObject(gameObject, animation.getId());
                    }
                }
                this.stop();
            }
        };
        if (tick.getTickDelay() >= 1) {
            World.getWorld().schedule(tick);
        } else {
            tick.execute();
        }
    }
    
	private static void pet(Player player) {
		Pets pets = Pets.GIANT_SQUIRREL;
		Pet pet = new Pet(player, pets.getNpc());
		
		if(player.alreadyHasPet(player, 20659) || player.getPet() == pets.getNpc()) {
			return;
		}
		
		int random = Utility.random(1500);
		if (random == 0) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(20659));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Giant squirrel.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Giant squirrel.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
	
	public static void tackleObstacle(Player player, Obstacle obstacle, GameObject object) {
		if ((!obstacle.isShortCut() && player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired()) || (player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired() && obstacle.isShortCut())) {
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendMessage("You need an Agility level of " + obstacle.getLevelRequired() + " to tackle this obstacle.");
			player.playAnimation(Animation.create(-1));
			return;
		};
		player.debug("Obstacle: "+obstacle.getScriptString()+" player: "+player.getUsername()+" object: "+object);
		player.getAttributes().put("busy", true);
		if(ScriptManager.getScriptManager().invokeWithFailTest(obstacle.getScriptString(), player, obstacle, object)) {
			pet(player);
			player.debug("Activating obstacle");
		} else {
			player.getAttributes().remove("busy");
			player.getActionSender().sendMessage("Nothing interesting happens.");
		}
	}
	
	public static void forceTeleport(final Player player, final Animation animation, final Location newLocation, int ticksBeforeAnim, int ticks) {
		if(animation != null) {
			if(ticksBeforeAnim < 1) {
				player.playAnimation(animation);
			} else {
				World.getWorld().schedule(new Task(ticksBeforeAnim) {
					@Override
					public void execute() {
						player.playAnimation(animation);
						this.stop();
					}
				});			
			}
		}
		World.getWorld().schedule(new Task(ticks) {
			@Override
			public void execute() {
				player.setTeleportTarget(newLocation);
				player.getAttributes().remove("busy");
				this.stop();
			}
		});
	}
	
	public static void delayedAnimation(final Player player, Animation anim, int ticks) {
        World.getWorld().schedule(new Task(ticks) {

            @Override
            public void execute() {
                this.stop();
                player.playAnimation(anim);
            }
        });
    }
	
	public static void setRunningToggled(final Player player, boolean toggled, int ticks) {
		final boolean originalToggledState = player.getWalkingQueue().isRunningToggled();
		player.getWalkingQueue().setRunningToggled(toggled);
		Task task = new Task(ticks) {
			@Override
			public void execute() {
				player.getWalkingQueue().setRunningToggled(originalToggledState);
				this.stop();
			}
		};
		if(task.getTickDelay() >= 1) {
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}
	
	public static void damage(final Player player, final int damage, int ticks) {		
		Task task = new Task(ticks) {
			@Override
			public void execute() {
				int dmg = damage;
				if(dmg > player.getSkills().getLevel(Skills.HITPOINTS)) {
					dmg = player.getSkills().getLevel(Skills.HITPOINTS);
				}
				player.take_hit(null, damage);
				this.stop();
			}
		};
		if(task.getTickDelay() >= 1) {
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}
	
	/*public static void jumpDitch(final Player player, final int animation, ForceMovement forceMovement, int ticks, final boolean removeAttribute) {
		final int atY = forceMovement.getEndX() == 3 ? 3520 : 3523;
		final int atX = forceMovement.getEndX() == 3 ? 2995 : 2998;
		Task task = new Task(1) {
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
				player.getWalkingQueue().reset();
				//final Player player, final Animation animation, ForceMovement forceMovement, int ticks, final boolean removeAttribute
				player.forceMove(player, animation, forceMovement, 0, removeAttribute);
				player.getUpdateFlags().flag(UpdateFlag.FORCE_MOVEMENT);
			}
		};
		if(task.getTickDelay() >= 1) {
			World.getWorld().schedule(task);
		} else {
			task.execute();
		}
	}*/
}