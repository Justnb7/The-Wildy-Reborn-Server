package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
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

		/*
		 * case SECOND_ITEM_ACTION_OPCODE: secondAction(player, packet); break;
		 * 
		 * case THIRD_ITEM_ACTION_OPCODE: thirdAction(player, packet); break;
		 * 
		 * case FOURTH_ITEM_ACTION_OPCODE: fourthAction(player, packet); break;
		 * 
		 * case FIFTH_ITEM_ACTION_OPCODE: fifthAction(player, packet); break;
		 * 
		 * case SIXTH_ITEM_ACTION_OPCODE: sixthAction(player, packet); break;
		 */
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

		if (player.inDebugMode()
				&& player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.getActionSender().sendMessage(
					"[ItemContainerAction] - FirstAction - InterfaceId: "
							+ interfaceId + " (" + removeId + ", " + removeSlot
							+ ")");
		}

		switch (interfaceId) {

		case InterfaceConstants.EQUIPMENT:
			player.getEquipment().unequipItem(removeSlot, true);
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
		}
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			final Item item = player.getInventory().get(removeSlot);

			System.out.println(item);

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

		}
	}

}