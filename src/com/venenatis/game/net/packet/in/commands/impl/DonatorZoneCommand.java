package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class DonatorZoneCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if(BoundaryManager.isWithinBoundary(player.getLocation(), "PvP Zone"))
			return;
		if(player.getTotalAmountDonated() >= 10 || player.getRights().isOwner(player)) {
			player.getTeleportAction().teleport(new Location(2518, 3369));
			player.getActionSender().sendMessage("You have teleported to the <img=26> <shad=7832575>donator zone.");
		} else {
			player.getActionSender().sendMessage("Only donators can use this command.");
			return;
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.DONATOR, Rights.SUPER_DONATOR, Rights.ELITE_DONATOR });
	}

}