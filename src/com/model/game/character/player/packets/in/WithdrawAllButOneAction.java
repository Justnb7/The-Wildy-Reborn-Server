package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.bank.BankItem;

public class WithdrawAllButOneAction implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int interfaceId = player.getInStream().readSignedWordBigEndianA();
		int itemId = player.getInStream().readSignedWordBigEndianA();
		player.getInStream().readSignedWordBigEndian();
		switch (interfaceId) {
		case 5382:
			int amount = player.getBank().getCurrentBankTab().getItemAmount(new BankItem(itemId + 1));
			if (amount < 1)
				return;
			if (amount == 1) {
				player.getActionSender().sendMessage("Your bank only contains one of this item.");
				return;
			}
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(itemId, amount - 1);
				return;
			}
			if ((player.getBank().getCurrentBankTab().getItemAmount(new BankItem(itemId + 1)) - 1) > 1)
				player.getItems().removeFromBank(itemId, amount - 1, true);
			break;
		}
	}

}
