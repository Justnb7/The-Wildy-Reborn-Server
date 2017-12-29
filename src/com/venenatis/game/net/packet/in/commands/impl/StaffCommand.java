package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

/**
 * Shows online staff members
 * 
 * @author Lennard
 *
 */
public class StaffCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		player.getActionSender().sendInterface(8134);
		player.getActionSender().sendString("@red@Venenatis Staff@bla@", 8144);
		player.getActionSender().sendString("[@red@Owner@bla@] <img=1>Patrick - " + World.getWorld().getOnlineStatus("patrick"), 8145);
		player.getActionSender().sendString("[@red@Owner@bla@] <img=1>Matthew - " + World.getWorld().getOnlineStatus("matthew"), 8146);

		for (int i = 8151; i < 8178; i++) {
			player.getActionSender().sendString("", i);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}