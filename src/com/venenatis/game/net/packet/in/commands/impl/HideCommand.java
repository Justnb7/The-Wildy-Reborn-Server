package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class HideCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		
		final String[] args = command.split(" ");
		boolean state = Boolean.valueOf(args[1]);
		
		player.setVisible(state);
		player.message("Current visible state is: "+state);
		for (Player other : player.getLocalPlayers()) {
			if (other == null) {
				continue;
			}

			if (other.getLocalPlayers().contains(player)) {
				other.getLocalPlayers().remove(player);
			}
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}