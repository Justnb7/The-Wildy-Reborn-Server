package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class YellColorCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (player.getTotalAmountDonated() >= 10 || player.getRights().isOwner(player)) {
			final String[] args = command.split(" ");
			int color = Integer.valueOf(args[1]);
				String yellColor = ""+color;
				player.setYellColor(yellColor);
				player.getActionSender().sendMessage("You've changed your yell color to the following:"+yellColor+" <col="+yellColor+">yellColor.");
		} else {
			player.getActionSender().sendMessage("This is a donator feature only.");
			return;
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}