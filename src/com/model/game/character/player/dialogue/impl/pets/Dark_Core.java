package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Dark core pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Dark_Core extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Got any sigils for me?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.PLAYER, Expression.DEFAULT, "Damnit Core-al!");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Let's bounce!");
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