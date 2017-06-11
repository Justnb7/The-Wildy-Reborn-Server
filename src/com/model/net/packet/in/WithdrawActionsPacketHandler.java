package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
import com.model.game.item.container.impl.shop.ShopManager;
import com.model.net.packet.PacketType;


public class WithdrawActionsPacketHandler implements PacketType {

	/**
	 * The first option opcode of an item inside a container interface.
	 */
	public static final int FIRST_ITEM_ACTION_OPCODE = 145;
	/**
	 * The second option opcode of an item inside a container interface.
	 */
	public static final int SECOND_ITEM_ACTION_OPCODE = 117;
	/**
	 * The third option opcode of an item inside a container interface.
	 */
	public static final int THIRD_ITEM_ACTION_OPCODE = 43;
	/**
	 * The fourth option opcode of an item inside a container interface.
	 */
	public static final int FOURTH_ITEM_ACTION_OPCODE = 129;
	/**
	 * The fifth option opcode of an item inside a container interface.
	 */
	public static final int FIFTH_ITEM_ACTION_OPCODE = 135;
	/**
	 * The sixth option opcode of an item inside a container interface.
	 */
	public static final int SIXTH_ITEM_ACTION_OPCODE = 208;

	@Override
	public void handle(Player player, int packet, int size) {
		switch (packet) {
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;

		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;

		case THIRD_ITEM_ACTION_OPCODE:
			thirdAction(player, packet);
			break;

		case FOURTH_ITEM_ACTION_OPCODE:
			fourthAction(player, packet);
			break;

		case FIFTH_ITEM_ACTION_OPCODE:
			fifthAction(player, packet);
			break;

		case SIXTH_ITEM_ACTION_OPCODE:
			sixthAction(player, packet);
			break;
		 
		}
	}

	/**
	 * Handles the event when a player clicks on the first option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 * 
	 * @param packet
	 *            The packet for this action.
	 */
	private void firstAction(Player player, int packet) {

		final int interfaceId = player.getInStream().readUnsignedWordA();
		final int removeSlot = player.getInStream().readUnsignedWordA();
		final int removeId = player.getInStream().readUnsignedWordA();

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - FirstAction - InterfaceId: " + interfaceId + " (" + removeId + ", " + removeSlot + ")");
		}

		switch (interfaceId) {

		case InterfaceConstants.EQUIPMENT:
			player.getEquipment().unequip(removeSlot);
			break;

		case InterfaceConstants.INVENTORY_STORE: {
			final Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getInterfaceState().isInterfaceOpen(48500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, 1);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, 1);
		}
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			final Item item = player.getInventory().get(removeSlot);

			//System.out.println(item);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);
				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;

		case InterfaceConstants.REMOVE_TRADE_ITEM: {
			final Item item = player.getTradeContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getTradeContainer().getAmount(removeId);

				player.getTradeContainer().removeOffer(item, limit);
			}
		}
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(removeId, removeSlot, 1);
			break;
			
		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, 1);
			break;

		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.getShopValue(player, removeSlot);
			break;

		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.getSellValue(player, removeId);
			break;

		}
	}
	
	/**
	 * Handles the event when a player clicks on the second option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 * @param packet
	 *            The packet for this option.
	 */
	private void secondAction(Player player, int packet) {
		final int interfaceId = player.getInStream().readSignedWordBigEndianA();
		final int removeId = player.getInStream().readSignedWordBigEndianA();
		final int removeSlot = player.getInStream().readSignedWordBigEndian();

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - SecondClick - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);
		}

		switch (interfaceId) {

		case InterfaceConstants.EQUIPMENT: {
			final Item item = player.getEquipment().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (removeId == 2550) {
				player.getActionSender().sendMessage("You can recoil " + (40 - player.getRecoil()) + " more damage.");
				return;
			}
		}
			break;

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(48500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, 5);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 5);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;

		case InterfaceConstants.REMOVE_TRADE_ITEM: {
			Item item = player.getTradeContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getTradeContainer().getAmount(removeId);

				player.getTradeContainer().removeOffer(new Item(removeId, 5), limit);
			}
		}
			break;
			
		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 10, removeSlot);
			break;
			
		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 5, removeSlot);
			break;
			
			
		}
		
	}

	/**
	 * Handles the event when a player clicks on the third option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 *
	 * @param packet
	 *            The packet for this option.
	 */
	private void thirdAction(Player player, int packet) {
		final int interfaceId = player.getInStream().readUnsignedWordBigEndian();
		final int removeId = player.getInStream().readUnsignedWordA();
		final int removeSlot = player.getInStream().readUnsignedWordA();

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - ThirdClick - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);
		}

		switch (interfaceId) {

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(48500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, 10);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, 10);
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(removeId, removeSlot, 10);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, 10);
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 10);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;

		case InterfaceConstants.REMOVE_TRADE_ITEM: {
			Item item = player.getTradeContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getTradeContainer().getAmount(removeId);

				player.getTradeContainer().removeOffer(new Item(removeId, 10), limit);
			}
		}
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, 10);
			break;
			
		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 100, removeSlot);
			break;
			
		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 10, removeSlot);
			break;

		}
	}

	/**
	 * Handles the event when a player clicks on the fourth option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 */
	private void fourthAction(Player player, int packet) {
		final int removeSlot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWord();
		final int removeId = player.getInStream().readUnsignedWordA();

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - FourthAction - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);
		}

		switch (interfaceId) {

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(48500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, Integer.MAX_VALUE);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, limit);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;

		case -32515: {
			Item item = player.getTradeContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getTradeContainer().getAmount(removeId);
				player.getTradeContainer().removeOffer(new Item(removeId, limit), limit);
			}
		}
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		}
	}

	/**
	 * Handles the event when a player clicks on the fifth option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 *
	 * @param packet
	 *            The packet for this option.
	 */
	private void fifthAction(Player player, int packet) {
		player.xRemoveSlot = player.getInStream().readSignedWordBigEndian();
		player.xInterfaceId = player.getInStream().readUnsignedWordA();
		player.xRemoveId = player.getInStream().readSignedWordBigEndian();

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - FifthOption - InterfaceId: " + player.xInterfaceId + " removeId: " + player.xRemoveId + " slot: " + player.xRemoveSlot);
		}

		player.getOutStream().writeFrame(27);
		player.flushOutStream();
	}

	/**
	 * Handles the event when a player clicks on the sixth option of an item
	 * container interface.
	 * 
	 * @param player
	 *            The player clicking the option.
	 *
	 * @param packet
	 *            The packet for this option.
	 */
	private void sixthAction(Player player, int packet) {
		int amountX = player.getInStream().readDWord();;

		if (amountX == 0) {
			amountX = 1;
		}

		if (player.inDebugMode() && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("[WithdrawActionsPacketHandler] - Sixth Option - InterfaceId: " + player.xInterfaceId + " removeId: " + player.xRemoveId + " slot: " + player.xRemoveSlot);
		}
		
		if (player.getArea().inWild()) {
			return;
		}
		
		if (player.dialogue().isActive()) {
			if (player.dialogue().input(amountX)) {
				return;
			}
		}

		/*if (player.getInputAmount() != null) {
			player.getInputAmount().input(amountX);
			player.setInputAmount(null);
			return;
		}*/

		switch (player.xInterfaceId) {

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(48500)) {
				player.getPriceChecker().deposit(player.xRemoveId, player.xRemoveSlot, amountX);
				return;
			}
			player.getBank().deposit(player.xRemoveId, player.xRemoveSlot, amountX);
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			Item item = player.getInventory().get(player.xRemoveSlot);

			if (item == null || item.getId() != player.xRemoveId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getInventory().getAmount(player.xRemoveId);
				player.getTradeContainer().offerItem(new Item(player.xRemoveId, amountX), limit);
			}
		}
			break;

		case InterfaceConstants.REMOVE_TRADE_ITEM: {
			Item item = player.getTradeContainer().get(player.xRemoveSlot);

			if (item == null || item.getId() != player.xRemoveId) {
				return;
			}

			if (player.isTrading()) {
				int limit = player.getTradeContainer().getAmount(player.xRemoveId);
				player.getTradeContainer().removeOffer(new Item(player.xRemoveId, amountX), limit);
			}
		}
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(player.xRemoveId, player.xRemoveSlot, amountX);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(player.xRemoveId, player.xRemoveSlot, amountX);
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(player.xRemoveId, player.xRemoveSlot, amountX);
			break;
		}
	}

}