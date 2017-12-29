package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Zilyana Jr. pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class ZilyanaJr extends Dialogue {
	
	private final int PET = 6633;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "FIND THE GODSWORD!");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "FIND THE GODSWORD!");
			setPhase(1);
			break;
		case 1:
			if(player.getInventory().contains(11806)) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "I FOUND THE GODSWORD!");
				setPhase(2);
			} else {
				setPhase(3);
			}
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "GOOD!!!!!");
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