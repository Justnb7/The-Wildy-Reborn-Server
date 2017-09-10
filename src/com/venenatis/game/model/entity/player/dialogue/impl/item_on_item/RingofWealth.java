package com.venenatis.game.model.entity.player.dialogue.impl.item_on_item;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class RingofWealth extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.ITEM, 12785,  "", "You have imbued your Ring of Wealth.");
		setPhase(0);
	}
	
	@Override
	public void next() {
		if (isPhase(0)) {
			stop();
		}
	}

}
