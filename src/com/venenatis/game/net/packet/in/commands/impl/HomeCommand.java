package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class HomeCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.getTeleportAction().teleport(Constants.RESPAWN_PLAYER_LOCATION, TeleportTypes.SPELL_BOOK, false);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}