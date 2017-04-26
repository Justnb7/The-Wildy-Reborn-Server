package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

/**
 * Custom bank packet desgined by, @author <a href="https://www.rune-server.ee/members/jason/">Jason</a> i believe.
 */
public class BankModifiableX implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		player.getInStream().readUnsignedWordA();
		int component = player.getInStream().readUnsignedWord();
		@SuppressWarnings("unused")
		int item = player.getInStream().readUnsignedWordA();
		int amount = player.getInStream().readDWord();
		if (amount <= 0)
			return;
		switch (component) {
		case 5382:

			break;
		}
	}

}
