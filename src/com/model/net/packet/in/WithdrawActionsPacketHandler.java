package com.model.net.packet.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.KillTracker;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.container.impl.InventoryContainer;
import com.model.game.item.container.impl.Bank;
import com.model.game.item.container.impl.Trade;
import com.model.game.shop.Shop;
import com.model.net.packet.PacketType;
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
	
	/**
	 * Withdraw X opcode.
	 */
	private static final int WITHDRAW_X = 135;

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
		case WITHDRAW_X:
			withdrawXAction(player, id);
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

		switch (interfaceIndex) {
		
		case 29880:
			player.getRunePouch().addItem(item.getId(), 1, slot);
			break;
			
		case 29910:
		case 29909:
		case 29908:
			player.getRunePouch().removeItem(item.getId(), 1, slot);
			break;

		case 1688:
            player.getEquipment().unequipItem(slot, true);
            break;
			
		case 5064:
            player.getBank().depositFromInventory(slot, 1);
            break;
            
        case 5382:
            player.getBank().withdraw(slot, 1, true);
            break;

		case 3900:
			Shop.SHOPS.get(player.getOpenShop()).sendPurchasePrice(player, new Item(item.getId()));
			break;

		case 3823:
			if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendSellingPrice(player, new Item(item.getId()));
			break;
			
		case Trade.PLAYER_INVENTORY_INTERFACE:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(item.getId(), 1));
			} else {
				if (slot >= 0 && slot < Trade.SIZE) {
					Trade.offerItem(player, id, slot, 1);
				}
			}
			break;
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				Trade.takeItem(player, id, slot, 1);
			}
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

		Item item = new Item(id);

		// Safety checks
		if (player.isDead() || player.isTeleporting()) {
			return;
		}
		
		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[withdrawFiveAction] - Item: %s Interface: %d Slot: %d%n", item.toString(), interfaceIndex, slot));
		}

		switch (interfaceIndex) {
		
		case 29880:
			player.getRunePouch().addItem(item.getId(), 5, slot);
			break;
			
		case 29910:
		case 29909:
		case 29908:
			player.getRunePouch().removeItem(item.getId(), 5, slot);
			break;

		case 1688:
			switch (item.getId()) {
			case 2572:
				KillTracker.open(player);
				break;
			}
			break;

		case 3900:
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(item.getId(), 1));
			break;

		case 3823:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(item.getId(), 5), slot);
			break;

		case 5064:
            player.getBank().depositFromInventory(slot, 5);
            break;
            
        case 5382:
            player.getBank().withdraw(slot, 5, true);
            break;
			
		case Trade.PLAYER_INVENTORY_INTERFACE:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(item.getId(), 5));
			} else {
				if (slot >= 0 && slot < Trade.SIZE) {
					Trade.offerItem(player, id, slot, 5);
				}
			}
			break;
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				Trade.takeItem(player, id, slot, 5);
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(item.getId(), 5));
			}
			break;

		}
	}

	private void withdrawTenAction(Player player, int packetId) {
		int interfaceIndex = player.getInStream().readUnsignedWordBigEndian();
		int id = player.getInStream().readUnsignedWordA();
		int slot = player.getInStream().readUnsignedWordA();

		Item item = new Item(id);

		// Safety checks
		if (player.isDead() || player.isTeleporting()) {
			return;
		}
		
		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[withdrawTenAction] - Item: %s Interface: %d Slot: %d%n", item.toString(), interfaceIndex, slot));
		}

		switch (interfaceIndex) {
		
		case 29880:
			player.getRunePouch().addItem(item.getId(), 10, slot);
			break;
			
		case 29910:
		case 29909:
		case 29908:
			player.getRunePouch().removeItem(item.getId(), 10, slot);
			break;

		case 1688:
			switch (item.getId()) {
			case 2572:
				KillTracker.open(player);
				break;
			}
			break;

		case 3900:
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(item.getId(), 5));
			break;

		case 3823:
			if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(item.getId(), 5), slot);
			break;

		case 5064:
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				player.getActionSender().sendMessage("You have declined the duel.");
				duelSession.getOther(player).getActionSender().sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			} else {
				player.getBank().depositFromInventory(slot, 10);
			}
			break;

		case 5382:
            player.getBank().withdraw(slot, 10, true);
            break;

		case Trade.PLAYER_INVENTORY_INTERFACE:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(item.getId(), 10));
			} else {
				if (slot >= 0 && slot < Trade.SIZE) {
					Trade.offerItem(player, id, slot, 10);
				}
			}
			break;
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				Trade.takeItem(player, id, slot, 10);
			}
			break;
		
		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(item.getId(), 10));

			}
			break;

		}

	}

	private void withdrawAllAction(Player player, int packetId) {
		int slot = player.getInStream().readUnsignedWordA();
		int interfaceIndex = player.getInStream().readUnsignedWord();
		int id = player.getInStream().readUnsignedWordA();

		int amount = 0;
		
		Item item = new Item(id);

		// Safety checks
		if (player.isDead() || player.isTeleporting()) {
			return;
		}
		
		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[withdrawAllAction] - Item: %s Interface: %d Slot: %d%n", item.toString(), interfaceIndex, slot));
		}
		

		switch (interfaceIndex) {

		case 29880:
			amount = player.getInventory().get(slot).getAmount();
			player.getRunePouch().addItem(item.getId(), amount, slot);
			break;
			
		case 29910:
		case 29909:
		case 29908:
			amount = player.getRunePouch().get(slot).getAmount();
			player.getRunePouch().removeItem(item.getId(), amount, slot);
			break;
		
		case 3900:
			Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(item.getId(), 10));
			break;

		case 3823:
			if (player.getOpenShop().equals("Death Store")) {
				player.getActionSender().sendMessage("You cannot sell items to this store!");
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(item.getId(), 10), slot);
			break;

		case 5064:
            player.getBank().depositFromInventory(slot, player.getInventory().getAmount(player.getInventory().get(slot)));
            break;

        case 5382:
            amount = 0;
            if (player.isWithdrawAsNote()) {
                amount = player.getBank().amount(id);
            } else {
                Item itemWithdrew = new Item(id, 1);
                amount = ItemDefinition.DEFINITIONS[itemWithdrew.getId()].isStackable() ? player.getBank().amount(id) : 28;
            }

            player.getBank().withdraw(slot, amount, true);
            break;
			
		case Trade.PLAYER_INVENTORY_INTERFACE:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(item.getId(), player.getInventory().getAmount(item.getId())));
			} else {
				if (slot >= 0 && slot < Trade.SIZE) {
					Trade.offerItem(player, id, slot, player.getInventory().getAmount(id));
				}
			}
			break;
			
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				Trade.takeItem(player, id, slot, player.getTrade().amount(id));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, slot, new GameItem(item.getId(), Integer.MAX_VALUE));
			}
			break;

		case 7295:

			break;

		}
	}
	
	/**
	 * Handles the withdraw x action.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void withdrawXAction(Player player, int packet) {
		int slot = player.getInStream().readUnsignedWordBigEndian();
		int interfaceId = player.getInStream().readUnsignedWordA();
		int id = player.getInStream().readUnsignedWordBigEndian();
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if(slot >= 0 && slot < InventoryContainer.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			System.out.println("yeep");
			if(slot >= 0 && slot < Bank.SIZE) {
				System.out.println("yeep2");
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Trade.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		}
	}

}
