package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.world.World;

public class FollowPlayer implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int followPlayer = player.getInStream().readUnsignedWordBigEndian();
		Player op = World.getWorld().getPlayers().get(followPlayer);
		if (op == null) {
			return;
		}
		player.getCombatState().reset();
		player.setFollowing(op);
	}
}