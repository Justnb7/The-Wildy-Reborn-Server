package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class PetPenanceQueen extends Dialogue {
	
	private final int PET = 6642;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Of all the high gamble rewards I could have won, I won you...");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Keep trying, human. You'll never win that Dragon", "Chainbody");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		}
	}

}