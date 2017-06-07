package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;
import com.model.server.Server;

public class InputFieldPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int id = player.inStream.readDWord();
		String text = player.inStream.readString();
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("Component; "+id+", input; " + text);
		}
		switch (id) {
		
		case 42521:
			Server.getDropManager().search(player, text);
			break;
		
		case 58063:
			
			break;
	
			default:
				break;
		}
	}

}
