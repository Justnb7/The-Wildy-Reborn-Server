package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.SubPacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * Wear Item
 **/
public class WieldPacketHandler implements SubPacketType {

	@Override
	public void processSubPacket(Player player, int packetType, int packetSize) {
		player.wearId = player.getInStream().readUnsignedWord();
		player.wearSlot = player.getInStream().readUnsignedWordA();
		player.interfaceId = player.getInStream().readUnsignedWordA();
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.teleporting) {
			return;
		}
		if (!player.getItems().playerHasItem(player.wearId, 1, player.wearSlot)) {
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		ItemDefinition def = ItemDefinition.forId(player.wearId);
		if (def == null || def.getEquipmentSlot() == null) {
			player.write(new SendMessagePacket(player.wearId + " is unable to be used, if this is an error please report it!"));
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.write(new SendMessagePacket("Your actions have declined the duel."));
			duelSession.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if ((player.playerIndex > 0 || player.npcIndex > 0) && player.wearId != 4153) {
			Combat.resetCombat(player);
		}
		player.getItems().wearItem(player.wearId, player.wearSlot);
	}

}