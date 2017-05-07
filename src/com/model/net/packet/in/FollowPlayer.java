package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

public class FollowPlayer implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int followPlayer = player.getInStream().readUnsignedWordBigEndian();
		Player op = World.getWorld().getPlayers().get(followPlayer);
		if (op == null) {
			return;
		}
		player.getCombat().reset();
		player.setFollowing(op);
	}
}