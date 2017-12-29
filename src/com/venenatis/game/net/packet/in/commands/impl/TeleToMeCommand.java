package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Teleports the given player to your location.
 * 
 * @author Lennard
 *
 */
public class TeleToMeCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		// TODO @Jack make it so players with spaces in their name can be
		// teleported.
		final String[] args = command.split(" ");
		String toMeName = args[1];
		String toMeName2 = args[2];
		
		String finalName = toMeName + toMeName2;
		
		for (Player players : World.getWorld().getPlayers()) {
			if (players == null || !finalName.equalsIgnoreCase(players.getUsername())) {
				continue;
			}
			players.setTeleportTarget(player.getLocation());
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.MODERATOR, Rights.ADMINISTRATOR, Rights.OWNER });
	}

}