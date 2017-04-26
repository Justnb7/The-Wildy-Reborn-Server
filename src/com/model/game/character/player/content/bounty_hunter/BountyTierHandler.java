package com.model.game.character.player.content.bounty_hunter;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * Handles the bounty hunter tiers
 * 
 * @author Mobster
 *
 */
public class BountyTierHandler {

	/**
	 * Handles upgrading a bounty hunter tier
	 * 
	 * @param player
	 *            The {@link Player} to upgrade a tier for
	 */
	public static void upgrade(Player player) {
		emb_loop: for (int x = BountyHunterEmblem.EMBLEMS.size() - 1; x >= 0; x--) {
			BountyHunterEmblem emblem = BountyHunterEmblem.valueOf(x);
			for (int i = 0; i < player.getInventory().size(); i++) {
				int id = player.getInventory().getId(i) - 1;
				if (emblem.getItemId() == id) {

					/*
					 * Can't upgrade a tier 10 as its already as high as it goes
					 */
					if (emblem.getIndex() == BountyHunterEmblem.TIER_10.getIndex()) {
						continue;
					}

					player.getInventory().set(i, new Item(BountyHunterEmblem.getNext(emblem).getItemId()));
					break emb_loop;
				}
			}
		}
	}

	/**
	 * Handles downgrading a bounty hunter tier for the killer
	 * 
	 * @param player
	 *            The {@link Player} To downgrade a tier for
	 */
	public static int downgrade(Player player, int id) {
		BountyHunterEmblem emblem = BountyHunterEmblem.get(id);
		if (emblem == null) {
			return -1;
		}
		
		if (emblem.getIndex() == 0) {
			return BountyHunterEmblem.TIER_1.getItemId();
		} else {
			return BountyHunterEmblem.getPrevious(emblem).getItemId();
		}
	}

}