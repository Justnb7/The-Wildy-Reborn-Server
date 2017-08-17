package com.venenatis.game.content.teleportation.teletab;

import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.world.World;

/**
 * Teletabs are the items used for player teleportation.
 * @author Patrick van Elderen
 *
 */
public class Tablet {
	
	/**
	 * Breaks the tele tab and teleports the player to the location
	 * 
	 * @param player
	 *            The player teleporting
	 * @param tabletId
	 *            The tablet thats being broken
	 */
	public static boolean breakTablet(final Player player, Item tabletId) {
		for(TabletData tablet : TabletData.values()) {
			//Check if we're actually breaking the correct tablet.
			if(tablet.getTablet() == tabletId) {
				
				if (!player.getController().canTeleport()) {
					return false;
				}

				if (player.getTeleportAction().isTeleporting()) {
					return false;
				}

				if (!MinigameHandler.execute(player, true, $it -> $it.canTeleport(player))) {
					return false;
				}
				
				if(player.getWildLevel() > 20) {
					player.getActionSender().sendMessage("You can not teleport past 20 wilderness!");
				}

				if (player.getDuelArena().isDueling()) {
					player.getActionSender().sendMessage("You cannot teleport while you are dueling.");
					return false;
				}
				
				if (player.getCombatState().isTeleblocked()) {
					player.getActionSender().sendMessage("You are currently teleblocked and can not teleport!");
					return false;
				}
				
				player.getInventory().remove(tablet.getTablet());
				
				player.playAnimation(Animation.create(4731));
				player.playGraphics(Graphic.create(678));
				player.getTeleportAction().setTeleporting(true);
				player.getAttributes().put("can_take_damage", false);
				
				World.getWorld().schedule(new Task(player, 3, false, StackType.STACK, BreakType.ON_MOVE) {
					public void execute() {
						//We arrived, set the new coordinates
						player.setTeleportTarget(tablet.getLocation());
						player.playGraphics(Graphic.create(-1));
						player.playAnimation(Animation.create(65535));
						//Reset the attributes
						player.getTeleportAction().setTeleporting(false);
						player.getAttributes().put("can_take_damage", true);
						this.stop();
					}
				});
			}
		}
		return true;
	}

}
