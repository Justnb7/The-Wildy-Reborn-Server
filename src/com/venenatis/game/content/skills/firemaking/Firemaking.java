package com.venenatis.game.content.skills.firemaking;

import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.server.Server;

import java.util.Random;

/**
 * The firemaking skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Firemaking {
	
	/**
	 * The random number generator
	 */
	private static Random random = new Random();
	
	/**
	 * Attempts to light a fire
	 * 
	 * @param player
	 *            The player lighting the fire
	 * @param itemUsed
	 *            The item being used either a log or tinderbox
	 * @param usedWith
	 *            The item be used on, which is also either a log or tinderbox
	 * @param location
	 *            The location of the fire
	 */
	public static boolean startFire(final Player player, Item itemUsed, Item usedWith, Location location) {
		for (final Logs log : Logs.values()) {
			if (itemUsed.getId() == 590 && usedWith.getId() == log.getLog() || itemUsed.getId() == log.getLog() && usedWith.getId() == 590) {
				
				if (System.currentTimeMillis() - player.getLastFire() < 200) {
					return false;
				}
				
				if (Server.getGlobalObjects().exists(log.getFire(), location)) {
					player.getActionSender().sendMessage("You can't light a fire on a fire!");
					return false;
				}
				
				if (player.getSkills().getLevel(Skills.FIREMAKING) < log.getLevel()) {
					player.getActionSender().sendMessage("You need a firemaking level of " + log.getLevel() + " to light this.");
					return false;
				}
				
				if (!player.getInventory().contains(590, 1)) {
					return false;
				}
				
				player.getAttributes().put("firemaking", true);
				
				final GroundItem item = new GroundItem(new Item(log.getLog()), player.getLocation(), player);
				
				if (GroundItemHandler.register(item)) {
					GroundItemHandler.createGroundItem(item);
				}
				
				player.playAnimation(Animation.create(733));
				player.getActionSender().sendMessage("You attempt to light the logs.");
				
				player.getInventory().remove(new Item(log.getLog()));
				
				GameObject fire = new GameObject(log.getFire(), player.getX(), player.getY(), player.getZ(), -1, 10, 100);
				
				final int delay = lightDelay(player, log.getLog());

				Server.getTaskScheduler().schedule(new Task(player, 1, false, StackType.NEVER_STACK, BreakType.ON_MOVE) {
					
					int clock = 0;
					@Override
					public void execute() {

						if(!player.hasAttribute("firemaking")) {
							player.playAnimation(Animation.create(65535));
							stop();
							return;
						}
						if (clock++ % 12 == 0)
							player.playAnimation(Animation.create(733));
						if (clock < delay)
							return;
						Server.getGlobalObjects().add(fire);
						
						if (item != null) {
							GroundItemHandler.sendRemoveGroundItem(item);
						}
						player.getActionSender().sendMessage("The fire catches and the logs begin to burn.");
						walk(player, location);
						player.getSkills().addExperience(Skills.FIREMAKING, log.getExperience());
						Location face = new Location(fire.getX(), fire.getY());
						player.face(player, face);
						player.setLastFire(System.currentTimeMillis());
						player.getAttributes().remove("firemaking");
						
						if(Area.inWilderness(player) && random.nextInt(10) < 7) {
							player.getInventory().addOrCreateGroundItem(player, new Item(13307, Utility.random(1, 5)));
						}
						
						stop();
						
						Server.getTaskScheduler().schedule(new Task(100) {
							@Override
							public void execute() {
								if (player.getOutStream() != null && player != null && player.isActive()) {
									GroundItemHandler.createGroundItem(new GroundItem(new Item(592), fire.getLocation(), player));
								}
								stop();
							}
						}.attach(player));
					}
				}.attach(player));
				return true;
			}
		}
		return false;
	}

	/**
	 * Finding the right direction to walk to.
	 * 
	 * @param player
	 *            The player lighting the fire
	 * @param location
	 *            The location the player walks to
	 */
	private static void walk(Player player, Location location) {
		int[] walkDir = { 0, 0 };
		if (Region.getClipping(location.getX() - 1, location.getY() - 1, player.getLocation().getZ(), -1, 0)) {
			walkDir[0] = -1;
			walkDir[1] = 0;
		} else if (Region.getClipping(location.getX() - +1, location.getY(), player.getLocation().getZ(), 1, 0)) {
			walkDir[0] = 1;
			walkDir[1] = 0;
		} else if (Region.getClipping(location.getX(), location.getY() - 1, player.getLocation().getZ(), 0, -1)) {
			walkDir[0] = 0;
			walkDir[1] = -1;
		} else if (Region.getClipping(location.getX(), location.getY() + 1, player.getLocation().getZ(), 0, 1)) {
			walkDir[0] = 0;
			walkDir[1] = 1;
		}
		player.getWalkingQueue().walkTo(walkDir[0], walkDir[1]);
	}

	/**
	 * Light delay for a specific log.
	 * 
	 * @param log
	 *            The log.
	 * @return The light delay.
	 */
	private static int lightDelay(Player player, int log) {
		for(Logs wood : Logs.values()) {
			if (wood.getLog() == log)
			   return random(4, (int) ((Math.sqrt(wood.getLevel() * 1) * (99 - player.getSkills().getLevel(Skills.FIREMAKING)))));
		}
		return 1;
	}
	
	/**
	 * Returns a random integer with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 *
	 * @param min
	 *            The inclusive lower bound.
	 * @param max
	 *            The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	private static int random(int min, int max) {
		Random random = new Random();
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}
	
}
