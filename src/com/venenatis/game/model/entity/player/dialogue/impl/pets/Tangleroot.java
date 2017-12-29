package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Tangleroot extends Dialogue {
	
	private final int PET = 7335;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(2);
		if(random == 0 ) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "How are you doing today?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Hello there pretty plant.");
			setPhase(2);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "I am Tangleroot!");
			setPhase(3);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I am Tangleroot!");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I am Tangleroot!");
			setPhase(1);
			break;
		case 3:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I am "+player.getUsername()+"!");
			setPhase(1);
			break;
		}
	}

}