package com.venenatis.game.model.combat.npcs.impl.zulrah;

import java.util.ArrayList;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;


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
		for (Item inventory : player.getInventory().toArray()) {
			if (inventory == null)
				continue;
			add(new Item(inventory.id, inventory.amount));
		}

		for (Item equipment : player.getEquipment().toArray()) {
			if (equipment == null)
				continue;
			add(new Item(equipment.id, equipment.amount));
		}

		player.getEquipment().clear(true);
		player.getInventory().clear(true);
	}

	public void retain() {
		int price = 500_000;
		if (!player.getInventory().contains(995, price)) {
			SimpleDialogues.sendMobStatement(player, 2040, "You need at least 500,000GP to claim your items.");
			return;
		}
		for (Item item : this) {
			player.getBank().add(item);
		}
		clear();
		player.getInventory().remove(995, price);
		SimpleDialogues.sendMobStatement(player, 2040, "You have retained all of your lost items for 500,000GP.", "Your items are in your bank.");
	}

}