package com.venenatis.game.content;

import java.util.PriorityQueue;
import java.util.Queue;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Item.ItemComparator;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

/**
 * Handles dropping items on death
 * 
 * @author Arithium
 * 
 */
public class DeathDropHandler {

	/**
	 * Handles dropping the items on death
	 * 
	 * @param player
	 */
	public static void handleDeathDrop(Player player) {

		Player killer = World.getWorld().lookupPlayerByName(player.getCombatState().getDamageMap().getKiller());

		if (killer == null) {
			killer = player;
		}

		final Item[] keep = player.isSkulled() ? new Item[player.isActivePrayer(Prayers.PROTECT_ITEM) ? 1 : 0]
				: new Item[player.isActivePrayer(Prayers.PROTECT_ITEM) ? 4 : 3];

		final Queue<Item> items = new PriorityQueue<Item>(ItemComparator.SHOP_VALUE_COMPARATOR);

		for (final Item item : player.getInventory().toNonNullArray()) {
			if (item != null) {
				items.add(item.copy());
			}
		}

		for (final Item item : player.getEquipment().toNonNullArray()) {
			if (item != null) {
				items.add(item.copy());
			}
		}

		final Queue<Item> temp = new PriorityQueue<>(items);

		for (int index = 0, taken = 0; index < keep.length; index++) {
			keep[index] = temp.poll();
			items.remove(keep[index]);

			if (keep[index] != null) {
				if (keep[index].getAmount() == keep.length - taken) {
					break;
				}

				if (keep[index].getAmount() > keep.length - taken) {
					items.add(new Item(keep[index].getId(), keep[index].getAmount() - (keep.length - taken)));
					keep[index].setAmount(keep.length - taken);
					break;
				}

				taken += keep[index].getAmount();
			}
		}

		player.getInventory().clear(false);
		player.getEquipment().clear(true);

		if (player.getInventory().add(keep) == 0) {
			player.getInventory().refresh();
		}

		while (!items.isEmpty()) {
			final Item item = items.poll();

			if (item == null) {
				continue;
			}

			// If killer is null, drop is for victim
			if (killer == null) {
				GroundItemHandler.createGroundItem(new GroundItem(item, player.getLocation().clone(), player));

				// If killer is null, drop is for victim
			} else if (killer.isNPC()) {
				GroundItemHandler.createGroundItem(new GroundItem(item, player.getLocation().clone(), player));

				// Drop all items for killer no random
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(item, player.getLocation().clone(), (Player) killer));
			}
		}

		if (killer != null && killer.isPlayer()) {
			GroundItemHandler
					.createGroundItem(new GroundItem(new Item(526), player.getLocation().clone(), killer.asPlayer()));
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(526), player.getLocation().clone(), player));
		}
		
		if(keep != null) {
			for(Item it : keep) {
				if(it != null) {
					int id = it.getId();
					BrokenItem brokenItem = BrokenItem.get(id);
					if(brokenItem != null) {
						id = brokenItem.getBrokenItem();
						player.getInventory().add(new Item(id, it.getAmount()));
						// only give back if its a broken item
					}
				}
			}
		}
	}
}