package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;

public class SaveCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		for (Player p2 : World.getWorld().getPlayers()) {
			if (p2 != null) {
				GameEngine.loginMgr.requestSave(p2);
				player.getActionSender().sendMessage("Submitted save requests for everybody online.");
			}
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.MODERATOR, Rights.ADMINISTRATOR, Rights.OWNER });
	}

}