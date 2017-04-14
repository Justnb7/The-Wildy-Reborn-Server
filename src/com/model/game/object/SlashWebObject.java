package com.model.game.object;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class SlashWebObject {
	
	/**
	 * Handles the slash web action for the player.
	 */
	public static void slashWeb(final Player player, Location position, boolean usingKnife) {
		if (!usingKnife) {
			
			int weapon = player.playerEquipment[3];
			if (weapon == -1) {
				return;
			}
			String name = player.getItems().getItemName(weapon).toLowerCase();
			if (!name.contains("dagger")) {
				player.getActionSender().sendMessage("You need a sharp weapon to slash through this.");
				return;
			}
		}
		final GlobalObject openedWeb = new GlobalObject(734, position.getX(), position.getY(), position.getZ(), 0, 10);

		player.playAnimation(Animation.create(451));
		
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {

			@Override
			public void execute() {
				if (Utility.random(2) == 0) {
					player.getActionSender().sendMessage("You fail to slash through the web.");
					this.stop();
				} else {
					player.getActionSender().sendMessage("You manage to slash through the web.");
					Server.getGlobalObjects().add(openedWeb);
					this.stop();
				}
			}
		});
	}

}
