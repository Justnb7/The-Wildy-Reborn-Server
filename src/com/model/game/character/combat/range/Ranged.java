package com.model.game.character.combat.range;

import com.model.game.character.Entity;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;

public class Ranged {
	
	private static Ranged ranged = new Ranged();
	
	public static Ranged getSingleton() {
		return ranged;
	}
	
	/**
	 * Spawns the arrow shot underneath the player's victim.
	 */
	public void dropShootersArrow(Player player, Entity victim, Item arrow) {
		if (player.isDead() || victim.isDead()) {
			return;
		}
		int arrowIndex = arrow.getId();
		GroundItem arrowSpawn = new GroundItem(new Item(arrowIndex), victim.getLocation(), player);
		if (!GroundItemHandler.register(arrowSpawn)) {
			return;
		}
		player.getActionSender().sendGroundItem(arrowSpawn);
	}

}
