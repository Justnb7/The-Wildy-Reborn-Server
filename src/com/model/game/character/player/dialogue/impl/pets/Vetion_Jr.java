package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Vet'ion jr pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Vetion_Jr extends Dialogue {
	
	private final int PET = player.getPet() == 5536 ? 5536 : player.getPet() == 5537 ? 5537 : -1;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Who is the true lord and king of the lands?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "The mighty heir and lord of the Wilderness.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Where is he? Why hasn't he lifted your burden?");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "I have not fulfilled my purpose.");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "What is your purpose?");
			setPhase(4);
			break;
		case 4:
			send(Type.NPC, Expression.DEFAULT, PET, "Not what is,", "what was. A great war tore this land apart and,", "for my failings in protecting this ", "land, I carry the burden of its waste.");
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