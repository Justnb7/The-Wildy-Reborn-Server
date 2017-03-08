package com.model.game.character.player.skill.firemaking;

import java.util.Random;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Position;
import com.model.game.object.GlobalObject;
import com.model.task.ScheduledTask;
import com.model.utility.cache.map.Region;

/**
 * The firemaking skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Firemaking {
	
	/**
	 * Attempt to light a fire.
	 */
	public static void startFire(final Player player, int itemUsed, int usedWith, final int x, final int y, final int z) {
		for (final LogData log : LogData.values()) {
			if (itemUsed == 590 && usedWith == log.getLog() || itemUsed == log.getLog() && usedWith == 590) {
				
				if (System.currentTimeMillis() - player.getLastFire() < 200) {
					return;
				}
				
				if (Server.getGlobalObjects().exists(log.getFire(), x, y, z)) {
					player.write(new SendMessagePacket("You can't light a fire on a fire!"));
					return;
				}
				
				if (player.getSkills().getLevel(Skills.FIREMAKING) < log.getLevel()) {
					player.write(new SendMessagePacket("You need a firemaking level of " + log.getLevel() + " to light this."));
					return;
				}
				
				if (!player.getItems().playerHasItem(590, 1)) {
					return;
				}
				
				final GroundItem item = new GroundItem(new Item(log.getLog()), player.getPosition(), player);
				
				if (GroundItemHandler.register(item)) {
					GroundItemHandler.createGroundItem(item);
				}
				
				player.playAnimation(Animation.create(733));
				player.write(new SendMessagePacket("You attempt to light the logs."));
				
				player.getItems().remove(new Item(log.getLog()));
				
				GlobalObject fire = new GlobalObject(log.getFire(), player.getX(), player.getY(), player.getZ(), -1, 10, 100);
				Server.getTaskScheduler().schedule(new ScheduledTask(lightDelay(player, log.getLog())) {
					@Override
					public void execute() {
						
						Server.getGlobalObjects().add(fire);
						
						if (item != null) {
							GroundItemHandler.removeGroundItem(item);
						}
						player.message("The fire catches and the logs begin to burn.");
						walk(player, x, y, z);
						player.getSkills().addExperience(Skills.FIREMAKING, log.getExperience());
						Position face = new Position(fire.getX(), fire.getY());
						player.face(player, face);
						player.setLastFire(System.currentTimeMillis());
						stop();
					}
				}.attach(player));
				
				Server.getTaskScheduler().schedule(new ScheduledTask(100) {
					@Override
					public void execute() {
						if (player.getOutStream() != null && player != null && player.isActive()) {
							GroundItemHandler.createGroundItem(new GroundItem(new Item(592), fire.getX(), fire.getY(), fire.getHeight(), player));
						}
						stop();
					}
				}.attach(player));
			}
		}
	}
	
	/**
	 * Finding the right direction to walk to.
	 */
	private static void walk(Player player, int x, int y, int z) {
		int[] walkDir = { 0, 0 };
		if (Region.getClipping(x - 1, y - 1, player.getPosition().getZ(), -1, 0)) {
			walkDir[0] = -1;
			walkDir[1] = 0;
		} else if (Region.getClipping(x - +1, y, player.getPosition().getZ(), 1, 0)) {
			walkDir[0] = 1;
			walkDir[1] = 0;
		} else if (Region.getClipping(x, y - 1, player.getPosition().getZ(), 0, -1)) {
			walkDir[0] = 0;
			walkDir[1] = -1;
		} else if (Region.getClipping(x, y + 1, player.getPosition().getZ(), 0, 1)) {
			walkDir[0] = 0;
			walkDir[1] = 1;
		}
		player.getMovementHandler().walkTo(walkDir[0], walkDir[1]);
	}

	/**
	 * Light delay for a specific log.
	 * @param log The log.
	 * @return The light delay.
	 */
	private static int lightDelay(Player player, int log) {
		for(LogData wood : LogData.values()) {
			if (wood.getLog() == log)
			   return random(4, (int) ((Math.sqrt(wood.getLevel() * 1) * (99 - player.getSkills().getLevel(Skills.FIREMAKING)))));
		}
		return 1;
	}
	
	/**
	 * Returns a random integer with min as the inclusive
	 * lower bound and max as the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	private static int random(int min, int max) {
		Random random = new Random();
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}
	
}
