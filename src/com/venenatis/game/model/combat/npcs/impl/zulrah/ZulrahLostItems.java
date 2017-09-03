package com.venenatis.game.model.combat.npcs.impl.zulrah;

import java.util.ArrayList;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;


public class ZulrahLostItems extends ArrayList<Item> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The player that has lost items
	 */
	private final Player player;

	/**
	 * Creates a new class for managing lost items by a single player
	 * 
	 * @param player the player who lost items
	 */
	public ZulrahLostItems(final Player player) {
		this.player = player;
	}

	/**
	 * Stores the players items into a list and deletes their items
	 */
	public void store() {
		//TODO store items
		/*for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] < 1) {
				continue;
			}
			add(new GameItem(player.playerItems[i] - 1, player.playerItemsN[i]));
		}
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] < 1) {
				continue;
			}
			add(new GameItem(player.playerEquipment[i], player.playerEquipmentN[i]));
		}*/
		
		player.getEquipment().clear(true);
		player.getInventory().clear(true);
	}

	public void retain() {
		int price = 500_000;
		if (!player.getInventory().contains(995, price)) {
			//player.talkingNpc = 2040;
			//player.getDH().sendNpcChat("You need at least 500,000GP to claim your items.");
			return;
		}
		for (Item item : this) {
			player.getBank().add(item);
		}
		clear();
		player.getInventory().remove(995, price);
		//player.talkingNpc = 2040;
		//player.getDH().sendNpcChat("You have retained all of your lost items for 500,000GP.", "Your items are in your bank.");
	}

}