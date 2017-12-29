package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Prince black dragon pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class PrinceBlackDragon extends Dialogue {
	
	private final int PET = 6636;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Shouldn't a prince only have two heads?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Why is that?");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Well,", "a standard Black dragon has one,", "the King has three so inbetween must have two?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "You're overthinking this.");
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