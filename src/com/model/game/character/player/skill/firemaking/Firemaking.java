package com.model.game.character.player.skill.firemaking;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler;
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
	 * Attempts to light a fire for the player.
	 */
	public static void useLighterWithLog(final Player player, int used, int with, final int x, final int y, final int z) {
		for (final LogData log : LogData.values()) {
			if (used == 590 && with == log.getLog() || used == log.getLog() && with == 590) {
				
				if (SkillHandler.isSkillActive(player, Skills.WOODCUTTING)) {
					player.message("You cannot perform this action while Woodcutting.");
					return;
				}
				
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
					player.write(new SendMessagePacket("You attempt to light the logs."));
					player.playAnimation(Animation.create(733));
				}
				player.getItems().remove(new Item(log.getLog()));
				Server.getTaskScheduler().schedule(new ScheduledTask(createTime) {
					@Override
					public void execute() {
						if (!player.getItems().playerHasItem(590, 1)) {
							this.stop();
							return;
						}
						GlobalObject fire = new GlobalObject(log.getFire(), player.getX(), player.getY(), player.getZ(), -1, 10, 250 + Utility.random(20));
						Server.getGlobalObjects().add(fire);
						
						player.message("The fire catches and the logs begin to burn.");
						player.face(item.getPosition());
						if (item != null) {
							GroundItemHandler.removeGroundItem(item);
						}
						getFiremakingWalk(player, x, y, z);
						/*if (!CollisionMap.isEastBlocked(player.heightLevel, player.absX - 1, player.absY)) {
							player.getPA().walkTo(-1, 0);
						} else if (!CollisionMap.isWestBlocked(player.heightLevel, player.absX + 1, player.absY)) {
							player.getPA().walkTo(1, 0);
						}*/
						player.getSkills().addExperience(Skills.FIREMAKING, log.getExperience());
						player.face(fire.getPosition());
						player.setLastFire(System.currentTimeMillis());
						stop();
					}
				}.attach(player));
				
			}
		}
	}
	
	/**
	 * Gets the clipped firemaking walk.
	 */
	public static void getFiremakingWalk(Player player, int x, int y, int z) {
		int[] walkDirection = { 0, 0 };
		if (Region.getClipping(x - 1, y - 1, player.getPosition().getZ(), -1, 0)) {
			walkDirection[0] = -1;
			walkDirection[1] = 0;
		} else if (Region.getClipping(x - +1, y, player.getPosition().getZ(), 1, 0)) {
			walkDirection[0] = 1;
			walkDirection[1] = 0;
		} else if (Region.getClipping(x, y - 1, player.getPosition().getZ(), 0, -1)) {
			walkDirection[0] = 0;
			walkDirection[1] = -1;
		} else if (Region.getClipping(x, y + 1, player.getPosition().getZ(), 0, 1)) {
			walkDirection[0] = 0;
			walkDirection[1] = 1;
		}
		player.getMovementHandler().walkTo(walkDirection[0], walkDirection[1]);
	}

	/**
	 * Registers the ashes when the fire is gone.
	 * 
	 * @param player
	 */
	public static void registerAshes(Player player, Position position) {
		GroundItem ash = new GroundItem(new Item(592), position, player);
		if (!GroundItemHandler.register(ash)) {
			return;
		}
		GroundItemHandler.createGroundItem(ash);
	}
	
}
