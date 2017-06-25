package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.server.Server;

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
