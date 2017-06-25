package com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

public class NoTokens extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, 2461, Expression.DEFAULT, "You need atleast 200 warrior guild tokens.", "You can get some by operating the armour animator.");
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
