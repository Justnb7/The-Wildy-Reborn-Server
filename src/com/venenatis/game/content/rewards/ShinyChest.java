package com.venenatis.game.content.rewards;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * When players open this chest they have a slim chance of one of the best items in
 * game
 * 
 * @author Patrick van Elderen
 *
 */
public class ShinyChest {
	
	/**
	 * Shiny key
	 */
	private final static Item SHINY_KEY = new Item(85);
	
	/**
	 * Shiny key rewards
	 */
    public static final Item[] RARE_SHINY_KEY_REWARDS = { new Item(11802), new Item(18349), new Item(18351), new Item(18353), new Item(18355), new Item(11785), new Item(10330), new Item(10332), new Item(10334), new Item(10336), new Item(10338), new Item(10340), new Item(10342), new Item(10344), new Item(10346), new Item(10348), new Item(10350), new Item(10352), new Item(12422), new Item(12424), new Item(12426), new Item(20011), new Item(20014) };
	public static final Item[] UNCOMMON_SHINY_KEY_REWARDS = { new Item(11770), new Item(11771), new Item(11772), new Item(11773), new Item(20095), new Item(20098), new Item(20101), new Item(20104), new Item(20107), new Item(20080), new Item(20083), new Item(20086), new Item(20089), new Item(20092), new Item(20146), new Item(20149), new Item(20152), new Item(20155), new Item(20158), new Item(20161), new Item(11804), new Item(11806), new Item(11808) };
	public static final Item[] COMMON_SHINY_KEY_REWARDS = { new Item(11283), new Item(12596), new Item(2581), new Item(2577) };
	
	/**
	 * Purple key
	 */
	private final static Item PURPLE_KEY = new Item(1547);
	
	/**
	 * Purple key rewards
	 */
    public static final Item[] RARE_PURPLE_KEY_REWARDS = { new Item(13022), new Item(19780), new Item(13887), new Item(13893), new Item(13899), new Item(13858), new Item(13861), new Item(13864), new Item(13884), new Item(13890), new Item(13896), new Item(13902), new Item(13870), new Item(13873), new Item(13876) };
	public static final Item[] UNCOMMON_PURPLE_KEY_REWARDS = { new Item(12817), new Item(12821), new Item(12825), new Item(13652), new Item(11802), new Item(11804), new Item(11806), new Item(11808) };
	public static final Item[] COMMON_PURPLE_KEY_REWARDS = { new Item(11283), new Item(12421), new Item(12420), new Item(12419), new Item(12457), new Item(12458), new Item(12459), new Item(12436) };
	
	/**
	 * The reward chest for the shiny and purple keys.
	 * 
	 * @param player
	 *            The player opening the chest
	 * @param item
	 *            The item we're using on the chest.
	 * @return Rewards the player a random reward.
	 */
	public static boolean searchChest(final Player player, Item item, int slot) {
		
		if (SHINY_KEY.getId() == item.getId()) {
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
				itemReceived = Utility.randomElement(UNCOMMON_SHINY_KEY_REWARDS);
				break;
			case 25:
				itemReceived = Utility.randomElement(RARE_SHINY_KEY_REWARDS);
				World.getWorld().sendWorldMessage("<img=25>@red@[Shiny key]: @blu@"+player.getUsername()+" received a "+ itemReceived.getName() + " from the shiny key chest.", false);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_SHINY_KEY_REWARDS);
			}
			player.getInventory().removeSlot(slot, SHINY_KEY.getAmount(), true);
			player.getActionSender().sendMessage("You unlock the chest with your shiny key.");
			player.getInventory().addOrCreateGroundItem(player, new Item(itemReceived.getId(), itemReceived.getAmount()));
		} else if (PURPLE_KEY.getId() == item.getId()) {
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
				itemReceived = Utility.randomElement(UNCOMMON_PURPLE_KEY_REWARDS);
				break;
			case 25:
				itemReceived = Utility.randomElement(RARE_PURPLE_KEY_REWARDS);
				World.getWorld().sendWorldMessage("<img=25>@red@[Purple key]: @blu@"+player.getUsername()+" received a "+ itemReceived.getName() + " from the purple key chest.", false);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_PURPLE_KEY_REWARDS);
			}
			
			player.getInventory().removeSlot(slot, PURPLE_KEY.getAmount(), true);
			player.getActionSender().sendMessage("You unlock the chest with your purple key.");
			player.getInventory().addOrCreateGroundItem(player, new Item(itemReceived.getId(), itemReceived.getAmount()));
		}
		player.playAnimation(Animation.create(881));
		player.playGraphics(Graphic.create(390));
		return true;

	}
}
