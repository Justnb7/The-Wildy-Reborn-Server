package com.venenatis.game.content.teleportation.tabs;

import com.venenatis.game.content.teleportation.Teleport;
import com.venenatis.game.content.teleportation.TeleportExecutor;
import com.venenatis.game.content.teleportation.Teleport.TeleportType;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.server.Server;

public class TeleTabs {

	final static int ANIM = 4731, GFX = 678;

	public enum TabData {
		VARROCK(8007, 3182, 3441), 
		LUMBRIDGE(8008, 3222, 3218), 
		FALADOR(8009, 2945, 3371), 
		CAMELOT(8010, 2747, 3477), 
		ARDOUGNE(8011, 2662, 3305), 
		WATCHTOWER(8012, 2549, 3112);

		private int itemId, posX, posY;

		TabData(int itemId, int posX, int posY) {
			this.itemId = itemId;
			this.posX = posX;
			this.posY = posY;
		}

		public int getItemId() {
			return itemId;
		}

		public int getPosX() {
			return posX;
		}

		public int getPosY() {
			return posY;
		}

		public static TabData forId(int itemId) {
			for (TabData data : TabData.values()) {
				if (data.itemId == itemId)
					return data;
			}
			return null;
		}
	}

	public static void useTeleTab(final Player player, int slot, final TabData data) {

		if (TeleportExecutor.canTeleport(player)) {
			player.playAnimation(Animation.create(4069));
			player.getInventory().remove(new Item(data.getItemId()));
			Server.getTaskScheduler().schedule(new ScheduledTask(2) {
				@Override
				public void execute() {
					TeleportExecutor.teleport(player, new Teleport(new Location(data.posX, data.posY, 0), TeleportType.TABLET), false);
					this.stop();
				}

			}.attach(player));
		}
	}

}
