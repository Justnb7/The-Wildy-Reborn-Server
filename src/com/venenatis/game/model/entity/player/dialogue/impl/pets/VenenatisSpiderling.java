package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The Venenatis spiderling pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class VenenatisSpiderling extends Dialogue {
	
	private final int PET = 495;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.PLAYER, Expression.DEFAULT, "It's a damn good job I don't have arachnophobia.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "We're misunderstood.", "Without us in your house, you'd be infested with flies and ", "other REAL nasties.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Thanks for that enlightening fact.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Everybody gets one.");
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