package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The General Graardor Jr. pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class General_Graardor_Jr extends Dialogue {
	
	private final int PET = 6632;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "Not sure this is going to be worth my time but... how are", "you?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.NPC, Expression.DEFAULT, PET, "SFudghoigdfpDSOPGnbSOBNfdbdnopbdn", "opbddfnopdfpofhdARRRGGGGH");
			setPhase(1);
			break;
		case 1:
			send(Type.PLAYER, Expression.DEFAULT, "Nope. Not worth it.");
			setPhase(2);
			break;
		case 2:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}