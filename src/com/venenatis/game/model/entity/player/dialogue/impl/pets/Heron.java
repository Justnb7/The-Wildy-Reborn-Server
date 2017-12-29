package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Heron extends Dialogue {
	
	private final int PET = 6715;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, PET, Expression.DEFAULT, "Hop inside my mouth if you want to live!");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'm not falling for that... I'm not a fish! I've got more foresight than that.");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		}
	}

}