package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Messages the player with their current position.
 * 
 * @author Lennard
 *
 */
public class PositionCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.message(player.getLocation().toString());
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] {Rights.OWNER});
	}

}