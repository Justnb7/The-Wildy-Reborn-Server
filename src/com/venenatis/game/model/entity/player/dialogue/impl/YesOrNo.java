package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class YesOrNo extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, "Select an Option", "Yes.", "No.");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		switch (index) {
		case 1: // Yes
			switch ((Integer) player.getAttribute("yes_no_action")) {
			case 1:
				Player opponent = player.getDuelArena().getOtherPlayer();

				player.getDuelArena().finishDuelMatch();
				opponent.getDuelArena().invokeDuelVictory();
				player.getActionSender().removeAllInterfaces();
				break;

			}
		case 2: // No
			player.getActionSender().removeAllInterfaces();
			break;
		}
	}

}
