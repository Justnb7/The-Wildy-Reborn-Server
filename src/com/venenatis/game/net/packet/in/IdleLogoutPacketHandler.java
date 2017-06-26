package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.util.Utility;

public class IdleLogoutPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		if (Combat.incombat(player) || player.rights == Rights.ADMINISTRATOR) {
			return;
		} else {
			player.logout();
			Utility.println(player.getName() + " is idle, kicked.");
		}
	}
}