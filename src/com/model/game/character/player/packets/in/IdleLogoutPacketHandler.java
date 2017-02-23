package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.packets.PacketType;
import com.model.utility.Utility;

public class IdleLogoutPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.underAttackBy > 0 || player.underAttackBy2 > 0 || player.rights == Rights.ADMINISTRATOR) {
			return; // 10/10 we good? ye
		} else {
			player.logout();
			Utility.println(player.getName() + " is idle, kicked.");
		}
	}
}