package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

/**
 * Spawns an object.
 * 
 * @author Lennard
 *
 */
public class ObjectSpawnCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		if (args.length != 2) {
			player.message("Please use ::object (objecId).");
			return;
		}
		final int objectId = Integer.parseInt(args[1]);
		Server.getGlobalObjects().add(new GameObject(objectId, player.getX(), player.getY(), player.getZ(), 0, 10));
		player.message("Spawned objectId: " + objectId + ".");
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}