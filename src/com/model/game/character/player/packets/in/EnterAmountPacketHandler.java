package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.Trading;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.shop.Shop;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Graham Edgecombe
 *
 */
public class EnterAmountPacketHandler implements PacketType {
	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		
		int amount = player.getInStream().readDWord();
		if (amount <= 0) {
			amount = 0;
		}
		if (player.getArea().inWild()) {
			return;
		}
		if (player.dialogue().isActive()) {
			if (player.dialogue().input(amount)) {
				return;
			}
		}
		//System.out.println("Interface: "+player.xInterfaceId);
		switch (player.xInterfaceId) {

		case 5064:
			if (!player.getItems().playerHasItem(player.xRemoveId, amount)) {
				return;
			}
			if(player.openInterface == 41700) {
				RunePouchContainer.store(player, player.xRemoveSlot, amount > player.getItems().getItemAmount(player.xRemoveId) ? player.getItems().getItemAmount(player.xRemoveId) : amount);
			} else
			player.getItems().addToBank(player.playerItems[player.xRemoveSlot] - 1, amount, true);
			break;
			
		case 41710:
			RunePouchContainer.withdraw(player, player.xRemoveSlot, amount > player.getRunePouchContainer().amount(player.xRemoveId) ? player.getRunePouchContainer().amount(player.xRemoveId) : amount);
			break;
			
		case 5382:
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(player.getBank().getCurrentBankTab().getItem(player.xRemoveSlot).getId() - 1, amount);
				return;
			}
			player.getItems().removeFromBank(player.getBank().getCurrentBankTab().getItem(player.xRemoveSlot).getId() - 1, amount, true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(player.xRemoveId, amount));
			} else {
				Trading.tradeItem(player, player.xRemoveId, amount > player.getItems().getItemAmount(player.xRemoveId) ? player.getItems().getItemAmount(player.xRemoveId) : amount, player.xRemoveSlot);
			}
			break;

		case 3415:
			Trading.takeItem(player, player.xRemoveId, amount > player.getTradeContainer().amount(player.xRemoveId) ? player.getTradeContainer().amount(player.xRemoveId) : amount, player.xRemoveSlot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, player.xRemoveSlot, new GameItem(player.xRemoveId, amount));
			}
			break;
			
		case 3900:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				player.write(new SendMessagePacket("All items in this shop cost 99K coins."));
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendPurchasePrice(player, new Item(100));
			break;

		}
	}
}