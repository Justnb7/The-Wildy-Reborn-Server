package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Zilyana Jr. pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Zilyana_Jr extends Dialogue {
	
	private final int PET = 6633;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "FIND THE GODSWORD!");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "FIND THE GODSWORD!");
			setPhase(1);
			break;
		case 1:
			if(player.getItems().playerHasItem(11806)) {
				send(Type.PLAYER, Expression.DEFAULT, "I FOUND THE GODSWORD!");
				setPhase(2);
			} else {
				setPhase(3);
			}
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "GOOD!!!!!");
			setPhase(3);
			break;
		case 3:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}