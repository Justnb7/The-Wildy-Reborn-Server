package com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class ExitCave extends Dialogue {

	private static int TZHAAR_MEJ_JAL = 2180;
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, TZHAAR_MEJ_JAL, Expression.CALM_TALK, "Well I suppose you tried... better luck next time.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		System.out.println("next : phase " + getPhase());
		if (getPhase() == 0) {
			stop();
		}
	}

}
