package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

public class WithdrawAllButOneAction implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		final int slot = player.getInStream().readSignedWordBigEndianA();
		final int interfaceId = player.getInStream().readUnsignedWord();
		final int itemId = player.getInStream().readSignedWordBigEndianA();
		
		switch (interfaceId) {
		
		case InterfaceConstants.WITHDRAW_BANK:
			final Item item = player.getBank().get(slot);
			
			if (item == null || item.getId() != itemId || item.getAmount() <= 0) {
				return;
			}
			
			player.getBank().withdraw(itemId, slot, item.getAmount() - 1);
			
			break;
		
		}
	}

}
