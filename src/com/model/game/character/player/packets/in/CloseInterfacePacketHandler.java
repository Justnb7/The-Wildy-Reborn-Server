package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Patrick van Elderen
 * 
 */
public class CloseInterfacePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		
		if (player.inDebugMode()) {
			System.out.println("[CloseInterface] - Closed Window");
		}
		
		//Decline trade when closing an interface
		if (Trading.isTrading(player)) {
			Trading.decline(player);
		}
		
		//Decline duel when closing an interface
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Trading.isTrading(player) && duelSession == null) {
			Trading.decline(player);
		}
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.write(new SendMessagePacket("You have declined the duel."));
			duelSession.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		
		//Reset the following variables when closing an interface
		player.openInterface = -1;
		player.setShopping(false);
		player.setTrading(false);
		player.setBanking(false);
		
	}

}
