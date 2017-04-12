package com.model.game.character.player.dialogue.impl.minigames;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.location.Position;

public class BarrowsTunnel extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Select Option", "Enter the tunnel!", "No, I'm not ready yet!");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch(index) {
			case 1:
				player.getActionSender().sendRemoveInterfacePacket();
				player.move(new Position(3551, 9691, 0));
				break;
			case 2:
				player.getActionSender().sendRemoveInterfacePacket();
				break;
			}
		}
	}

}
