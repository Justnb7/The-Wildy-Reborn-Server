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

public class WithdrawActionsPacketHandler implements PacketType {

	/**
	 * Withdraw one opcode.
	 */
	private static final int WITHDRAW_1 = 145;

	/**
	 * Withdraw 5 opcode.
	 */
	private static final int WITHDRAW_5 = 117;

	/**
	 * Withdraw 10 opcode.
	 */
	private static final int WITHDRAW_10 = 43;

	/**
	 * Withdraw all opcode.
	 */
	private static final int WITHDRAW_ALL = 129;

	@Override
	public void handle(Player player, int id, int size) {
		switch (id) {
		case WITHDRAW_1:
			withdrawOneAction(player, id);
			break;
		case WITHDRAW_5:
			break;
		case WITHDRAW_10:
			break;
		case WITHDRAW_ALL:
			break;
		}
	}

	private void withdrawOneAction(Player player, int packetId) {
		int interfaceIndex = player.getInStream().readUnsignedWordA();
		int slot = player.getInStream().readUnsignedWordA();
		int id = player.getInStream().readUnsignedWordA();

		Item item = new Item(id);

		// Safety checks
		if (player.isDead() || player.isTeleporting()) {
			return;
		}

		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[withdrawOneAction] - Item: %s Interface: %d Slot: %d%n", item.toString(), interfaceIndex, slot));
		}
		
		if(player.getRunePouchContainer().storeOrWithdrawRunes(player, id, 1, interfaceIndex)) {
			return;
		}

		switch (interfaceIndex) {
			
		case 7423:
			player.getItems().addToBank(item.getId(), 1, false);
			player.getItems().resetItems(7423);
			break;

		case 1688:
			player.getItems().removeEquipment(item.getId(), slot);
			break;

		case 5064:
			if (player.isBanking()) {
				player.getItems().addToBank(item.getId(), 1, true);
			}/* else {
				RunePouchContainer.store(player, slot, 1);
			}*/
			break;

		case 5382:
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(item.getId(), 1);
				return;
			}
			player.getItems().removeFromBank(item.getId(), 1, true);
			break;

		case 3900:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				player.getActionSender().sendMessage("All items in this shop cost 99K coins.");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendPurchasePrice(player, new Item(item.getId()));
			break;

		case 3823:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				player.getActionSender().sendMessage("Items cannot be sold to this shop.");
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendSellingPrice(player, new Item(item.getId()));
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(item.getId(), 1));
			} else {
				Trading.tradeItem(player, item.getId(), 1, slot);
			}
			break;

		case 3415:
			Trading.takeItem(player, item.getId(), 1, slot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(item.getId(), 1));
			}
			break;
		}
	}

}
