package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class FollowPlayer implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int followPlayer = player.getInStream().readUnsignedWordBigEndian();
		if (World.getWorld().getPlayers().get(followPlayer) == null) {
			return;
		}
		player.npcIndex = 0;
		player.mageFollow = false;
		player.usingBow = false;
		player.followDistance = 1;
		player.followId = followPlayer;
	}
}