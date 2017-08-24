package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Dark core pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Dark_Core extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Got any sigils for me?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Damnit Core-al!");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Let's bounce!");
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