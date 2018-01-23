package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.content.Perks.BuyPerkHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class BuyPerk extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		BuyPerkHandler perk2 = new BuyPerkHandler();
		perk2.openBuyInterface(player);	
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}