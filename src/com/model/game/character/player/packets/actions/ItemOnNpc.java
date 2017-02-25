package com.model.game.character.player.packets.actions;

import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class ItemOnNpc implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int itemId = player.getInStream().readSignedWordA();
		int i = player.getInStream().readSignedWordA();
		int slot = player.getInStream().readSignedWordBigEndian();
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		Npc npc = World.getWorld().getNpcs().get(i);
		if (npc == null) {
			return;
		}
		if (!player.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		@SuppressWarnings("unused")
		int npcId = npc.npcId;
		player.walkingToObject = false;
		switch (itemId) {

		}
	}
}
