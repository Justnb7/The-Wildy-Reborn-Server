package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The Chaos elemental Jr pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Chaos_Elemental_Jr extends Dialogue {
	
	private final int PET = 5907;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Is it true a level 3 skiller caught one of your siblings?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "Yes, they killed my mummy,", "kidnapped my brother,", "smiled about it and went to sleep.");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Aww, well you have me now!", "I shall call you Squishy and you shall be mine", "and you shall be my Squishy");
			setPhase(2);
			break;
		case 2:
			send(Type.PLAYER, Expression.DEFAULT, "Come on, Squishy come on, little Squishy!");
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