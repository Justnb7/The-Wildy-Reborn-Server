package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Kraken Jr pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Kraken extends Dialogue {
	
	private final int PET = 6640;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "What's Kraken?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Not heard that one before.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "How are you actually walking on land?");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "We have another leg,", "just below the center of our body that", "we use to move across solid surfaces.");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "That's.... interesting.");
			setPhase(4);
			break;
		case 4:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}