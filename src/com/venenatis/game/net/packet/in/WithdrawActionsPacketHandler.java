package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.gamble.Gamble.GambleStage;
import com.venenatis.game.content.skills.smithing.SmithingTask;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.combat.combat_effects.DragonfireShield;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.container.impl.rune_pouch.RunePouchContainer;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.world.shop.ShopManager;


public class WithdrawActionsPacketHandler implements IncomingPacketListener {

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
		
		case 27013:
			player.getPetInsurance().reclaimPet(removeId);
			break;
		
		case 1119:// Smithing
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			SmithingTask.start(player, removeId, 1, interfaceId, removeSlot);
			break;
		
		case RunePouchContainer.INVNTORY_CONTAINER:
			player.getRunePouch().addItem(removeId, 1, removeSlot);
			break;

		case RunePouchContainer.RUNE_POUCH_CONTAINER:
			player.getRunePouch().removeItem(removeId, 1, removeSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 1:
			player.getRunePouch().removeItem(removeId, 1, 1);
			break;
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 2:
			player.getRunePouch().removeItem(removeId, 1, 2);
			break;

		case InterfaceConstants.EQUIPMENT:
			player.getEquipment().unequip(removeSlot);
			break;

		case InterfaceConstants.INVENTORY_STORE: {
			final Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}

			if (player.getInterfaceState().isInterfaceOpen(49500)) {
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
			
			if (player.getGamble().getStage() == GambleStage.OFFERING_STAGE) {
				player.getGamble().offerItem(removeId, 1, removeSlot);
			} else if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().offerItem(removeId, removeSlot, 1);
			} else if (player.isTrading()) {
				player.getTradeContainer().offerItem(new Item(removeId), 1);
			}
		}
			break;
			
		case 48224:
			if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().removeItem(removeId, 1, removeSlot);
			}
			break;

		case 56008:
			player.getGamble().removeItem(removeId, 1, removeSlot);
			break;
			
		case 23013:
			player.getPetInsurance().reclaimPet(removeId);
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

		case 49542:
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
		
		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			SmithingTask.start(player, removeId, 5, component, removeSlot);
			break;
		
		case 23016:
			ShopManager.buy(player, removeId, 1, removeSlot);
			break;
		
		case RunePouchContainer.INVNTORY_CONTAINER:
			player.getRunePouch().addItem(removeId, 5, removeSlot);
			break;

		case RunePouchContainer.RUNE_POUCH_CONTAINER:
			player.getRunePouch().removeItem(removeId, 5, removeSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 1:
			player.getRunePouch().removeItem(removeId, 5, 1);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 2:
			player.getRunePouch().removeItem(removeId, 5, 2);
			break;

		case InterfaceConstants.EQUIPMENT: {
			final Item item = player.getEquipment().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}
			
			player.debug("equipment slot: "+removeSlot);
			
			Item equipment = player.getEquipment().get(removeSlot);
			
			switch(removeSlot) {
			
			case 0:// Helmet
				if (equipment.getId() == 11864 && equipment != null) {
					player.getActionSender().sendMessage("You have to slay "+player.getSlayerTaskAmount()+" more "+player.getSlayerTask()+".");
				}
				break;
			
			case 3:// Weapon
				if (equipment.getId() == 12926 && equipment != null) {
					int charges = player.getToxicBlowpipeCharge();

					if (charges < 0) {
						charges = 0;
					}

					player.getActionSender().sendMessage("Your blowpipe has " + charges + " remaining charges.");
				}
				break;
			
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
				if(equipment.getId() == 2550 && equipment != null) {
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
			if (player.getInterfaceState().isInterfaceOpen(49500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, 5);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, 5);
			break;

		case -15994:
			player.getPriceChecker().withdraw(removeId, removeSlot, 5);
			break;

		case InterfaceConstants.REMOVE_INVENTORY_ITEM: {
			Item item = player.getInventory().get(removeSlot);

			if (item == null || item.getId() != removeId) {
				return;
			}
			
			if (player.getGamble().getStage() == GambleStage.OFFERING_STAGE) {
				player.getGamble().offerItem(removeId, 5, removeSlot);
			} else if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().offerItem(removeId, removeSlot, 5);
			} else if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 5);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;
			
		case -17312:
			if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().removeItem(removeId, 5, removeSlot);
			}
			break;

		case -9528:
			player.getGamble().removeItem(removeId, 5, removeSlot);
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
		
		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			SmithingTask.start(player, removeId, 10, interfaceId, removeSlot);
			break;

		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 10, removeSlot);
			break;

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(49500)) {
				player.getPriceChecker().deposit(removeId, removeSlot, 10);
				return;
			}
			player.getBank().deposit(removeId, removeSlot, 10);
			break;

		case 49542:
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
			
			if (player.getGamble().getStage() == GambleStage.OFFERING_STAGE) {
				player.getGamble().offerItem(removeId, 10, removeSlot);
			} else if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().offerItem(removeId, removeSlot, 10);
			} else if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, 10);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;
			
		case 48224:
			if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().removeItem(removeId, 10, removeSlot);
			}
			break;

		case 56008:
			player.getGamble().removeItem(removeId, 10, removeSlot);
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

		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, removeId, 5, removeSlot);
			break;
			
		case RunePouchContainer.INVNTORY_CONTAINER:
			player.getRunePouch().addItem(removeId, 10, removeSlot);
			break;

		case RunePouchContainer.RUNE_POUCH_CONTAINER:
			player.getRunePouch().removeItem(removeId, 10, removeSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 1:
			player.getRunePouch().removeItem(removeId, 10, 1);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 2:
			player.getRunePouch().removeItem(removeId, 10, 2);
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
		final int interfaceId = player.getInStream().readUnsignedShort();
		final int removeId = player.getInStream().readUnsignedWordA();

		player.debug("[WithdrawActionsPacketHandler] - FourthAction - InterfaceId: " + interfaceId + " removeId: " + removeId + " slot: " + removeSlot);

		switch (interfaceId) {

		case InterfaceConstants.SHOP_INTERFACE:
			ShopManager.buy(player, removeId, 100, removeSlot);
			break;

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(49500)) {
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
			
			if (player.getGamble().getStage() == GambleStage.OFFERING_STAGE) {
				player.getGamble().offerItem(removeId, player.getInventory().getAmount(removeId), removeSlot);
			} else if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().offerItem(removeId, removeSlot, player.getInventory().getAmount(removeId));
			} else if (player.isTrading()) {
				int limit = player.getInventory().getAmount(removeId);

				item = new Item(removeId, limit);

				player.getTradeContainer().offerItem(item, limit);
			}
		}
			break;
			
		case 48224:
			if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().removeItem(removeId, 10, removeSlot);
			}
			break;
			
		case 56008:
			player.getGamble().removeItem(removeId, Integer.MAX_VALUE, removeSlot);
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

		case 49542:
			player.getPriceChecker().withdraw(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(removeId, removeSlot, Integer.MAX_VALUE);
			break;

		case InterfaceConstants.SHOP_INVENTORY:
			player.getOutStream().writeFrame(27);
			player.flushOutStream();
			break;

		case RunePouchContainer.INVNTORY_CONTAINER:
			int limit = player.getInventory().getAmount(removeId);
			player.getRunePouch().addItem(removeId, limit, removeSlot);
			break;

		case RunePouchContainer.RUNE_POUCH_CONTAINER:
			limit = player.getRunePouch().getAmount(removeId);
			player.getRunePouch().removeItem(removeId, limit, removeSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 1:
			limit = player.getRunePouch().getAmount(removeId);
			player.getRunePouch().removeItem(removeId, limit, 1);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 2:
			limit = player.getRunePouch().getAmount(removeId);
			player.getRunePouch().removeItem(removeId, limit, 2);
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
		int amountX = player.getInStream().readDWord();

		if (amountX == 0) {
			amountX = 1;
		}

		player.debug("[WithdrawActionsPacketHandler] - Sixth Option - InterfaceId: " + player.xInterfaceId + " removeId: " + player.xRemoveId + " slot: " + player.xRemoveSlot);
		
		if (BoundaryManager.isWithinBoundary(player.getLocation(), "PvP Zone")) {
			return;
		}

		if (player.getInputAmount() != null) {
			player.getInputAmount().input(amountX);
			player.setInputAmount(null);
			return;
		}
		switch (player.xInterfaceId) {
		
		case RunePouchContainer.INVNTORY_CONTAINER:
			if (player.getInterfaceState().isInterfaceOpen(RunePouchContainer.INTERFACE)) {
				player.getRunePouch().addItem(player.xRemoveId, amountX, player.xRemoveSlot);
				return;
			}
			player.getRunePouch().addItem(player.xRemoveId, amountX, player.xRemoveSlot);
			break;

		case RunePouchContainer.RUNE_POUCH_CONTAINER:
			if (player.getInterfaceState().isInterfaceOpen(RunePouchContainer.INTERFACE)) {
				player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
				return;
			}
			player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 1:
			if (player.getInterfaceState().isInterfaceOpen(RunePouchContainer.INTERFACE)) {
				player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
				return;
			}
			player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
			break;
			
		case RunePouchContainer.RUNE_POUCH_CONTAINER + 2:
			if (player.getInterfaceState().isInterfaceOpen(RunePouchContainer.INTERFACE)) {
				player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
				return;
			}
			player.getRunePouch().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
			break;

		case InterfaceConstants.INVENTORY_STORE:
			if (player.getInterfaceState().isInterfaceOpen(49500)) {
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
			
			if (player.getGamble().getStage() == GambleStage.OFFERING_STAGE) {
				player.getGamble().offerItem(player.xRemoveId, amountX, player.xRemoveSlot);
			} else if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().offerItem(player.xRemoveId, player.xRemoveSlot, amountX);
			} else if (player.isTrading()) {
				int limit = player.getInventory().getAmount(player.xRemoveId);
				player.getTradeContainer().offerItem(new Item(player.xRemoveId, amountX), limit);
			}
		}
			break;
			
		case 48224:
			if (player.getAttributes().get("duel_stage") != null) {
				player.getDuelArena().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
			}
			break;

		case 56008:
			player.getGamble().removeItem(player.xRemoveId, amountX, player.xRemoveSlot);
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

		case 49542:
			player.getPriceChecker().withdraw(player.xRemoveId, player.xRemoveSlot, amountX);
			break;

		case InterfaceConstants.WITHDRAW_BANK:
			player.getBank().withdraw(player.xRemoveId, player.xRemoveSlot, amountX);
			break;

		case InterfaceConstants.DEPOSIT_BOX:
			player.getBank().deposit(player.xRemoveId, player.xRemoveSlot, amountX);
			break;
			
		case InterfaceConstants.SHOP_INVENTORY:
			ShopManager.sell(player, player.xRemoveId, amountX, player.xRemoveSlot);
			break;
		}
	}

}