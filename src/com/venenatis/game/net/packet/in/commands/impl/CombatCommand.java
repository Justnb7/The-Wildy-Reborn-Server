package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Shows the users combat level.
 * 
 * @author Lennard
 *
 */
public class CombatCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.message("[combat_level= " + player.getSkills().getCombatLevel() + "]");
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}