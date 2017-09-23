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
		
		//Decline trade when closing an interface
		if (player.isTrading()) {
			player.getTradeSession().declineTrade(true);
		}
		
	}

}
