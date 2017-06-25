package com.venenatis.game.model.entity.player.dialogue.impl.minigames;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

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
				player.getActionSender().removeAllInterfaces();
				player.setTeleportTarget(new Location(3551, 9691, 0));
				break;
			case 2:
				player.getActionSender().removeAllInterfaces();
				break;
			}
		}
	}

}
