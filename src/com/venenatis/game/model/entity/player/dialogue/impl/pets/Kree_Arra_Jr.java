package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The Kree'arra Jr pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Kree_Arra_Jr extends Dialogue {
	
	private final int PET = 6631;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Huh... that's odd... I thought that would be big news.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "You thought what would be big news?");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Well there seems to be an absence of a certain", "ornithological piece: a headline regarding mass", "awareness of a certain avian variety.");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "What are you talking about?");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "Oh have you not heard?", "It was my understanding that everyone had heard....");
			setPhase(4);
			break;
		case 4:
			send(Type.NPC, Expression.DEFAULT, PET, "Heard wha...... OH NO!!!!?!?!!?!");
			setPhase(5);
			break;
		case 5:
			send(Type.PLAYER, Expression.DEFAULT, "OH WELL THE BIRD,", "BIRD, BIRD, BIRD BIRD IS THE WORD.", "OH WELL THE BIRD, BIRD, BIRD,", "BIRD BIRD IS THE WORD.");
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