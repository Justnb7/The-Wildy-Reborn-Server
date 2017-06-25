package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The Dagannoth prime pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Dagannoth_Prime extends Dialogue {
	
	private final int PET = 6629;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "So despite there being three kings,", "you're clearly the leader, right?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Definitely.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "I'm glad I got you as a pet.");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "Ugh. Human, I'm not a pet.");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "Stop following me then.");
			setPhase(4);
			break;
		case 4:
			send(Type.NPC, Expression.DEFAULT, PET, "I can't seem to stop.");
			setPhase(5);
			break;
		case 5:
			send(Type.PLAYER, Expression.DEFAULT, "Pet.");
			setPhase(6);
			break;
		case 6:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}