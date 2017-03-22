package com.model.game.character.player.content.rewards;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.utility.Utility;

/**
 * Handles crystal chest
 * 
 * @author Patrick van Elderen
 * @date 20-5-2016
 *
 */
public class CrystalChest {
	
	/**
	 * Ids of key halves
	 */
	public static final Item[] KEY_HALVES = { new Item(985), new Item(987) };
	
	/**
	 * Creates the key
	 * 
	 * @param player
	 */
	public static boolean createKey(final Player player) {
		if (player.getItems().playerHasItems(KEY_HALVES)) {
			player.getItems().remove(KEY_HALVES[0]);
			player.getItems().remove(KEY_HALVES[1]);
			player.getItems().addOrCreateGroundItem(new Item(989));
			player.getActionSender().sendMessage("You have combined the two parts to form a key.");
			return true;
		}
		return false;
	}
	
	/**
	 * Chest rewards
	 */
    public static final Item[] RARE_CHEST_REWARDS = { new Item(10551, 1), new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), new Item(4714, 1), new Item(4732, 1), new Item(4734, 1), new Item(4736, 1), new Item(4738, 1), new Item(4740, 50), new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), new Item(4759, 1), new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1), new Item(4151, 1), new Item(6585, 1), new Item(11840, 1), new Item(6737, 1), new Item(11838, 1), new Item(12954, 1), };
	
	public static final Item[] UNCOMMON_CHEST_REWARDS = { new Item(4724, 1), new Item(4726, 1), new Item(4728, 1), new Item(4730, 1), new Item(4745, 1), new Item(4747, 1), new Item(4749, 1), new Item(4751, 1), new Item(6733, 1), new Item(4153, 1), new Item(7462, 1) };
	
	public static final Item[] COMMON_CHEST_REWARDS = { new Item(7461, 1), new Item(7460, 1), new Item(10926, 2), new Item(2617, 1), new Item(1079, 1), new Item(4585, 1), new Item(4587, 1), new Item(10828, 1), new Item(626, 1), new Item(628, 1), new Item(8850, 1), new Item(6735, 1), new Item(6731, 1), new Item(10548, 1), };
	
	/**
	 * Searches the chest
	 * 
	 * @param player
	 * @param x
	 * @param y
	 */
	public static void searchChest(final Player player, final int x, final int y) {
		if (!player.getItems().playerHasItem(989)) {
			return;
		}
		player.getActionSender().sendMessage("You unlock the chest with your key.");
		player.getItems().deleteItem(989);
		player.playAnimation(Animation.create(881));
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

		player.getItems().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
		player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getDefinition().getName()) + " " + itemReceived.getDefinition().getName() + " in the chest.");
	}
}
