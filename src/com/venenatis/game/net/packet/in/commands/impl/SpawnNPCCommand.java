package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Spawns an NPC.
 * 
 * @author Lennard
 *
 */
public class SpawnNPCCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		int npc = Integer.parseInt(args[1]);
		int health = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
		if (npc < 0) {
			return;
		}

		Location spawnLocation = new Location(player.getX(), player.getY() - 1, player.getZ());
		NPC spawn = null;
		if (npc > 0) {
			spawn = new NPC(npc, spawnLocation, 0);
			spawn.setLocation(spawnLocation);
			World.getWorld().register(spawn);
			if (health > 0) {
				spawn.setHitpoints(health);
			}
		}
		player.message("[mob_spawn= " + npc + "] index "+spawn.getIndex());
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}