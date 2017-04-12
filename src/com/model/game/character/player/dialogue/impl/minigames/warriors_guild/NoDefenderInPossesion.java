package com.model.game.character.player.dialogue.impl.minigames.warriors_guild;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.location.Position;

public class NoDefenderInPossesion extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, 2461, Expression.DEFAULT, "You are not in the possesion of a defender.", "You must kill cyclops to obtain a defender.", "The fee for entering the area is 200 tokens.", "Do you want to enter?");
		setPhase(0);
	}

	@Override
	protected void next() {
		System.out.println("next : phase " + getPhase());
		if (getPhase() == 0) {
			send(Type.CHOICE, "Select Option", "Enter", "Nevermind");
			setPhase(1);
		}
	}

	@Override
	protected void select(int index) {
		if (getPhase() == 1) {
			switch (index) {
			case 1:
				if (player.getItems().playerHasItem(8851, 200)) {
					player.move(new Position(2847, 3540, 2));
					player.getWarriorsGuild().cycle();
					stop();
				} else {
					player.dialogue().start("PLAYER_HAS_NO_TOKENS");
				}
				break;
			case 2:
				stop();
				break;
			}
		}
	}
}