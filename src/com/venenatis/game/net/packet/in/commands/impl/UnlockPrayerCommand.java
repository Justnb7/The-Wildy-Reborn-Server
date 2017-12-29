package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class UnlockPrayerCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		final int type = Integer.parseInt(args[1]);
		if (type == 0) {
			player.setPreserveUnlocked(true);
		} else if (type == 1) {
			player.setRigourUnlocked(true);
		} else if (type == 2) {
			player.setAuguryUnlocked(true);
		}
		player.getActionSender().sendConfig(709, player.isPreserveUnlocked() ? 1 : 0);
		player.getActionSender().sendConfig(711, player.isRigourUnlocked() ? 1 : 0);
		player.getActionSender().sendConfig(713, player.isAuguryUnlocked() ? 1 : 0);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}