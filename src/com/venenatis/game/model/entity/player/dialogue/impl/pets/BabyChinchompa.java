package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class BabyChinchompa extends Dialogue {
	
	/**
	 * The pet identifier
	 */
	private int petId;

	@Override
	protected void start(Object... parameters) {
		petId = player.getPet();
		if (petId == Pet.BABY_CHINCHOMPA_GOLD.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Squeaka squeaka!");
			setPhase(0);
		} else {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Squeak squeak!");
			setPhase(0);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			stop();
			break;
		}
	}

}
