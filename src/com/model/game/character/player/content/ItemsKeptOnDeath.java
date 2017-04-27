package com.model.game.character.player.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.effect.SkullType;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.bounty_hunter.BountyHunterEmblem;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.item.Item;
import com.model.utility.Utility;

/**
 * Handles items kept on death.
 * @author Swiffy
 */
public class ItemsKeptOnDeath {

	/**
	 * Sends the items kept on death interface for a player.
	 * @param player	Player to send the items kept on death interface for.
	 */
	public static void open(Player player) {
		clearInterfaceData(player); //To prevent sending multiple layers of items.
		sendInterfaceData(player); //Send info on the interface.
		player.write(new SendInterfacePacket(17100)); //Open the interface.
	}

	/**
	 * Sends the items kept on death data for a player.
	 * @param player	Player to send the items kept on death data for.
	 */
	public static void sendInterfaceData(Player player) {

		player.getActionSender().sendString(""+getAmountToKeep(player), 17107);

		ArrayList<Item> toKeep = getItemsToKeep(player);
		for(int i = 0; i < toKeep.size(); i++) {
			player.getActionSender().sendUpdateItem(17108+i, toKeep.get(i).getId(), 0, 1);
		}

		int toSend = 17112;
		for(Item item : Utility.concat(player.getInventory().toArray(), player.getEquipment().toArray())) {
			if(item == null || item.getId() <= 0 || item.getAmount() <= 0 || !item.getDefinition().isTradeable() || toKeep.contains(item)) {
				continue;
			}
			player.getActionSender().sendUpdateItem(toSend, item.getId(), 0, item.getAmount());
			toSend++;
		}
	}

	/**
	 * Clears the items kept on death interface for a player.
	 * @param player	Player to clear the items kept on death interface for.
	 */
	public static void clearInterfaceData(Player player) {
		for(int i = 17108; i <= 17152; i++)
			player.getActionSender().clearItemOnInterface(i);
	}

	/**
	 * Sets the items to keep on death for a player.
	 * @param player	Player to set items for.
	 */
	public static ArrayList<Item> getItemsToKeep(Player player) {
		ArrayList<Item> items = new ArrayList<Item>();
		for(Item item : Utility.concat(player.getInventory().toArray(), player.getEquipment().toArray())) {
			if(item == null || item.getId() <= 0 || item.getAmount() <= 0 || !item.getDefinition().isTradeable()) {
				continue;
			}

			//Dont keep emblems
			if(item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_1.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_2.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_3.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_4.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_5.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_6.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_7.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_8.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_9.getItemId() ||
					item.getId() == BountyHunterEmblem.MYSTERIOUS_EMBLEM_10.getItemId()) {
				continue;
			}

			items.add(item);
		}
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item item, Item item2) {
				int value1 = item.getDefinition().getGeneralPrice();
				int value2 = item2.getDefinition().getGeneralPrice();
				if (value1 == value2) {
					return 0;
				} else if (value1 > value2) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		ArrayList<Item> toKeep = new ArrayList<Item>();
		int amountToKeep = getAmountToKeep(player);
		for(int i = 0; i < amountToKeep && i < items.size(); i++) {
			toKeep.add(items.get(i));
		}
		return toKeep;
	}

	public static int getAmountToKeep(Player player) {
		if(player.getSkullTimer() > 0) {
			if(player.getSkullType() == SkullType.RED_SKULL) {
				return 0;
			}
		}
		return (player.getSkullTimer() > 0 ? 0 : 3) + (player.isActivePrayer(Prayers.PROTECT_ITEM) ? 1 : 0);
	}
}