package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.world.World;

public class ItemOnNpc implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int itemId = player.getInStream().readSignedWordA();
		int i = player.getInStream().readSignedWordA();
		@SuppressWarnings("unused")
		int slot = player.getInStream().readSignedWordBigEndian();

		NPC npc = World.getWorld().getNPCs().get(i);
		if (npc == null) {
			return;
		}
		if (!player.getInventory().contains(itemId)) {
			return;
		}
		player.faceEntity(npc);
		npc.faceEntity(player);
		@SuppressWarnings("unused")
		int npcId = npc.getId();
		switch (itemId) {

		}
	}
}
