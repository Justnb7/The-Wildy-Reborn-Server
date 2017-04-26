package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.shop.Shop;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Patrick van Elderen and original PI creators
 *
 */
public class EnterAmountPacketHandler implements PacketType {
	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
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
		
		if (player.getRunePouchContainer().storeOrWithdrawRunes(player, player.xRemoveId, amount > player.getInventory().amount(player.xRemoveId) ? player.getInventory().amount(player.xRemoveId) : amount, player.xInterfaceId)) {
			return;
		}
		//System.out.println("Interface: "+player.xInterfaceId);
		switch (player.xInterfaceId) {

		case 5064:

			break;
			
		case 41710:
			player.getRunePouchContainer().withdraw(player, player.xRemoveSlot, amount > player.getRunePouchContainer().amount(player.xRemoveId) ? player.getRunePouchContainer().amount(player.xRemoveId) : amount);
			break;
			
		case 5382:
			
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(player.xRemoveId, amount));
			} else {
				Trading.tradeItem(player, player.xRemoveId, amount > player.getInventory().amount(player.xRemoveId) ? player.getInventory().amount(player.xRemoveId) : amount, player.xRemoveSlot);
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
				player.getActionSender().sendMessage("All items in this shop cost 99K coins.");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendPurchasePrice(player, new Item(100));
			break;

		}
	}
}