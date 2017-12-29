package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Makes the player perform the given animation.
 * 
 * @author Lennard
 *
 */
public class HelpCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.getActionSender().removeAllInterfaces();
		player.getActionSender().sendInterface(59525);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}