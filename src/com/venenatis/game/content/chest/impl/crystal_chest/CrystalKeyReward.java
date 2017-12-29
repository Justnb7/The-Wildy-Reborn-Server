package com.venenatis.game.content.chest.impl.crystal_chest;

import java.util.ArrayList;
import java.util.Collection;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * Handles crystal key chest rewards.
 * 
 * @author Lennard
 *
 */
public class CrystalKeyReward {
	
	private final Player player;

	public CrystalKeyReward(Player player) {
		this.player = player;
	}
	
	public void giveReward() {
		final Collection<Item> rewards = getRewards();
		
		player.message("You open the chest and search for rewards...");
		
		player.getActionSender().sendItemsOnInterface(6963, rewards);
		player.getActionSender().sendInterface(6960);
		
		for(Item item : rewards) {
			player.getInventory().add(item);
		}
		
	}

	public Collection<Item> getRewards() {
		final Collection<Item> rewards = new ArrayList<Item>();
		final CrystalKeyRewardTable table = CrystalKeyRewardTable.forRoll(Utility.random(1000));
		if (table == CrystalKeyRewardTable.COMMON || table == CrystalKeyRewardTable.UNCOMMON
				|| table == CrystalKeyRewardTable.RARE) {
			rewards.add(new Item(1631, 1));
		} else if (table == CrystalKeyRewardTable.VERY_RARE) {
			rewards.add(new Item(6571, 1));
		} else if (table == CrystalKeyRewardTable.JACKPOT) {
			rewards.add(new Item(19496, 1));
		}
		int rewardTableAmount = 1;
		if(Utility.random(2) == 0) {
			rewardTableAmount++;
		}
		if(Utility.random(10) == 0) {
			rewardTableAmount++;
		}
		
		while (rewardTableAmount > 0) {
			final Item[] randomRewards = table.getRewardTable()[Utility.random(table.getRewardTable().length - 1)];

			if (randomRewards.length > 0) {
				for (Item toAdd : randomRewards) {
					if (toAdd == null) {
						continue;
					}
					rewards.add(toAdd);
				}
			}
			rewardTableAmount--;
		}
		return rewards;
	}
	
}