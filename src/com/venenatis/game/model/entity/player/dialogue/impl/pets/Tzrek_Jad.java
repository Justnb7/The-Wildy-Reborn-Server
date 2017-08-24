package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.util.Utility;

/**
 * The Tzrek jad pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Tzrek_Jad extends Dialogue {
	
	private final int PET = 5892;

	@Override
	protected void start(Object... parameters) {
		int randomDialogue = Utility.random(1);
		if(randomDialogue == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Do you miss your people?");
			setPhase(0);
		}
		send(DialogueType.PLAYER, Expression.DEFAULT, "Are you hungry?");
		setPhase(5);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I don't think so.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Jal-Zek Kl? (Foreigner hurt us?)");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "No, no, I wouldn't hurt you.");
			setPhase(4);
			break;
		case 4:
			stop();
			break;
		case 5:
			send(DialogueType.NPC, Expression.DEFAULT, PET, "Kl-Kra!");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Ooookay...");
			setPhase(4);
			break;
		}
	}

}
