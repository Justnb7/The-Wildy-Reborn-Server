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
			if (player.isBanking()) {
				player.getBank().getBankSearch().setText(text);
				player.getBank().setLastSearch(System.currentTimeMillis());
				if (text.length() > 2) {
					player.getBank().getBankSearch().updateItems();
					player.getBank().setCurrentBankTab(player.getBank().getBankSearch().getTab());
					player.getBank().resetBank();
					player.getBank().getBankSearch().setSearching(true);
				} else {
					if (player.getBank().getBankSearch().isSearching())
						player.getBank().getBankSearch().reset();
					player.getBank().getBankSearch().setSearching(false);
				}
			}
			break;
	
			default:
				break;
		}
	}

}
