package com.model.net.packet.in;

import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.net.packet.PacketType;
import com.model.utility.Utility;

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