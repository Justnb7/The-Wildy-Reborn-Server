package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * Custom bank packet desgined by, @author <a href="https://www.rune-server.ee/members/jason/">Jason</a> i believe.
 */
public class WithdrawModifiableX implements IncomingPacketListener {

	@Override
	public void handle(Player player, int id, int size) {
		final int slot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedShort();
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
