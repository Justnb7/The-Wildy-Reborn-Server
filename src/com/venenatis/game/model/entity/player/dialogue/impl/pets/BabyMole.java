package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Giant mole pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class BabyMole extends Dialogue {
	
	private final int PET = 6635;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "Hey, Mole. How is life above ground?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Well, the last time I was above ground,", "I was having to contend with people throwing snow at ", "some weird yellow duck in my park.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why were they doing that?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "No idea,", "I didn't stop to ask as an angry mob", "was closing in on them pretty quickly.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sounds awful.");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Anyway, keep Molin'!");
			setPhase(5);
			break;
		case 5:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}