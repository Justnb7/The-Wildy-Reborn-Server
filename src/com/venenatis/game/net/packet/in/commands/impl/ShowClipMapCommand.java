package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.pathfinder.clipmap.Region;

public class ShowClipMapCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		for (int x = player.getX() - 4; x < player.getX() + 4; x++)
			for (int y = player.getY() - 4; y < player.getY() + 4; y++)
				if (Region.getClippingMask(x, y, player.getZ()) != 0)
					player.getActionSender().sendGroundItem(new GroundItem(new Item(229, 1), Location.create(x, y, player.getZ()), player));
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}