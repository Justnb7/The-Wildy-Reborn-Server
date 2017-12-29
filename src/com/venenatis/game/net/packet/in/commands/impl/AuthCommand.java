package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import mysql.donations.AutoDonations;
import mysql.voting.Voting;

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
public class AuthCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		//TODO
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}