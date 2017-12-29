package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Dagannoth rex pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class DagannothRex extends Dialogue {
	
	private final int PET = 6630;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Do you have any berserker rings?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Nope.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "You sure?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Yes.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "So, if I tipped you upside down and shook you,", "you'd not drop any berserker rings?");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Nope.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "What if I endlessly killed your father for weeks on end,", "would I get one then.");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Been done by someone, nope.");
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
