package com.venenatis.game.model.entity.player.dialogue.impl.slayer.abilities;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;
import com.venenatis.game.model.masks.Animation;

public class TeleportToTask extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		if(player.canTeleportToSlayerTask()) {
			send(Type.ITEM, 6798, "", "You can make out some faded words on the ancient", "parchment. It's an archaic invocation of the Slayer masters.", "However there's nothing more for you to learn.");
		    setPhase(2);
		} else {
			send(Type.ITEM, 6798, "", "You can make out some faded words on the ancient", "parchment. It appears to be an archaic invocation of the", "Slayer masters! Would you like to absorb its power?");
			setPhase(0);
		}
	}

	@Override
	protected void next() {
		if(getPhase() == 0) {
			send(Type.CHOICE, "Choose option.", "Learn to teleport to slayer task", "Cancel");
		} else {
			if(getPhase() == 1) {
				player.setCanTeleportToTask(true);
				stop();
			} else {
				if(getPhase() == 2) {
					stop();
				}
			}
		}
	}
	
	@Override
	protected void select(int index) {
		if(getPhase() == 0) {
			switch(index) {
			case 1:
				player.playAnimation(Animation.create(7403));
				send(Type.ITEM, 6798, "", "You study the scroll and learn a new slayer @red@ability@bla@.");
				setPhase(1);
				break;
			case 2:
				stop();
				break;
			}
		}
	}
}
