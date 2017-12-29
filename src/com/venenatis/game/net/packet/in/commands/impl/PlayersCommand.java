package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Tells a player how many players there are currently online.
 * 
 * @author Lennard
 *
 */
public class PlayersCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.getActionSender().sendMessage("<col=255>There are currently " + World.getWorld().getPlayerCount() + " players online!");
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}