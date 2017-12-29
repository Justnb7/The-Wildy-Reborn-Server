package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Removes the NPC transformation and transforms the player to their normal
 * character.
 * 
 * @author Lennard
 *
 */
public class UntransformCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.setPnpc(-1);
		player.getActionSender().sendMessage("You have reset your appearance.");
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}