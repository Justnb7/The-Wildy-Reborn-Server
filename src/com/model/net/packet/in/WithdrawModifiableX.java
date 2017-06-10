package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
import com.model.net.packet.PacketType;

/**
 * Custom bank packet desgined by, @author <a href="https://www.rune-server.ee/members/jason/">Jason</a> i believe.
 */
public class WithdrawModifiableX implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		final int slot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWord();
		final int itemId = player.getInStream().readUnsignedWordA();
		final int amount = player.getInStream().readDWord();
		
		switch (interfaceId) {
		
		case InterfaceConstants.WITHDRAW_BANK:
			final Item item = player.getBank().get(slot);
			
			if (item == null || item.getId() != itemId) {
				return;
			}
			
			player.getBank().withdraw(itemId, slot, amount);
			
			break;
		
		}
	}

}
