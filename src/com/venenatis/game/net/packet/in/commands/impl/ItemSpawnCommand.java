package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Objects;
import java.util.Optional;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Spawns an in-game item.
 * 
 * @author Lennard
 *
 */
public class ItemSpawnCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (command.length() < 5) {
			player.message("Player use ::item (itemid) (amount)");
			return;
		}
		String[] args = command.split(" ");
		Item item = null;
		if (args.length == 2) {
			item = new Item(Integer.valueOf(args[1]), 1);
		} else if (args.length == 3) {
			item = new Item(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
		} else {
			player.message("[Unable to parse= '" + args[1] + "' and or '" + args[2] + "]");
		}
		if (!Objects.isNull(item)) {
			player.getInventory().add(item);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.PLAYER, Rights.OWNER });
	}

}