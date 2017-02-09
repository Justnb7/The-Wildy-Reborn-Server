package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class FollowPlayer implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int followPlayer = player.getInStream().readUnsignedWordBigEndian();
		if (World.getWorld().getPlayers().get(followPlayer) == null) {
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}
		player.walkingToObject = false;
		player.playerIndex = 0;
		player.npcIndex = 0;
		player.mageFollow = false;
		player.usingBow = false;
		player.usingRangeWeapon = false;
		player.followDistance = 1;
		player.followId = followPlayer;
	}
}