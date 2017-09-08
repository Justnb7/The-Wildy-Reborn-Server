package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.util.Utility;

public class IdleLogoutPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		/*if (Combat.incombat(player) || player.getRights().isOwner(player)) {
			return;
		} else {
			player.logout();
			Utility.println(player.getUsername() + " is idle, kicked.");
		}*/
	}
}