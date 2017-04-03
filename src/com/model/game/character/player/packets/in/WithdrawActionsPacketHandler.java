package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.bank.BankItem;
import com.model.game.shop.Shop;
import com.model.utility.json.definitions.ItemDefinition;

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
			withdrawFiveAction(player, id);
			break;
		case WITHDRAW_10:
			withdrawTenAction(player, id);
			break;
		case WITHDRAW_ALL:
			withdrawAllAction(player, id);
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
			System.out.println(String.format("[withdrawOneAction] - Item: %s Interface: %d Slot: %d%n", item.toString(),
					interfaceIndex, slot));
		}

		if (player.getRunePouchContainer().storeOrWithdrawRunes(player, id, 1, interfaceIndex)) {
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
			}
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

	private void withdrawFiveAction(Player player, int packetId) {
		int interfaceIndex = player.getInStream().readSignedWordBigEndianA();
		int id = player.getInStream().readSignedWordBigEndianA();
		int slot = player.getInStream().readSignedWordBigEndian();

		if (player.getRunePouchContainer().storeOrWithdrawRunes(player, id, 5, interfaceIndex)) {
			return;
		}

		switch (interfaceIndex) {

		case 1688:
			player.getPA().useOperate(id);
			break;

		case 3900:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				Shop.skillBuy(player, id);
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(id, 1));
			break;

		case 3823:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(id, 5), slot);
			break;

		case 5064:
			if (player.isBanking()) {
				player.getItems().addToBank(id, 5, true);
			}
			break;

		case 5382:
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(id, 5);
				return;
			}
			player.getItems().removeFromBank(id, 5, true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(id, 5));
			} else {
				Trading.tradeItem(player, id, 5, slot);
			}
			break;

		case 3415:
			Trading.takeItem(player, id, 5, slot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(id, 5));
			}
			break;

		}
	}

	private void withdrawTenAction(Player player, int packetId) {
		int interfaceIndex = player.getInStream().readUnsignedWordBigEndian();
		int id = player.getInStream().readUnsignedWordA();
		int slot = player.getInStream().readUnsignedWordA();

		if (player.getRunePouchContainer().storeOrWithdrawRunes(player, id, 10, interfaceIndex)) {
			return;
		}

		switch (interfaceIndex) {

		case 1688:
			player.getPA().useOperate(id);
			break;

		case 3900:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
			}
			if (player.getOpenShop().equals("Skillcape Shop")) {
				Shop.skillBuy(player, id);
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(id, 5));// <5?
			break;

		case 3823:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
			}
			if (player.getOpenShop().equals("Skillcape Shop")) {
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(id, 5), slot);
			break;

		case 5064:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
				return;
			}
			if (player.isBanking()) {
				player.getItems().addToBank(id, 10, true);
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
					MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession)
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				player.getActionSender().sendMessage("You have declined the duel.");
				duelSession.getOther(player).getActionSender().sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			break;

		case 5382:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
			}
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(id, 10);
				return;
			}
			player.getItems().removeFromBank(id, 10, true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(id, 10));
			} else {
				Trading.tradeItem(player, id, 10, slot);
			}
			break;

		case 3415:
			Trading.takeItem(player, id, 10, slot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(id, 10));

			}
			break;

		}

	}

	private void withdrawAllAction(Player player, int packetId) {
		int slot = player.getInStream().readUnsignedWordA();
		int interfaceIndex = player.getInStream().readUnsignedWord();
		int id = player.getInStream().readUnsignedWordA();

		if (player.getRunePouchContainer().storeOrWithdrawRunes(player, id, player.getRunePouchContainer().amount(id),
				interfaceIndex)) {
			return;
		}
		switch (interfaceIndex) {

		case 3900:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
			}
			if (player.getOpenShop().equals("Skillcape Shop")) {
				Shop.skillBuy(player, id);
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(id, 10));
			break;

		case 3823:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
			}
			if (player.getOpenShop().equals("Skillcape Shop")) {
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(id, 10), slot);
			break;

		case 5064:
			if (Trading.isTrading(player)) {
				Trading.decline(player);
				return;
			}
			if (player.isBanking()) {
				player.getItems().addToBank(id, player.getItems().getItemAmount(id), true);
			}
			break;

		case 5382:
			if (!player.isBanking()) {
				return;
			}
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(id,
						player.getBank().getCurrentBankTab().getItemAmount(new BankItem(id + 1)));
				return;
			}
			player.getItems().removeFromBank(id,
					player.getBank().getCurrentBankTab().getItemAmount(new BankItem(id + 1)), true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(id, player.getItems().getItemAmount(id)));
			} else {
				if (ItemDefinition.forId(id).isStackable()) {
					Trading.tradeItem(player, id, player.itemAmount[slot], slot);
				} else {
					Trading.tradeItem(player, id, 28, slot);
				}
			}
			break;

		case 3415:
			if (ItemDefinition.forId(id).isStackable()) {
				for (Item itemId : player.getTradeContainer().container()) {
					if (itemId != null && itemId.id == id) {
						Trading.takeItem(player, itemId.id, player.getTradeContainer().get(slot).getAmount(), slot);
					}
				}
			} else {
				Trading.takeItem(player, id, 28, slot);

			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(id, Integer.MAX_VALUE));
			}
			break;

		case 7295:
			if (ItemDefinition.forId(id).isStackable()) {
				player.getItems().addToBank(player.playerInventory[slot], player.itemAmount[slot], false);
				player.getItems().resetItems(7423);
			} else {
				player.getItems().addToBank(player.playerInventory[slot],
						player.getItems().itemAmount(player.playerInventory[slot]), false);
				player.getItems().resetItems(7423);
			}
			break;

		}
	}

}
