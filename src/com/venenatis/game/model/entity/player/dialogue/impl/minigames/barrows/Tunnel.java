package com.venenatis.game.model.entity.player.dialogue.impl.minigames.barrows;

import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class Tunnel extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, "You've found a hidden tunnel, do you want to enter?", "Yeah I'm fearless!", "No way, that looks scary!");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch(index) {
			case 1:
				player.setTeleportTarget(BarrowsHandler.TUNNEL_LOCATIONS[BarrowsHandler.getRandom().nextInt(BarrowsHandler.TUNNEL_LOCATIONS.length)]);
				break;
			case 2:
				stop();
				break;
			}
		}
	}

}
