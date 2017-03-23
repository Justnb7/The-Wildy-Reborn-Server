package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Dagannoth supreme pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Dagannoth_Supreme extends Dialogue {
	
	private final int PET = 6628;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Hey, so err... I kind of own you now.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Tsssk. Next time you enter those caves,", "human, my father will be having words.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Maybe next time I'll add your brothers to my collection.");
			setPhase(2);
			break;
		case 2:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}