package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Kalphite princess pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Kalphite_Princess extends Dialogue {
	
	private final int PET = 6638;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "What is it with your kind and potato cactus?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Truthfully?");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Yeah, please.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Soup. We make a fine soup with it.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Kalphites can cook?");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Nah, we just collect it and put it there because", "we know fools like yourself will come", "down looking for it then inevitably be killed by my mother.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Evidently not, that's how I got you!");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Touché");
			setPhase(7);
			break;
		case 7:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}