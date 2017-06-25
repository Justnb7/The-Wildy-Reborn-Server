package com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

public class NotEnoughTokens extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.STATEMENT, "You do not have enough tokens to continue.");
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
