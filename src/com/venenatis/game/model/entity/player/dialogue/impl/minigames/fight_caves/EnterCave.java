package com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class EnterCave extends Dialogue {
	
	private static int TZHAAR_MEJ_JAL = 2180;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, TZHAAR_MEJ_JAL, Expression.DISTRESSED, "You're on your own now JalYt, prepare to fight for", "your life!");
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
