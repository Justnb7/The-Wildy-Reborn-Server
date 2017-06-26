package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The Callisto Cub pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Callisto_Cub extends Dialogue {
	
	private final int PET = 497;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Why the grizzly face?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "You're not funny...");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "You should get in the.... sun more.");
			setPhase(2);
			break;
		case 2:
			send(Type.NPC, Expression.DEFAULT, PET, "You're really not funny...");
			setPhase(3);
			break;
		case 3:
			send(Type.PLAYER, Expression.DEFAULT, "One second,", "let me take a picture of you with my.... kodiak camera.");
			setPhase(4);
			break;
		case 4:
			send(Type.NPC, Expression.DEFAULT, PET, ".....");
			setPhase(5);
			break;
		case 5:
			send(Type.PLAYER, Expression.DEFAULT, "Feeling.... blue.");
			setPhase(6);
			break;
		case 6:
			send(Type.NPC, Expression.DEFAULT, PET, "If you don't stop,", "I'm going to leave some... brown... at your feet, human.");
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