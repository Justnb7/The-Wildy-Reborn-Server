package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import mysql.donations.AutoDonations;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Makes the player perform the given animation.
 * 
 * @author Lennard
 *
 */
public class ClaimCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (!Constants.MYSQL_ENABLED) {
			player.getActionSender().sendMessage("Unable to claim because donating is toggled off by Ajw");
			return;
		}
		if (player.getLastSql().elapsed(5000)) {
			new Thread(new AutoDonations(player)).start();
		} else {
			player.getActionSender().sendMessage("Please wait 5 seconds in between claiming!");
		}
		player.getSqlTimer().reset();
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}