package com.venenatis.game.net.packet.in;

import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.combat_effects.DragonfireShield;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.world.shop.ShopManager;


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

		player.debug("[WithdrawActionsPacketHandler] - FirstAction - InterfaceId: " + interfaceId + " (" + removeId + ", " + removeSlot + ")");

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

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				player.getTradeContainer().offerItem(new Item(removeId), 1);
			} else if (player.getDuelArena().canOffer()) {
				player.getDuelContainer().offerItem(new Item(removeId), 1);
			}
		}
			break;

		case InterfaceConstants.REMOVE_TRADE_ITEM: {
			final Item item = player.getTradeContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.isTrading()) {
				player.getTradeContainer().removeOffer(new Item(removeId), 1);
			}
		}
			break;

		case InterfaceConstants.PLAYER_STAKE_CONTAINER: {
			final Item item = player.getDuelContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getDuelArena().canOffer()) {
				player.getDuelContainer().removeOffer(new Item(removeId), 1);
			}
		}
			break;

		case InterfaceConstants.PRICE_CHECKER:
			player.getPriceChecker().withdraw(removeId, removeSlot, 1);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, 1);
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, 1);
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
		final int component = player.getInStream().readSignedWordBigEndianA();
		final int removeId = player.getInStream().readSignedWordBigEndianA();
		final int removeSlot = player.getInStream().readSignedWordBigEndian();

		player.debug(String.format("Packet 117: component %d item %d slot %d%n", component, removeId, removeSlot));

		switch (component) {

		case InterfaceConstants.EQUIPMENT: {
			final Item item = player.getEquipment().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}
			
			//player.debug(""+removeSlot);
			
			Item equipment = player.getEquipment().get(removeSlot);
			
			switch(removeSlot) {
			case 5:// Shield
				if (equipment != null) {
					switch (equipment.getId()) {
						case 11283:
						case 11284:
							DragonfireShield.dfsSpec(player, player.getCombatState().getTarget());
							break;
					}
				}
				break;
			case 12:// Ring
				if(equipment.getId() == 2550) {
					player.getActionSender().sendMessage("<col=7f00ff>Your Ring of Recoil can deal " + player.getCombatState().getRingOfRecoil() + " more points of damage before shattering.");
				}
				break;
			
			}
		}

		//case InterfaceConstants.SHOP_INTERFACE:
		case -25485:
			ShopManager.buy(player, removeId, 1, removeSlot);
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
			} else if (player.getDuelArena().canOffer()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 5);

				player.getDuelContainer().offerItem(item, limit);
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

		case InterfaceConstants.PLAYER_STAKE_CONTAINER: {
			Item item = player.getDuelContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getDuelArena().canOffer()) {
				int limit = player.getDuelContainer().getAmount(removeId);
				player.getDuelContainer().removeOffer(new Item(removeId, 5), limit);
			}
		}
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 1, removeSlot);
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

		player.debug("[WithdrawActionsPacketHandler] - ThirdClick - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);

		switch (interfaceId) {

		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 10, removeSlot);
			break;

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
			} else if (player.getDuelArena().canOffer()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 10);

				player.getDuelContainer().offerItem(item, limit);
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

		case InterfaceConstants.PLAYER_STAKE_CONTAINER: {
			Item item = player.getDuelContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getDuelArena().canOffer()) {
				int limit = player.getDuelContainer().getAmount(removeId);
				player.getDuelContainer().removeOffer(new Item(removeId, 10), limit);
			}
		}
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, 10);
			break;

		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 5, removeSlot);
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

		player.debug("[WithdrawActionsPacketHandler] - FourthAction - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);

		switch (interfaceId) {

		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 100, removeSlot);
			break;

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
			} else if (player.getDuelArena().canOffer()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, limit);

				player.getDuelContainer().offerItem(item, limit);
			}
		}
			break;

		case 33021: {
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

		case InterfaceConstants.PLAYER_STAKE_CONTAINER: {
			Item item = player.getDuelContainer().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getDuelArena().canOffer()) {
				int limit = player.getDuelContainer().getAmount(removeId);
				player.getDuelContainer().removeOffer(new Item(removeId, 100), limit);
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

		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 10, removeSlot);
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

		player.debug("[WithdrawActionsPacketHandler] - FifthOption - InterfaceId: " + player.xInterfaceId + " removeId: " + player.xRemoveId + " slot: " + player.xRemoveSlot);

		if (player.xInterfaceId == InterfaceConstants.SHOP_INTERFACE) {
			ShopManager.buy(player, player.xRemoveId, 500, player.xRemoveSlot);
			return;
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

		player.debug("[WithdrawActionsPacketHandler] - Sixth Option - InterfaceId: " + player.xInterfaceId + " removeId: " + player.xRemoveId + " slot: " + player.xRemoveSlot);
		
		if (Area.inWilderness(player)) {
			return;
		}

		if (player.getInputAmount() != null) {
			player.getInputAmount().input(amountX);
			player.setInputAmount(null);
			return;
		}

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
			} else if (player.getDuelArena().canOffer()) {
				int limit = player.getInventory().getAmount(player.xRemoveId);
				player.getDuelContainer().offerItem(new Item(player.xRemoveId, amountX), limit);
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

		case InterfaceConstants.PLAYER_STAKE_CONTAINER: {
			Item item = player.getDuelContainer().get(player.xRemoveSlot);

			if (item == null || item.getId() != player.xRemoveId) {
				return;
			}

			if (player.getDuelArena().canOffer()) {
				int limit = player.getDuelContainer().getAmount(player.xRemoveId);
				player.getDuelContainer().removeOffer(new Item(player.xRemoveId, amountX), limit);
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