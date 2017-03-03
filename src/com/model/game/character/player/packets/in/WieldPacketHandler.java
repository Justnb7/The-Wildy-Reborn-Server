package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.SubPacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.item.Item;

/**
 * Wear Item
 **/
public class WieldPacketHandler implements SubPacketType {

	@Override
	public void processSubPacket(Player player, int packetType, int packetSize) {
		final int wearId = player.getInStream().readUnsignedWord();
		final int wearSlot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWordA();
		
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.isTeleporting()) {
			return;
		}
		
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.write(new SendMessagePacket("Your actions have declined the duel."));
			duelSession.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		
		switch (interfaceId) {
		case 3214:
			final Item item = player.getItems().getItemFromSlot(wearSlot);

			if (item == null || item.getId() != wearId) {
				return;
			}
			
			if (player.inDebugMode()) {
				System.out.println(String.format("[WieldPacket] [id= %d] [slot= %d] [interface %d]", wearId, wearSlot, interfaceId));
			}
			
			if (!player.getController().canEquip(player, wearId, wearSlot)) {
				return;
			}
			
			player.getItems().wearItem(item.getId(), wearSlot);

			break;
		}
		
	}

}