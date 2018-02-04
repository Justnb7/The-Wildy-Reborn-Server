package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.content.Perks.PerkHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class BuyPerk extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		PerkHandler perk2 = new PerkHandler();
		perk2.openBuyInterface(player);	
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}