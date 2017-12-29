package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class EmptyCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (BoundaryManager.isWithinBoundary(player.getLocation(), "PvP Zone")) {
			return;
		}

		if (player.getCombatState().inCombat()) {
			return;
		}
		player.getInventory().clear(true);
		player.getActionSender().sendMessage("You have cleared your inventory.");
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}