package com.venenatis.game.model.entity.player.dialogue.impl.item_on_item;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class BlueDarkBow extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.ITEM, 12766, "", "<col=6f0000>WARNING!</col> changing the color of your Dark Bow", "will render it untradeable. You will need to use a", "cleaning cloth to revert the changes. Are you sure?");
		setPhase(0);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(DialogueType.CHOICE, "Select an Option", "Yes.", "No.");
		} else if (isPhase(1)) {
			player.getActionSender().removeAllInterfaces();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch (index) {
			case 1: // Yes
				if (player.getInventory().containsAny(11235, 12757)) {
					player.getActionSender().sendMessage("You paint your Dark Bow blue.");
					player.getInventory().remove(new Item(11235), new Item(12757));
					player.getInventory().add(12766, 1);
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