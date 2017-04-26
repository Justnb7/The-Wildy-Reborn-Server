package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class InputFieldPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int id = player.inStream.readDWord();
		String text = player.inStream.readString();
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("Component; "+id+", input; " + text);
		}
		switch (id) {
		
		case 58063:
			
			break;
	
			default:
				break;
		}
	}

}
