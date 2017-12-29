package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Displays a GFX on the player.
 * 
 * @author Lennard
 *
 */
public class GfxCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		int gfxId = Integer.parseInt(args[1]);
		player.playGraphic(Graphic.create(gfxId, 0, 100));
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}