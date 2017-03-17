package com.model.game.character.player.dialogue.impl.minigames.warriors_guild;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

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
