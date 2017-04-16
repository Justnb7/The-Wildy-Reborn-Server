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
import com.model.game.item.Item;

/**
 * Handles the 'wield' option on items.
 * 
 * @author Patrick van Elderen
 * 
 */
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
			player.getActionSender().sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).getActionSender().sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		
		switch (interfaceId) {
		case 3214: // Inventory
			final Item item = player.getItems().getItemFromSlot(wearSlot);

			if (item == null || item.getId() != wearId) {
				return;
			}
			
			if (player.inDebugMode()) {
				System.out.println(String.format("[WieldPacket] [id= %d] [slot= %d] [interface %d]", wearId, wearSlot, interfaceId));
			}
			if (item.getId() == 5733) {
				// TODO custom stuff for the equip option of rotten potato
			} else {
				// Try equipping the item
				if (!player.getController().canEquip(player, wearId, wearSlot)) {
					return;
				}
				
				player.getEquipment().equipItem(wearSlot);
			}

			break;
		}
		
	}

}