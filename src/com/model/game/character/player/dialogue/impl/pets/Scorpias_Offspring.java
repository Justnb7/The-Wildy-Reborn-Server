package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Scorpia's offspring pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Scorpias_Offspring extends Dialogue {
	
	private final int PET = 5547;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "At night time,", "if I were to hold ultraviolet light over you,", "would you glow?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Two things wrong there, human.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Oh?");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "One,", "When has it ever been night time here?");
			setPhase(3);
			break;
		case 3:
			send(Type.NPC, Expression.DEFAULT, PET, "Two,", "When have you ever seen ultraviolet light around here?");
			setPhase(4);
			break;
		case 4:
			send(Type.PLAYER, Expression.DEFAULT, "Hm...");
			setPhase(5);
			break;
		case 5:
			send(Type.NPC, Expression.DEFAULT, PET, "In answer to your question though.", "Yes I, like every scorpion, would glow.");
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