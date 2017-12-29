package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class JailCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.MODERATOR, Rights.ADMINISTRATOR, Rights.OWNER });
	}

}