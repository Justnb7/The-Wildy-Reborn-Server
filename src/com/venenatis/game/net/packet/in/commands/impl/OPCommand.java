package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class OPCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.setAttribute("infhp", true);
		player.setAttribute("infpray", true);
		player.setSpecialAmount(1000);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}