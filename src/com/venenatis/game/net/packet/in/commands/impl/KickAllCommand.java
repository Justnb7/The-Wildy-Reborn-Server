package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.world.World;

public class KickAllCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		try {
        	if (World.getWorld().getActivePlayers() > 10) {
        		player.getActionSender().sendMessage("Are you on the LIVE game? shit nigga dont wanna forcekick everyone");
        		return;
        	} else { 
            	for (Player op : World.getWorld().getPlayers()) {
            		if (op == null) continue;
    				op.logout();
            	}
        	}
        } catch (Exception e) {
            e.printStackTrace();
            player.getActionSender().sendMessage("player must be online.");
        }
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}