package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Teleports you to the given player.
 * 
 * @author Lennard
 *
 */
public class TeleToCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		// TODO @Jack make it so you can tele to players with spaces in their
		// name.
		final String[] args = command.split(" ");
		String toName = args[1];
		for (Player players : World.getWorld().getPlayers()) {
			if (players == null || !toName.equalsIgnoreCase(players.getUsername())) {
				continue;
			}
			player.setTeleportTarget(players.getLocation());
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.MODERATOR, Rights.ADMINISTRATOR, Rights.OWNER });
	}

}