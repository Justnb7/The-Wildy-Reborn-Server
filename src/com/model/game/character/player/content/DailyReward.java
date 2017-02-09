package com.model.game.character.player.content;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.model.game.World;
import com.model.game.character.player.Player;
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
	private int getDay() {
		Calendar calendar = new GregorianCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * Allows the server to set the current day of week.
	 */
	public void setDayOfWeek() {
		player.dayOfWeek = getDay();
	}

	/**
	 * Reward the player with a free gamble.
	 */
	public void dailyReward() {
		if (getDay() == player.dayOfWeek) {
			// player.write(new SendMessagePacket("@dre@It's a new day, you've been awared with a ... for logging in today."));
			player.dailyReward = true;
			reward();
			// TODO reward with a random reward
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
		
		if(ItemDefinition.forId(itemReceived.getId()).getShopValue() > 10_000_000) {
			World.getWorld().sendWorldMessage("<img=11>[Server]: "+player.getName()+" was awarded with "+Utility.getAOrAn(itemReceived.getDefinition().getName())+" from the daily login rewards.", false);
		}
	}

	/**
	 * Common rewards
	 */
	private Item[] COMMON = { new Item(537, 15) };
	
	/**
	 * Uncommon rewards
	 */
	private Item[] UNCOMMON = { new Item(4151), new Item(6585) };
	
	/**
	 * Rare rewards
	 */
	private Item[] RARE = { new Item(22000) };

}
