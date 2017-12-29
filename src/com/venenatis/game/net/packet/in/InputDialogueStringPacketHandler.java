package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.Clan;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

public class InputDialogueStringPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		/*long value = player.getInStream().readQWord();
		

		if (value < 0) {
			// prevent invalid packets
			value = 0;
		}

		String stringValue = Utility.longToPlayerName2(value);
		
		if (player.getInputString() != null) {
			player.getInputString().input(stringValue);
			player.setInputString(null);
		}*/
		
		String input = Utility.longToPlayerName(player.getInStream().readLong());
		String formattedInput = input.replaceAll("_", " ");
		
		switch (player.enterXInterfaceId) {

		case 6969:
			if ((formattedInput != null) && (formattedInput.length() > 0) && (player.getClan() == null)) {
				Clan localClan = Server.getClanManager().getClan(formattedInput);
				if (localClan != null)
					localClan.addMember(player);
				else if (input.equalsIgnoreCase(player.getUsername()))
					Server.getClanManager().create(player);
				else {
					player.message(input + " has not created a clan yet.");
				}
			}
			break;

		default:
			System.out.println("[StringInputHandler] " + player + " - " + input);
			break;
		}
		return;
	}

}