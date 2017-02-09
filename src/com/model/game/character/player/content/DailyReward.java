package com.model.game.character.player.content;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * This class represents the functionality of our daily rewards.
 * 
 * @author Patrick van Elderen
 *
 */
public class DailyReward {

	private Player player;

	public DailyReward(Player player) {
		this.player = player;
	}

	/**
	 * Grab the current day
	 * 
	 * @return
	 */
	public int getDay() {
		Calendar calendar = new GregorianCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * Reward the player with a free daily reward.
	 */
	public void dailyReward() {
		if (getDay() != player.dayOfWeek) {
			player.dayOfWeek = getDay();
			reward();
		}
	}
	
	public void reward() {
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
			itemReceived = Utility.randomElement(UNCOMMON);
			break;
		case 25:
			itemReceived = Utility.randomElement(RARE);
			break;
		default:
			itemReceived = Utility.randomElement(COMMON);
		}
		player.getItems().addOrCreateGroundItem(itemReceived.getId(), itemReceived.getAmount());
		player.write(new SendMessagePacket("You've recieved "+itemReceived.amount+" x "+itemReceived.getName()+" for playing, thanks!"));
		
		if(ItemDefinition.forId(itemReceived.getId()).getShopValue() > 10_000_000) {
			World.getWorld().sendWorldMessage("<img=11>[Server]: "+player.getName()+" was awarded with "+Utility.getAOrAn(itemReceived.getDefinition().getName())+" from the daily login rewards.", false);
		}
	}

	/**
	 * Common rewards
	 */
	private Item[] COMMON = { new Item(537, 15), new Item(4151), new Item(989), new Item(6585), new Item(4153), new Item(13307, 5) };
	
	/**
	 * Uncommon rewards
	 */
	private Item[] UNCOMMON = { new Item(4716), new Item(4718), new Item(4720), new Item(4722), new Item(11804), new Item(11806), new Item(11808) };
	
	/**
	 * Rare rewards
	 */
	private Item[] RARE = { new Item(11802), new Item(21999), new Item(22000), new Item(22002), new Item(22003), new Item(22004), new Item(22005) };

}
