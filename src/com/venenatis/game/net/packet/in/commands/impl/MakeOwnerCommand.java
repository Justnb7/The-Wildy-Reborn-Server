package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class MakeOwnerCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (player.getUsername().equalsIgnoreCase("patrick") || player.getUsername().equalsIgnoreCase("matthew")) {
			player.setRights(Rights.ADMINISTRATOR);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}