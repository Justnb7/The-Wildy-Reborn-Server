package com.model.game.character.player.content.rewards;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.location.Location;
import com.model.utility.Utility;

/**
 * 
 * @author Patrick van Elderen
 *
 */
public class ShinyChest {
	
	/**
	 * Chest rewards
	 */
    public static final Item[] RARE_CHEST_REWARDS = { new Item(11802), new Item(18349), new Item(18351), new Item(18353), new Item(18355), new Item(11785), new Item(10330), new Item(10332), new Item(10334), new Item(10336), new Item(10338), new Item(10340), new Item(10342), new Item(10344), new Item(10346), new Item(10348), new Item(10350), new Item(10352), new Item(12422), new Item(12424), new Item(12426), new Item(20011), new Item(20014) };
	public static final Item[] UNCOMMON_CHEST_REWARDS = { new Item(11770), new Item(11771), new Item(11772), new Item(11773), new Item(20095), new Item(20098), new Item(20101), new Item(20104), new Item(20107), new Item(20080), new Item(20083), new Item(20086), new Item(20089), new Item(20092), new Item(20146), new Item(20149), new Item(20152), new Item(20155), new Item(20158), new Item(20161), new Item(11804), new Item(11806), new Item(11808) };
	public static final Item[] COMMON_CHEST_REWARDS = { new Item(4151), new Item(6585), new Item(4153), new Item(6731), new Item(6733), new Item(6735), new Item(6737), new Item(11283), new Item(12596), new Item(2581), new Item(2577) };
	
	/**
	 * Searches the chest
	 * @param player
	 * @param x
	 * @param y
	 */
	public static void searchChest(final Player player, Location position) {
			player.getActionSender().sendMessage("You unlock the chest with your key.");
			player.getItems().deleteItem(85);
			player.playAnimation(Animation.create(881));
			player.playGraphics(Graphic.create(390));
			
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
