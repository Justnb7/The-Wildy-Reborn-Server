package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The K'ril Tsutsaroth Jr pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class KrilTsutsarothJr extends Dialogue {
	
	private final int PET = 6634;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "How's life in the light?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Burns slightly.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "You seem much nicer than your father. He's mean.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "If you were stuck in a very dark cave for centuries", "you'd be pretty annoyed too.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I guess.");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "He's actually quite mellow really.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Uh.... Yeah.");
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