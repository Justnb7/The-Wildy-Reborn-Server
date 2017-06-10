package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.net.packet.PacketType;
import com.model.server.Server;

public class InputFieldPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		final int component = player.inStream.readDWord();
		final String text = player.inStream.readString();
		
		if (component < 0 || text == null || text.length() < 0) {
			return;
		}

		if (player.getRights() == Rights.ADMINISTRATOR && player.inDebugMode()) {
			player.getActionSender().sendMessage("[InputFieldPacketListener] Component: " + component + " | Text: " + text);
		}
		
		switch (component) {
		
		case 42521:
			Server.getDropManager().search(player, text);
			break;
	
			default:
				break;
		}
	}

}
