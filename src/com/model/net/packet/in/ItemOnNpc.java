package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

public class ItemOnNpc implements PacketType {

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
		@SuppressWarnings("unused")
		int npcId = npc.getId();
		switch (itemId) {

		}
	}
}
