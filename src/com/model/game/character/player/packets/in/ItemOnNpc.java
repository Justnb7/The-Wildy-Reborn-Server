package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class ItemOnNpc implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int itemId = player.getInStream().readSignedWordA();
		int i = player.getInStream().readSignedWordA();
		int slot = player.getInStream().readSignedWordBigEndian();

		NPC npc = World.getWorld().getNpcs().get(i);
		if (npc == null) {
			return;
		}
		if (!player.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		@SuppressWarnings("unused")
		int npcId = npc.npcId;
		switch (itemId) {

		}
	}
}
