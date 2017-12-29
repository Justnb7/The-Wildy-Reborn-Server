package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Snakeling pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Snakeling extends Dialogue {
	
	private final int PET = player.getPet() == 2130 ? 2130 : player.getPet() == 2131 ? 2131 : player.getPet() == 2132 ? 2132 : -1;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Hey little snake!");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Soon, Zulrah shall establish dominion over this plane.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Wanna play fetch?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Submit to the almighty Zulrah.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Walkies? Or slidies...?");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Zulrah's wilderness as a God will soon be demonstrated.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I give up...");
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