package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

public class UnSkullCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		// TODO Auto-generated method stub
		final String[] args = command.split(" ");
		String toName = args[1];
		for (Player players : World.getWorld().getPlayers()) {
			if (players == null || !toName.equalsIgnoreCase(players.getUsername())) {
				continue;
			}
			player.setSkullType(SkullType.NONE);
			player.setSkullTimer(-1);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			players.getActionSender().sendMessage("You have been unskulled by an owner.");
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {

		return Optional.of(new Rights[] { Rights.OWNER });
	}

}