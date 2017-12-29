package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Sends a still gfx.
 * 
 * @author Lennard
 *
 */
public class StillGfxCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		if (args.length != 2) {
			player.message("Please use ::stillgfx (gfxId).");
			return;
		}
		final int gfx = Integer.parseInt(args[1]);
		player.getActionSender().stillGfx(gfx, new Location(player.getX(), player.getY() - 1, 0), 10);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}