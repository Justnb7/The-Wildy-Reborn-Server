package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.content.KillDeathRatioHighscores;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Opens the K/D ratio interface.
 * 
 * @author Lennard
 *
 */
public class HighCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		KillDeathRatioHighscores.openInterface(player);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}