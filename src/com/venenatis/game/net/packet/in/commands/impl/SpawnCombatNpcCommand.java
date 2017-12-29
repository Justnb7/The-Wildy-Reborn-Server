package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class SpawnCombatNpcCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		int npc = Integer.parseInt(args[1]);
		boolean attack = Boolean.valueOf(args[1]);

		if (npc < 0) {
			return;
		}

		if (npc > 0) {
			NPC.spawnNpc(player, npc, new Location(player.getX(), player.getY() -1), 0, attack, true);
		}
		player.message("[mob_spawn= " + npc + "]");
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}