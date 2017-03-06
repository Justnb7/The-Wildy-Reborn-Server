package com.model.game.character.player.skill.firemaking;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Position;
import com.model.game.object.GlobalObject;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;

/**
 * The firemaking skill
 * @author Patrick van Elderen
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
				
				boolean notInstant = System.currentTimeMillis() - player.getLastFire() > 2500;
				
				final GroundItem item = new GroundItem(new Item(log.getLog()), player.getPosition(), player);
				
				int createTime = 2;
				
				if (GroundItemHandler.register(item)) {
					GroundItemHandler.createGroundItem(item);
				}
				
				if (notInstant) {
					player.playAnimation(Animation.create(733));
					player.write(new SendMessagePacket("You attempt to light the logs."));
					createTime = 3 + Utility.random(6);
				}
				
				player.getItems().remove(new Item(log.getLog()));
				
				GlobalObject fire = new GlobalObject(log.getFire(), player.getX(), player.getY(), player.getZ(), -1, 10, 100);
				Server.getTaskScheduler().schedule(new ScheduledTask(createTime) {
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
	
}
