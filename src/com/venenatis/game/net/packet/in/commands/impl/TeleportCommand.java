package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Teleports a player to the given location.
 * 
 * @author Lennard
 *
 */
public class TeleportCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		if (args.length == 4) {
			player.setTeleportTarget(
					new Location(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
		} else if (args.length == 3) {
			player.setTeleportTarget(
					new Location(Integer.parseInt(args[1]), Integer.parseInt(args[2]), player.getLocation().getZ()));
		} else {
			player.getActionSender().sendMessage("Please use ::move (x) (y) (z)");
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}