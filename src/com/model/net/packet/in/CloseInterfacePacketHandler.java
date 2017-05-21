package com.model.net.packet.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.item.container.impl.Trade;
import com.model.net.packet.PacketType;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Patrick van Elderen
 * 
 */
public class CloseInterfacePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		player.getInterfaceState().interfaceClosed();
		if (player.inDebugMode()) {
			System.out.println("[CloseInterface] - Closed Window");
		}
		
		//Decline trade when closing an interface
		if (Trade.inTrade(player)) {
			Trade.declineTrade(player);
		}
		
		//Decline duel when closing an interface
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.getActionSender().sendMessage("You have declined the duel.");
			duelSession.getOther(player).getActionSender().sendMessage("The challenger has declined the duel.");
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
