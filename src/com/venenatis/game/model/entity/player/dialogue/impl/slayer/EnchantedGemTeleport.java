package com.venenatis.game.model.entity.player.dialogue.impl.slayer;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The teleport options for the enchanted gem.
 * 
 * @author Patrick van Elderen
 *
 */
public class EnchantedGemTeleport extends Dialogue {
	
	@Override
	protected void start(Object... paramaters) {
		if (player.getWildLevel() > 30) {
			player.getActionSender().sendMessage("You need to be lower than level 30 in the wilderness to do this action.");
		} else {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Mazchna", "Vannaka", "Chaeldar", "Nieve", "Duradel");
			setPhase(0);
		}
	}
	
	@Override
	protected void next() {
		
	}
	
	@Override
	protected void select(int index) {
		if (getPhase() == 0) {
			if (index == 1) {
				player.getTeleportAction().teleport(new Location(3507, 3503, 0), TeleportTypes.MODERN, false);
				player.getActionSender().sendMessage("You teleport to Mazchna's location.");
			} else if (index == 2) {
				player.getTeleportAction().teleport(new Location(3147, 9913, 0), TeleportTypes.MODERN, false);
				player.getActionSender().sendMessage("You teleport to Vannaka's location.");
			} else if (index == 3) {
				player.getTeleportAction().teleport(new Location(3079, 3504, 0), TeleportTypes.MODERN, false);
				player.getActionSender().sendMessage("You teleport to Chaeldar's location.");
			} else if (index == 4) {
				player.getTeleportAction().teleport(new Location(2430, 3417, 0), TeleportTypes.MODERN, false);
				player.getActionSender().sendMessage("You teleport to Nieve's location.");
			} else if (index == 5) {
				player.getTeleportAction().teleport(new Location(2928, 3536, 0), TeleportTypes.MODERN, false);
				player.getActionSender().sendMessage("You teleport to Duradel's location.");
			}
		}
	}
}