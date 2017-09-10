package com.venenatis.game.model.entity.player.dialogue.impl.item_on_item;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class BlessedSaradominSword extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.ITEM, 12809, "", "A blessed Saradomin sword is untradeable, with an", "Attack requirement of 75. After 10,000 hits, the sword", "crumbles to dust, and you get the Tear back.");
		setPhase(0);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(DialogueType.CHOICE, "Select an Option", "Add the Tear ti the sword?", "Cancel.");
		} else if (isPhase(1)) {
			player.getActionSender().removeAllInterfaces();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch (index) {
			case 1: // Yes
				if (player.getInventory().containsAny(11838, 12804)) {
					send(DialogueType.ITEM, 12809, "", "The Tear of Saradomin blesses your sword.", "It has 10,000 hits left.");
					player.getInventory().remove(new Item(11838), new Item(12804));
					player.getInventory().add(12809, 1);
					setPhase(1);
				}
				break;
			case 2: // No
				player.getActionSender().removeAllInterfaces();
				break;
			}
		}
	}
}