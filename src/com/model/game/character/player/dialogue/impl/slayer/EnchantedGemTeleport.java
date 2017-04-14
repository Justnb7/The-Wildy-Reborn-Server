package com.model.game.character.player.dialogue.impl.slayer;

import com.model.game.character.player.content.teleport.Teleport;
import com.model.game.character.player.content.teleport.Teleport.TeleportType;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.location.Location;

/**
 * The teleport options for the enchanted gem.
 * 
 * @author Patrick van Elderen
 *
 */
public class EnchantedGemTeleport extends Dialogue {
	
	@Override
	protected void start(Object... paramaters) {
		//TODO: Add other teleport restrictions.
		if (player.wildLevel > 20) {
			player.getActionSender().sendMessage("You need to be lower than level 20 in the wilderness to do this action.");
		} else {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Mazchna", "Vannaka", "Chaeldar", "Nieve", "Duradel");
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
				TeleportExecutor.teleport(player, new Teleport(new Location(3507, 3503, 0), TeleportType.NORMAL), true);
				player.getActionSender().sendMessage("You teleport to Mazchna's location.");
			} else if (index == 2) {
				TeleportExecutor.teleport(player, new Teleport(new Location(3147, 9913, 0), TeleportType.NORMAL), true);
				player.getActionSender().sendMessage("You teleport to Vannaka's location.");
			} else if (index == 3) {
				TeleportExecutor.teleport(player, new Teleport(new Location(3079, 3504, 0), TeleportType.NORMAL), true);
				player.getActionSender().sendMessage("You teleport to Chaeldar's location.");
			} else if (index == 4) {
				TeleportExecutor.teleport(player, new Teleport(new Location(2430, 3417, 0), TeleportType.NORMAL), true);
				player.getActionSender().sendMessage("You teleport to Nieve's location.");
			} else if (index == 5) {
				TeleportExecutor.teleport(player, new Teleport(new Location(2928, 3536, 0), TeleportType.NORMAL), true);
				player.getActionSender().sendMessage("You teleport to Duradel's location.");
			}
		}
	}
}