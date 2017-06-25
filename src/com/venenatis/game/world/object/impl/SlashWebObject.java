package com.venenatis.game.world.object.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

public class SlashWebObject {
	
	/**
	 * Handles the slash web action for the player.
	 */
	public static void slashWeb(final Player player, Location position, boolean usingKnife) {
		if (!usingKnife) {
			
			Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (weapon == null) {
				return;
			}
			String name = weapon.getName().toLowerCase();
			if (!name.contains("dagger")) {
				player.getActionSender().sendMessage("You need a sharp weapon to slash through this.");
				return;
			}
		}
		final GameObject openedWeb = new GameObject(734, position.getX(), position.getY(), position.getZ(), 0, 10);
		final GameObject closedWeb = new GameObject(733, position.getX(), position.getY(), position.getZ(), 0, 10);
		player.playAnimation(Animation.create(451));
		
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {

			@Override
			public void execute() {
				if (Utility.random(2) == 0) {
					player.getActionSender().sendMessage("You fail to slash through the web.");
					this.stop();
				} else {
					player.getActionSender().sendMessage("You manage to slash through the web.");
					Server.getGlobalObjects().replaceObject(closedWeb, openedWeb, 10);
					this.stop();
				}
			}
		});
	}

}
