package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.utility.Utility;

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
			send(Type.PLAYER, Expression.DEFAULT, "Do you miss your people?");
			setPhase(0);
		}
		send(Type.PLAYER, Expression.DEFAULT, "Are you hungry?");
		setPhase(5);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "I don't think so.");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "Jal-Zek Kl? (Foreigner hurt us?)");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "No, no, I wouldn't hurt you.");
			setPhase(4);
			break;
		case 4:
			stop();
			break;
		case 5:
			send(Type.NPC, Expression.DEFAULT, PET, "Kl-Kra!");
			setPhase(6);
			break;
		case 6:
			send(Type.PLAYER, Expression.DEFAULT, "Ooookay...");
			setPhase(4);
			break;
		}
	}

}
