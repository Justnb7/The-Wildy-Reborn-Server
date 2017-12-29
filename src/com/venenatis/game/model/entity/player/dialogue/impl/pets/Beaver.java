package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Beaver extends Dialogue {
	
	private final int PET = 6717;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "How much wood could a woodchuck chuck if a woodchuck could chuck wood?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Approximately 32,768 depending on his woodcutting level.");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		}
	}
}