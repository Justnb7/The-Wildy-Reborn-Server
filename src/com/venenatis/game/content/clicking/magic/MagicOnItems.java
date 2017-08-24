package com.venenatis.game.content.clicking.magic;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.spell.impl.HighAlchemy;
import com.venenatis.game.model.combat.magic.spell.impl.LowAlchemy;
import com.venenatis.game.model.entity.player.Player;

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

		if (player.getCombatState().isDead()) {
			return;
		}

		if (player.isTeleporting()) {
			return;
		}

		player.debug(String.format("[MagicOnItem] - ItemId: %d Slot: %d ChildId: %d SpellId: %d", itemId, slot, childId, spellId));

		final Item item = player.getInventory().get(slot);

		if (item == null || item.getId() != itemId) {
			return;
		}
		
		player.getMagic().setItemUsed(item);

		switch (spellId) {

		/* Low Alchemy */
		case 1162:
		    player.getMagic().cast(new LowAlchemy());
		    break;

		/* High Alchemy */
		case 1178:
		    player.getMagic().cast(new HighAlchemy());
		    break;

		}
	}

}