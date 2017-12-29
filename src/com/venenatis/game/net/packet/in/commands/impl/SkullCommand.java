package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class SkullCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		Combat.skull(player, SkullType.SKULL, 300);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}