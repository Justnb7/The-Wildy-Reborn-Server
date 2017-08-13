package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

public class RunescapeGuide extends Dialogue {
	
	private static final int NPC_ID = 3308;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.DEFAULT, "Welcome to Venenatis, "+player.getUsername(), "First, you need to choose your game mode.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			player.getGameModeSelection().open(player);
		}
	}
}
