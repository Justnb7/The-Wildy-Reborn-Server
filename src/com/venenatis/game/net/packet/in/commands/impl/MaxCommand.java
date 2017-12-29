package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Maxes the player.
 * 
 * @author Lennard
 *
 */
public class MaxCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		for (int id = 0; id < Skills.SKILL_COUNT; id++) {
			player.getSkills().setLevel(id, 99);
			player.getSkills().setExperience(id, player.getSkills().getXPForLevel(99));
			player.getActionSender().sendSkillLevel(id);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.PLAYER, Rights.OWNER });
	}

}