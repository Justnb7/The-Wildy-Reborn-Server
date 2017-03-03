package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class BankModifiableX implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.getInStream().readUnsignedWordA();
		int component = player.getInStream().readUnsignedWord();
		int item = player.getInStream().readUnsignedWordA();
		int amount = player.getInStream().readDWord();
		if (amount <= 0)
			return;
		switch (component) {
		case 5382:
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(item, amount);
				return;
			}
			player.getItems().removeFromBank(item, amount, true);
			break;
		}
	}

}
