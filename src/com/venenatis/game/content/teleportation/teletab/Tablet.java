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
 * Teletabs and scrolls are the items used for player teleportation.
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
	public static boolean breakTablet(final Player player, int tabletId) {
		for(TabletData tablet : TabletData.values()) {
			//Check if we're actually breaking the correct tablet.
			if(tablet.getTablet().getId() == tabletId) {
				//Check if the container allows us to teleport
				if (!player.getController().canTeleport()) {
					return false;
				}

				//If we're already teleporting we cannot teleport again
				if (player.getTeleportAction().isTeleporting()) {
					return false;
				}

				//Check if the minigame allows us to teleport
				if (!MinigameHandler.execute(player, true, $it -> $it.canTeleport(player))) {
					return false;
				}
				
				//No teleporting above level 20 wilderness
				if(player.getWildLevel() > 20) {
					player.getActionSender().sendMessage("You can not teleport past 20 wilderness!");
					return false;
				}

				//We cannot teleport out of the dueling arena
				if (player.getDuelArena().isDueling()) {
					player.getActionSender().sendMessage("You cannot teleport while you are dueling.");
					return false;
				}
				
				//We can't teleport when we're teleblocked
				if (player.getCombatState().isTeleblocked()) {
					player.getActionSender().sendMessage("You are currently teleblocked and can not teleport!");
					return false;
				}
				
				//Remove the tab/scroll
				Item tab = tablet.getTablet();
				player.getInventory().remove(new Item(tab.getId(), 1));
				
				//If we're using the teleport scroll we have different animations and gfx
				if (tablet.isScroll()) {
					player.playAnimation(Animation.create(3864));
					player.playGraphics(Graphic.create(1039));
				} else {
					player.playAnimation(Animation.create(4731));
					player.playGraphics(Graphic.create(678));
				}
				
				//Set teleporting attributes
				player.getTeleportAction().setTeleporting(true);
				player.setCanBeDamaged(false);
				
				//Start the task
				World.getWorld().schedule(new Task(player, 3, false, StackType.STACK, BreakType.ON_MOVE) {
					public void execute() {
						//We arrived, set the new coordinates
						player.setTeleportTarget(tablet.getLocation());
						player.playGraphics(Graphic.create(-1));
						player.playAnimation(Animation.create(65535));
						//Reset the attributes
						player.getTeleportAction().setTeleporting(false);
						player.setCanBeDamaged(true);
						this.stop();
					}
				});
				return true;
			}
		}
		return false;
	}

}
