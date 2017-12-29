package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class ChompyChick extends Dialogue {
	
	private final int PET = 4001;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, PET, Expression.DEFAULT, "*Chirp!*");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			stop();
			break;
		}
	}
}
