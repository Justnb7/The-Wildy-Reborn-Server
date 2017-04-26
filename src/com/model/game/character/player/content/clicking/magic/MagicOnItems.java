package com.model.game.character.player.content.clicking.magic;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * Handles the action of using spells with items.
 * 
 * @author SeVen
 */
public class MagicOnItems {

    /**
     * Handles the action of using spells with {@link Item}s for a player.
     * 
     * @param player
     *            The player performing this action.
     * 
     * @param itemId
     *            The id of the item.
     * 
     * @param slot
     *            The slot of the item.
     * 
     * @param childId
     *            Not really sure
     * 
     * @param spellId
     *            The id of the spell.
     */
	public static void handleAction(Player player, int itemId, int slot, int childId, int spellId) {

		if (player.isDead()) {
			return;
		}

		if (player.isTeleporting()) {
			return;
		}

		if (player.inDebugMode()) {
			System.out.println(String.format("[MagicOnItem] - ItemId: %d Slot: %d ChildId: %d SpellId: %d", itemId, slot, childId, spellId));
		}

		final Item item = player.getInventory().get(slot);

		if (item == null || item.getId() != itemId) {
			return;
		}

		switch (spellId) {

		/* Low Alchemy */
		case 1162:
			break;

		/* High Alchemy */
		case 1178:
			break;

		}
	}

}