package com.venenatis.game.content;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

public class GoodieBag {
	
	/**
	 * The goodie bag item Id
	 */
	private static Item GOODIE_BAG = new Item(22017);
	
	/**
	 * Rewards
	 */
    public static final Item[] RARE_CHEST_REWARDS = { new Item(7462), new Item(11770), new Item(11771), new Item(11772), new Item(11773), new Item(11759), new Item(11748), new Item(13080) };
	
	public static final Item[] UNCOMMON_CHEST_REWARDS = { new Item(4151), new Item(6585), new Item(4153), new Item(6570), new Item(6731), new Item(6733), new Item(6735), new Item(6737), new Item(12851) };
	
	public static final Item[] COMMON_CHEST_REWARDS = { new Item(995, Utility.random(3_500_000, 5_000_000)), new Item(7462), new Item(12829), new Item(12831), new Item(13307, Utility.random(1, 5)) };

	/**
	 * Open the bag for a random reward.
	 * 
	 * @param player
	 *            The player opening the bag
	 */
	public static void open(final Player player) {
		if (!player.getInventory().contains(GOODIE_BAG)) {
			return;
		}
		player.getInventory().remove(GOODIE_BAG);
		Item itemReceived;
		switch (Utility.getRandom(50)) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			itemReceived = Utility.randomElement(UNCOMMON_CHEST_REWARDS);
			break;
		case 25:
			itemReceived = Utility.randomElement(RARE_CHEST_REWARDS);
			break;
		default:
			itemReceived = Utility.randomElement(COMMON_CHEST_REWARDS);
		}

		player.getInventory().addOrCreateGroundItem(player, new Item(itemReceived.getId(), itemReceived.getAmount()));
		player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in your goodie bag.");
	}
	
	
}
