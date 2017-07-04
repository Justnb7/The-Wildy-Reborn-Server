package com.venenatis.game.content.rewards;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;

/**
 * This class represents the wilderness rewards for killing players.
 * Rewards such as blood money points emblems etc...
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van  Elderen</a>
 *
 */
public class WildernessRewards {
	
	
	
	public static boolean receive_reward(Player killer, Player opponent) {
		long wealth = getWealth(opponent);
		killer = World.getWorld().lookupPlayerByName(opponent.getCombatState().getDamageMap().getKiller());
		
		if(wealth > Constants.PK_POINTS_WEALTH) {
			//TODO implement rewards here
		}
		return false;
	}
	
	private static long getWealth(Player player) {
		LinkedList<Item> all = new LinkedList<>();
		
		Item[] inv_items = player.getInventory().toNonNullArray();
		for (Item inventory : inv_items) {
			all.add(inventory);
		}
		
		Item[] equipment_items = player.getEquipment().toNonNullArray();
		for(Item equipment : equipment_items) {
			all.addLast(equipment);
		}

		int finalamount = player.isSkulled() ? 0 : 3;
		if (player.isActivePrayer(Prayers.PROTECT_ITEM))
			finalamount++;
		int amount = finalamount;
		if (amount > 0) {
			all.sort(Collections.reverseOrder((one, two) -> Double.compare(one.getValue(), two.getValue())));
			for (Iterator<Item> it = all.iterator(); it.hasNext();) {
				Item next = it.next();
				if (amount == 0) {
					break;
				}
				if (next.isStackable() && next.getAmount() > 1) {
					next.amount -= finalamount == 0 ? 1 : finalamount;
				} else {
					it.remove();
				}
				amount--;
			}
		}
		long wealth = 0;
		for (Item totalWealth : all) {
			wealth += (totalWealth.getValue() * totalWealth.amount);
		}
		player.debug(""+wealth);
		return wealth;
	}

}
