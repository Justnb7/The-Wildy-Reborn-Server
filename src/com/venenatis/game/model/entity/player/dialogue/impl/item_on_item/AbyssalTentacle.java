package com.venenatis.game.model.entity.player.dialogue.impl.item_on_item;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class AbyssalTentacle extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.STATEMENT, "<col=6f0000>Warning!</col>", "The tentacle will gradually consume your whip and destroy it. you", "won't be able to get the whip out again.", "The combined item is not tradeable.");
		setPhase(0);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(DialogueType.CHOICE, "Are you sure you wish to do this?", "Yes, let the tentacle consume the whip.", "No, I'll keep my whip.");
		} else if (isPhase(1)) {
			player.getActionSender().removeAllInterfaces();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch (index) {
			case 1: // Yes
				if (player.getInventory().containsAny(4151, 12004)) {
					player.getInventory().remove(new Item(4151), new Item(12004));
					player.getInventory().add(12006, 1);
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