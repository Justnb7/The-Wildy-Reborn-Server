package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Patrick van Elderen
 * 
 */
public class CloseInterfacePacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int id, int size) {
		player.getInterfaceState().interfaceClosed();
		player.debug("[CloseInterface] - Closed Window");
		
		/*if ((Boolean) player.getAttributes().get("banking")) {
			player.getAttributes().put("banking", false);
		}*/
		
		//Decline gambling when closing an interface
		if (player.getGamble().getStage() != null) {
			player.getGamble().decline();
		}
		
		//Decline dueling when closing an interface
		if (player.getAttributes().get("duel_stage") != null) {
			player.getDuelArena().decline();
		}
		
		//Decline trade when closing an interface
		if (player.isTrading()) {
			player.getTradeSession().declineTrade(true);
		}
		
	}

}
