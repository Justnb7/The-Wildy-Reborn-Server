package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Transforms the player into an NPC.
 * 
 * @author Lennard
 *
 */
public class TransformCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		final int transformId = Integer.parseInt(args[1]);
		final NPCDefinitions def = NPCDefinitions.get(transformId);

		if (def == null) {
			player.getActionSender().sendMessage("This mob does not exist!");
			return;
		}
		
		if(transformId == -1) {
			player.setPnpc(-1);
			player.setPlayerTransformed(false);
			player.message("You transform back into your original state.");
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}

		player.setPnpc(transformId);
		player.setPlayerTransformed(true);
		player.getActionSender().sendMessage(String.format("You have turned into %s (ID: %s).", def.getName(), transformId));
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}