package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class SlayerTaskCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		for (int i = 0; i < 20; i++) {
			SlayerTaskManagement.vannakaTask(player);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}