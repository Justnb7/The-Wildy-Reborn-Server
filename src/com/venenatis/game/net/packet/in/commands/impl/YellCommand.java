package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Handles the yell command.
 * 
 * @author Lennard
 *
 */
public class YellCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		command = command.substring(5);
		if (command.length() == 0)
			return;
		String prefix = "@bla@[Yell] " + getYellImage(player.getRights())
				+ player.getUsername() + ":</col> <col=18626b>";
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null || !p.isActive()) {
				continue;
			}
			p.getActionSender().sendMessage(prefix + Character.toUpperCase(command.charAt(0)) + command.substring(1) + ":yell:");
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

	private String getYellImage(Rights rank) {
		return "<img=" + rank.getCrown() + ">";
	}

}