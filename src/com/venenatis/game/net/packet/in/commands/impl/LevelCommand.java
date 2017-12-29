package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Sets the specified skill to the specified level.
 * 
 * @author Lennard
 *
 */
public class LevelCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		int skillId = Integer.parseInt(args[1]);
		int level = Integer.parseInt(args[2]);
		player.getSkills().setLevel(skillId, level);
		player.getSkills().setExperience(skillId, player.getSkills().getExperience(level + 1));
		player.getActionSender().sendSkillLevel(skillId);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}