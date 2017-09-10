package com.venenatis.game.model.entity.player.dialogue.impl.item_on_item;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class GraniteMaulUpgrade extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.ITEM, 12848, "", "The upgraded version has the same stats and behaviour,", "but is untradeable. Although you can revert it to its", "original form, you will not get the upgrade kit back.");
		setPhase(0);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(DialogueType.CHOICE, "Select an Option", "Apply the upgrade.", "Cancel.");
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch (index) {
			case 1: // Yes
				if (player.getInventory().containsAny(4153, 12849)) {
					send(DialogueType.ITEM, 12848, "", "You apply the upgrade.");
					player.getInventory().remove(new Item(4153), new Item(12849));
					player.getInventory().add(12848, 1);
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