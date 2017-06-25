package com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

public class DefenderInPosession extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, 2461, Expression.DEFAULT, "You are currently in posession of a " + ItemDefinition.get(player.getWarriorsGuild().currentDefender()) + ".", "It will cost 200 tokens to re-enter the cyclops area.", "Do you want to enter? It will cost you.");
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
				if (player.getInventory().contains(8851, 200)) {
					player.setTeleportTarget(new Location(2847, 3540, 2));
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
