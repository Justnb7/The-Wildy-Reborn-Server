package com.model.game.item;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.account.Account;
import com.model.game.character.player.content.bounty_hunter.BountyHunterEmblem;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.item.bank.BankItem;
import com.model.game.item.bank.BankTab;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.shop.Currency;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;
import com.model.utility.logging.PlayerLogging;
import com.model.utility.logging.PlayerLogging.LogType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemAssistant {

	private Player player;

	public ItemAssistant(Player client) {
		this.player = client;
	}

	public void updateInventory() {
		this.resetItems(3214);
	}

	/**
	 * Adds an item to the bank without checking if the player has it in there
	 * inventory
	 *
	 * @param itemId
	 *            the id of the item were banking
	 * @param amount
	 *            amount of items to bank
	 */
	public void addItemToBank(int itemId, int amount, boolean refresh) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return;
		}
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(
				player.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemId)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							player.getBank().setCurrentBankTab(tab);
							resetBank();
						}
					} else {
						if (isNotable(itemId) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemId, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								player.getBank().setCurrentBankTab(tab);
								resetBank();
							}
						}
					}
				}
			}
		}
		if (isNotable(itemId))
			item = new BankItem(itemId, amount);
		if (tab.freeSlots(player) == 0) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item
				.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			// ItemHandler.createGroundItem(c, itemId, c.absX, c.absY,
			// c.heightLevel, amount);
			return;
		}
		tab.add(item);
		if (refresh) {
			resetTempItems();
		}
		if (player.isBanking())
			resetBank();
		if (refresh) {
			// c.sendMessage(getItemName(itemId) + " x" + item.getAmount() +
			// " has been added to your bank.");
		}
	}

	public void addItemToBank(int itemId, int amount) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return;
		}
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(
				player.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemId)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							player.getBank().setCurrentBankTab(tab);
							resetBank();
						}
					} else {
						if (isNotable(itemId) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemId, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								player.getBank().setCurrentBankTab(tab);
								resetBank();
							}
						}
					}
				}
			}
		}
		if (isNotable(itemId))
			item = new BankItem(itemId, amount);
		if (tab.freeSlots(player) == 0) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item
				.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			// ItemHandler.createGroundItem(c, itemId, c.absX, c.absY,
			// c.heightLevel, amount);
			return;
		}
		tab.add(item);
		resetTempItems();
		if (player.isBanking())
			resetBank();
		// c.sendMessage(getItemName(itemId) + " x" + item.getAmount() +
		// " has been added to your bank.");
	}

	public void addItemToBank(String playerName, int itemId, int amount) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return;
		}
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(
				player.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemId)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							player.getBank().setCurrentBankTab(tab);
							resetBank();
						}
					} else {
						if (isNotable(itemId) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemId, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								player.getBank().setCurrentBankTab(tab);
								resetBank();
							}
						}
					}
				}
			}
		}
		if (isNotable(itemId))
			item = new BankItem(itemId, amount);
		if (tab.freeSlots(player) == 0) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			// temHandler.createGroundItem(c, itemId, c.absX, c.absY,
			// c.heightLevel, amount);
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item
				.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"The item has been dropped on the floor.");
			GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId,
					amount), player.getX(), player.getY(), player.getZ(),
					player));
			// ItemHandler.createGroundItem(c, itemId, c.absX, c.absY,
			// c.heightLevel, amount);
			return;
		}
		tab.add(item);
		resetTempItems();
		if (player.isBanking())
			resetBank();
		// c.sendMessage(getItemName(itemId) + " x" + item.getAmount() +
		// " has been added to your bank.");
	}

	/**
	 * Check if the player has multiple items (at least 1 of) <br>
	 * DOES NOT CHECK AMOUNT
	 * 
	 * @param items
	 * @return
	 */
	public boolean contains(int... items) {
		for (int it : items) {
			if (!playerHasItem(it, 1)) {
				return false;
			}
		}
		return true;
	}

	public int getWornItemSlot(int itemId) {
		for (int i = 0; i < player.playerEquipment.length; i++)
			if (player.playerEquipment[i] == itemId)
				return i;
		return -1;
	}

	public void resetItems(int WriteFrame) {
		// synchronized (c) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarShort(53);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(WriteFrame);
			player.getOutStream().writeShort(player.playerItems.length);
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItemsN[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
				} else {
					player.getOutStream().writeByte(player.playerItemsN[i]);
				}
				player.getOutStream()
						.writeWordBigEndianA(player.playerItems[i]);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
		}
		// }
	}

	public boolean isNotable(int itemId) {
		boolean withdrawAsNote = ItemDefinition.forId(itemId).isNoted();

		if (withdrawAsNote)
			return true;
		/*
		 * for (ItemList list : Server.itemHandler.ItemList) if (list != null)
		 * if (list.itemId == itemId) if
		 * (list.itemDescription.startsWith("Swap this note at any bank"))
		 * return true;
		 */
		return false;
	}

	/**
	 * Item kept on death
	 *
	 * @param keepItem
	 *            Keep item id
	 * @param deleteItem
	 *            Delete item id
	 */
	public void keepItem(int keepItem, boolean deleteItem) {
		int value = 0;
		int item = 0;
		int slotId = 0;
		boolean itemInInventory = false;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] - 1 > 0) {
				if (BountyHunterEmblem.get(player.playerItems[i] - 1) != null) {
					continue;
				}
				int inventoryItemValue = Currency.COINS.calculateCurrency(
						player, player.playerItems[i] - 1);
				if (inventoryItemValue > value && (!player.invSlot[i])) {
					value = inventoryItemValue;
					item = player.playerItems[i] - 1;
					slotId = i;
					itemInInventory = true;
				}
			}
		}
		for (int i1 = 0; i1 < player.playerEquipment.length; i1++) {
			if (player.playerEquipment[i1] > 0) {
				int equipmentItemValue = Currency.COINS.calculateCurrency(
						player, player.playerEquipment[i1]);
				if (equipmentItemValue > value && (!player.equipSlot[i1])) {
					value = equipmentItemValue;
					item = player.playerEquipment[i1];
					slotId = i1;
					itemInInventory = false;
				}
			}
		}
		if (itemInInventory) {
			player.invSlot[slotId] = true;
			if (deleteItem) {
				deleteItem(player.playerItems[slotId] - 1,
						getItemSlot(player.playerItems[slotId] - 1), 1);
			}
		} else {
			player.equipSlot[slotId] = true;
			if (deleteItem) {
				deleteEquipmentSlot(item, slotId);
			}
		}
		player.itemKeptId[keepItem] = item;
	}

	/**
	 * Reset items kept on death
	 */
	public void resetKeepItems() {
		for (int i = 0; i < player.itemKeptId.length; i++) {
			player.itemKeptId[i] = -1;
		}
		for (int i1 = 0; i1 < player.invSlot.length; i1++) {
			player.invSlot[i1] = false;
		}
		for (int i2 = 0; i2 < player.equipSlot.length; i2++) {
			player.equipSlot[i2] = false;
		}
	}

	/**
	 * delete all items
	 */
	public void deleteAllItems() {
		for (int i1 = 0; i1 < player.playerEquipment.length; i1++) {
			deleteEquipmentSlot(player.playerEquipment[i1], i1);
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			deleteItem(player.playerItems[i] - 1,
					getItemSlot(player.playerItems[i] - 1),
					player.playerItemsN[i]);
		}
	}

	public void dropAllItems() {
		try {
			if (player.deathShopEnabled) {
				if (player.deathShop.getContainer().size() > 0)
					player.getActionSender().sendMessage(
							"You have died, so all of your previous death store items have been deleted!");
				player.deathShop.getContainer().clear();
			}

			/**
			 * What you might want to start doing is logging information at this
			 * point.
			 */
			Player killer = ((player.killerId != -1 && player.killerId != player
					.getIndex()) ? World.getWorld().getPlayers()
					.get(player.killerId) : null);

			if (killer != null) {
				if (player.getAccount().getType().equals(Account.IRON_MAN_TYPE)
						|| player.getAccount().getType()
								.equals(Account.ULTIMATE_IRON_MAN_TYPE)
						|| player.getAccount().getType()
								.equals(Account.HARDCORE_IRON_MAN_TYPE)) {
					player.getActionSender().sendMessage("<col=ff0033>You was killed by "
							+ killer.getName()
							+ ", This means you can only loot your untradables.");
					PlayerLogging.write(LogType.IRON_KILLED_PLAYER, player,
							"Killed by " + killer.getName());
				}
			}

			if (Objects.nonNull(killer)) {
				if (killer.getAccount() != null
						&& player.getAccount() != null
						&& !killer
								.getAccount()
								.getType()
								.attackableTypes()
								.contains(player.getAccount().getType().alias())) {
					killer.getActionSender().sendMessage("You do not receive drops from this player.");
					return;
				}
			}

			/*
			 * Handle giving the emblem to the killer
			 */
			if (killer != null && killer.getAttribute("receive_emblem", false)) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(
						BountyHunterEmblem.TIER_1.getItemId()), player
						.getPosition(), killer));
				killer.setAttribute("receive_emblem", false);
			}
			for (int i = 0; i < player.playerItems.length; i++) {

				if (killer != null) {
					if (player.getRunePouchContainer().hasPouch()) {
						List<Item> runes = player.getRunePouchContainer()
								.stream().filter(Objects::nonNull)
								.collect(Collectors.toList());

						player.getRunePouchContainer().clear();
						player.getItems().deleteItem(12791);
						runes.forEach(item -> GroundItemHandler
								.createGroundItem(new GroundItem(new Item(item
										.getId(), item.getAmount()), player
										.getX(), player.getY(), player.getZ(),
										killer)));
					}
					if (isTradeable(player.playerItems[i] - 1)) {
						GroundItemHandler.createGroundItem(new GroundItem(
								new Item(player.playerItems[i] - 1,
										player.playerItemsN[i]), player.getX(),
								player.getY(), player.getZ(), killer));
					} else {
						GroundItemHandler.createGroundItem(new GroundItem(
								new Item(player.playerItems[i] - 1,
										player.playerItemsN[i]), player.getX(),
								player.getY(), player.getZ(), player));
					}
				} else {
					if (player.playerItems[i] - 1 != 995) {
						GroundItem item = new GroundItem(new Item(
								player.playerItems[i] - 1,
								player.playerItemsN[i]), player.getX(),
								player.getY(), player.getZ(), player);
						GroundItemHandler.createGroundItem(item);
					} else {
						GroundItemHandler.createGroundItem(new GroundItem(
								new Item(player.playerItems[i] - 1,
										player.playerItemsN[i]), player.getX(),
								player.getY(), player.getZ(), player));
					}
				}
			}
			for (int e = 0; e < player.playerEquipment.length; e++) {
				if (killer != null) {
					if (isTradeable(player.playerEquipment[e])) {
						GroundItemHandler.createGroundItem(new GroundItem(
								new Item(player.playerEquipment[e],
										player.playerEquipmentN[e]), player
										.getX(), player.getY(), player.getZ(),
								killer));
					} else {
						GroundItemHandler.createGroundItem(new GroundItem(
								new Item(player.playerEquipment[e],
										player.playerEquipmentN[e]), player
										.getX(), player.getY(), player.getZ(),
								player));
					}
				} else {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(
							player.playerEquipment[e],
							player.playerEquipmentN[e]), player.getX(), player
							.getY(), player.getZ(), player));
				}
			}
			GroundItemHandler.createGroundItem(new GroundItem(new Item(526, 1),
					player.getX(), player.getY(), player.getZ(),
					killer != null ? killer : player));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getWealth() {
		LinkedList<Item> all = new LinkedList<>();
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] <= 0)
				continue;
			all.add(new Item(player.playerItems[i] - 1, player.playerItemsN[i]));
		}
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] <= 0)
				continue;
			all.add(new Item(player.playerEquipment[i],
					player.playerEquipmentN[i]));
		}
		int finalamount = player.isSkulled ? 0 : 3;
		if (player.isActivePrayer(Prayers.PROTECT_ITEM))
			finalamount++;
		int amount = finalamount;
		if (amount > 0) {
			all.sort(Collections.reverseOrder((one, two) -> Double.compare(one
					.getDefinition().getGeneralPrice(), two.getDefinition()
					.getGeneralPrice())));
			for (Iterator<Item> it = all.iterator(); it.hasNext();) {
				Item next = it.next();
				if (amount == 0) {
					break;
				}
				if (next.getDefinition().isStackable() && next.getAmount() > 1) {
					next.amount -= finalamount == 0 ? 1 : finalamount;
				} else {
					it.remove();
				}
				amount--;
			}
		}
		long wealth = 0;
		for (Item i : all) {
			wealth += (i.getDefinition().getGeneralPrice() * i.amount);
		}
		return wealth;
	}

	public boolean isTradeable(int itemId) {
		boolean tradable = ItemDefinition.forId(itemId).isTradeable();
		if (tradable)
			return true;

		return false;
	}

	/**
	 * Determines if an item can be added to the players inventory based on some
	 * set of conditions.
	 * 
	 * @param item
	 *            the id of the item being added to the inventory
	 * @param amount
	 *            the amount of the item
	 * @return {@code true} if the item can be added, otherwise {@code false}
	 */
	public boolean isItemAddable(int item, int amount) {
		if (amount < 1) {
			amount = 1;
		}

		if (item <= 0) {
			return false;
		}

		if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && ItemDefinition
				.forId(item).isStackable())
				|| ((freeSlots() > 0) && !ItemDefinition.forId(item)
						.isStackable())) {

			for (int i = 0; i < player.playerItems.length; i++) {
				if ((player.playerItems[i] == (item + 1))
						&& ItemDefinition.forId(item).isStackable()
						&& (player.playerItems[i] > 0)) {
					return true;
				}
			}

			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] <= 0) {
					if ((amount < Integer.MAX_VALUE) && (amount > -1)) {
						if (amount > 1) {
							return true;
						}
					}
					return true;
				}
			}

			return false;
		}
		return false;
	}

	/**
	 * Add Item
	 *
	 * @param item
	 *            item id
	 * @param amount
	 *            the amount
	 * @return return
	 */
	public boolean addItem(int item, int amount) {
		if (amount < 1) {
			amount = 1;
		}
		if (item <= 0) {
			return false;
		}

		if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && ItemDefinition
				.forId(item).isStackable())
				|| ((freeSlots() > 0) && !ItemDefinition.forId(item)
						.isStackable())) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if ((player.playerItems[i] == (item + 1))
						&& ItemDefinition.forId(item).isStackable()
						&& (player.playerItems[i] > 0)) {
					player.playerItems[i] = (item + 1);
					if (((player.playerItemsN[i] + amount) < Constants.MAXITEM_AMOUNT)
							&& ((player.playerItemsN[i] + amount) > -1)) {
						player.playerItemsN[i] += amount;
					} else {
						player.playerItemsN[i] = Constants.MAXITEM_AMOUNT;
					}
					if (player.getOutStream() != null && player != null) {
						player.getOutStream().putFrameVarShort(34);
						int offset = player.getOutStream().offset;
						player.getOutStream().writeShort(3214);
						player.getOutStream().writeByte(i);
						player.getOutStream().writeShort(player.playerItems[i]);
						if (player.playerItemsN[i] > 254) {
							player.getOutStream().writeByte(255);
							player.getOutStream()
									.putInt(player.playerItemsN[i]);
						} else {
							player.getOutStream().writeByte(
									player.playerItemsN[i]);
						}
						player.getOutStream().putFrameSizeShort(offset);
						player.flushOutStream();
					}
					i = 30;
					return true;
				}
			}
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] <= 0) {
					player.playerItems[i] = item + 1;
					if ((amount < Constants.MAXITEM_AMOUNT) && (amount > -1)) {
						player.playerItemsN[i] = 1;
						if (amount > 1) {
							player.getItems().addItem(item, amount - 1);
							return true;
						}
					} else {
						player.playerItemsN[i] = Constants.MAXITEM_AMOUNT;
					}
					resetItems(3214);
					i = 30;
					return true;
				}
			}
			return false;
		} else {
			resetItems(3214);
			player.getActionSender().sendMessage(
					"Not enough space in your inventory.");
			return false;
		}
	}

	public void addItemtoInventory(Item item) {
		addItem(item.getId(), item.getAmount());
	}

	public boolean isWearingItem(int itemID) {
		for (int i = 0; i < 12; i++) {
			if (player.playerEquipment[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	public boolean isWearingItem(Item item) {
		return isWearingItem(item.getId());
	}

	public void addItem(Item item) {
		addItem(item.getId(), item.getAmount());
	}

	public void addItemToBank(Item item) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return;
		}
		addItemToBank(item.getId(), item.getAmount());
	}

	public void resetBonus() {
		Arrays.fill(player.getBonuses(), 0);
	}

	public void writeBonus() {
		int offset = 0;
		String send;
		for (int i = 0; i < player.getBonuses().length; i++) {
			if (player.getBonuses()[i] >= 0) {
				send = Combat.BONUS_NAMES[i] + ": +" + player.getBonuses()[i];
			} else {
				send = Combat.BONUS_NAMES[i] + ": -" + java.lang.Math.abs(player.getBonuses()[i]);
			}

			if (i == 10) {
				offset = 1;
			}
			player.getActionSender().sendString(send, (1675 + i + offset));
		}
	}

	public void getBonus() {
		for (int item : player.playerEquipment) {
			if (item <= 0)
				continue;
			int[] values = ItemDefinition.forId(item).getBonus();
			for (int i = 0; i < values.length; i++)
				player.getBonuses()[i] += values[i];
		}
	}

	/**
	 * two handed weapon check
	 *
	 * @param itemName
	 *            item's name
	 * @param itemId
	 *            the item's id
	 * @return return;
	 */
	public boolean is2handed(String itemName, int itemId) {
		boolean Handed = ItemDefinition.forId(itemId).isTwoHanded();

		if (Handed)
			return true;
		return false;
	}

	/**
	 * Wear Item
	 *
	 * @param id
	 *            the item id
	 * @param slotId
	 *            the slot
	 * @return return;
	 */
	public boolean wearItem(int id, int slotId) {

		Item item = player.getItems().getItemFromSlot(slotId);

		int targetSlot = ItemDefinition.forId(id).getEquipmentSlot();

		if (!ItemConstants.canWear(item, player))
			return false;

		if (!player.getItems().playerHasItem(id)
				|| player.playerItems[slotId] - 1 != id
				|| player.playerItemsN[slotId] <= 0) {
			return false;
		}
		int wearAmount = player.playerItemsN[slotId];
		if (wearAmount < 1) {
			return false;
		}

		if (targetSlot == player.getEquipment().getWeaponId()) {
			if (player.usingBow) {
				player.usingBow = false;
			}
		}
		if (slotId >= 0 && id >= 0) {
			int toEquip = player.playerItems[slotId];
			int toEquipN = player.playerItemsN[slotId];
			int toRemove = player.playerEquipment[targetSlot];
			int toRemoveN = player.playerEquipmentN[targetSlot];
			if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
				DuelSession session = (DuelSession) Server
						.getMultiplayerSessionListener().getMultiplayerSession(
								player, MultiplayerSessionType.DUEL);
				if (!Objects.isNull(session)) {
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getHelmetId()]
							&& session.getRules().contains(Rule.NO_HELM)) {
						player.getActionSender().sendMessage(
								"Wearing helmets has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getAmuletId()]
							&& session.getRules().contains(Rule.NO_AMULET)) {
						player.getActionSender().sendMessage(
								"Wearing amulets has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getQuiverId()]
							&& session.getRules().contains(Rule.NO_ARROWS)) {
						player.getActionSender().sendMessage(
								"Wearing arrows has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getChestId()]
							&& session.getRules().contains(Rule.NO_BODY)) {
						player.getActionSender().sendMessage(
								"Wearing platebodies has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getBootsId()]
							&& session.getRules().contains(Rule.NO_BOOTS)) {
						player.getActionSender().sendMessage(
								"Wearing boots has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getGlovesId()]
							&& session.getRules().contains(Rule.NO_GLOVES)) {
						player.getActionSender().sendMessage(
								"Wearing gloves has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getCapeId()]
							&& session.getRules().contains(Rule.NO_CAPE)) {
						player.getActionSender().sendMessage(
								"Wearing capes has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getLegsId()]
							&& session.getRules().contains(Rule.NO_LEGS)) {
						player.getActionSender().sendMessage(
								"Wearing platelegs has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getRingId()]
							&& session.getRules().contains(Rule.NO_RINGS)) {
						player.getActionSender().sendMessage(
								"Wearing a ring has been disabled for this duel.");
						return false;
					}
					if (targetSlot == player.playerEquipment[player
							.getEquipment().getWeaponId()]
							&& session.getRules().contains(Rule.NO_WEAPON)) {
						player.getActionSender().sendMessage(
								"Wearing weapons has been disabled for this duel.");
						return false;
					}
					if (session.getRules().contains(Rule.NO_SHIELD)) {
						if (targetSlot == player.playerEquipment[player
								.getEquipment().getShieldId()]
								|| targetSlot == player.playerEquipment[player
										.getEquipment().getWeaponId()]
								&& player.getItems().is2handed(
										getItemName(id).toLowerCase(), id)) {
							player.getActionSender().sendMessage(
									"Wearing shields and 2handed weapons has been disabled for this duel.");
							return false;
						}
					}
				}
			}
			if (toEquip == toRemove + 1
					&& ItemDefinition.forId(toRemove).isStackable()) {
				deleteItem(toRemove, getItemSlot(toRemove), toEquipN);
				player.playerEquipmentN[targetSlot] += toEquipN;
			} else if (targetSlot != 5 && targetSlot != 3) {
				player.playerItems[slotId] = toRemove + 1;
				player.playerItemsN[slotId] = toRemoveN;
				player.playerEquipment[targetSlot] = toEquip - 1;
				player.playerEquipmentN[targetSlot] = toEquipN;
			} else if (targetSlot == 5) {
				boolean wearing2h = is2handed(
						getItemName(
								player.playerEquipment[player.getEquipment()
										.getWeaponId()]).toLowerCase(),
						player.playerEquipment[player.getEquipment()
								.getWeaponId()]);
				if (wearing2h) {
					toRemove = player.playerEquipment[player.getEquipment()
							.getWeaponId()];
					toRemoveN = player.playerEquipmentN[player.getEquipment()
							.getWeaponId()];
					player.playerEquipment[player.getEquipment().getWeaponId()] = -1;
					player.playerEquipmentN[player.getEquipment().getWeaponId()] = 0;
					updateSlot(player.getEquipment().getWeaponId());
				}
				player.playerItems[slotId] = toRemove + 1;
				player.playerItemsN[slotId] = toRemoveN;
				player.playerEquipment[targetSlot] = toEquip - 1;
				player.playerEquipmentN[targetSlot] = toEquipN;
			} else if (targetSlot == 3) {
				boolean is2h = is2handed(getItemName(id).toLowerCase(), id);
				boolean wearingShield = player.playerEquipment[player
						.getEquipment().getShieldId()] > 0;
				boolean wearingWeapon = player.playerEquipment[player
						.getEquipment().getWeaponId()] > 0;
				if (is2h) {
					if (wearingShield && wearingWeapon) {
						if (freeSlots() > 0) {
							player.playerItems[slotId] = toRemove + 1;
							player.playerItemsN[slotId] = toRemoveN;
							player.playerEquipment[targetSlot] = toEquip - 1;
							player.playerEquipmentN[targetSlot] = toEquipN;
							removeItem(player.getEquipment().getShieldId());
						} else {
							player.getActionSender().sendMessage(
									"You do not have enough inventory space to do this.");
							return false;
						}
					} else if (wearingShield && !wearingWeapon) {
						player.playerItems[slotId] = player.playerEquipment[player
								.getEquipment().getShieldId()] + 1;
						player.playerItemsN[slotId] = player.playerEquipmentN[player
								.getEquipment().getShieldId()];
						player.playerEquipment[targetSlot] = toEquip - 1;
						player.playerEquipmentN[targetSlot] = toEquipN;
						player.playerEquipment[player.getEquipment()
								.getShieldId()] = -1;
						player.playerEquipmentN[player.getEquipment()
								.getShieldId()] = 0;
						updateSlot(player.getEquipment().getShieldId());
					} else {
						int remove_slot = getItemSlot(toRemove);
						if (toRemove != -1 && remove_slot != -1
								&& ItemDefinition.forId(toRemove).isStackable()) {
							player.playerItems[slotId] = 0;
							player.playerItemsN[slotId] = 0;
							player.playerItemsN[remove_slot] += toRemoveN;
						} else {
							player.playerItems[slotId] = toRemove + 1;
							player.playerItemsN[slotId] = toRemoveN;
						}
						player.playerEquipment[targetSlot] = toEquip - 1;
						player.playerEquipmentN[targetSlot] = toEquipN;
					}
				} else {
					int remove_slot = getItemSlot(toRemove);
					if (toRemove != -1 && remove_slot != -1
							&& ItemDefinition.forId(toRemove).isStackable()) {
						player.playerItems[slotId] = 0;
						player.playerItemsN[slotId] = 0;
						player.playerItemsN[remove_slot] += toRemoveN;
					} else {
						player.playerItems[slotId] = toRemove + 1;
						player.playerItemsN[slotId] = toRemoveN;
					}
					player.playerEquipment[targetSlot] = toEquip - 1;
					player.playerEquipmentN[targetSlot] = toEquipN;
				}
			}
			if (targetSlot == 3) {
				player.setUsingSpecial(false);
				player.getWeaponInterface().sendSpecialBar(id);
			}
			if (player.getOutStream() != null && player != null) {
				player.getOutStream().putFrameVarShort(34);
				int offset = player.getOutStream().offset;
				player.getOutStream().writeShort(1688);
				player.getOutStream().writeByte(targetSlot);
				player.getOutStream().writeShort(id + 1);

				if (player.playerEquipmentN[targetSlot] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().putInt(
							player.playerEquipmentN[targetSlot]);
				} else {
					player.getOutStream().writeByte(
							player.playerEquipmentN[targetSlot]);
				}

				player.getOutStream().putFrameSizeShort(offset);
				player.flushOutStream();
			}

			player.getWeaponInterface()
					.sendWeapon(
							player.playerEquipment[player.getEquipment()
									.getWeaponId()],
							getItemName(player.playerEquipment[player
									.getEquipment().getWeaponId()]));
			resetBonus();
			getBonus();
			player.getPA().resetAutoCast();
			player.autoCast = false;
			writeBonus();
			CombatAnimation.itemAnimations(player);
			player.getPA().requestUpdates();
			player.getCombat().reset();
			return true;
		} else {
			return false;
		}
	}

	public void wearItem(int wearID, int wearAmount, int targetSlot) {
		player.getOutStream().putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(1688);
		player.getOutStream().writeByte(targetSlot);
		player.getOutStream().writeShort(wearID + 1);

		if (wearAmount > 254) {
			player.getOutStream().writeByte(255);
			player.getOutStream().putInt(wearAmount);
		} else {
			player.getOutStream().writeByte(wearAmount);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.playerEquipment[targetSlot] = wearID;
		player.playerEquipmentN[targetSlot] = wearAmount;
		player.getWeaponInterface().sendWeapon(
				player.playerEquipment[player.getEquipment().getWeaponId()],
				getItemName(player.playerEquipment[player.getEquipment()
						.getWeaponId()]));
		player.getItems().resetBonus();
		player.getItems().getBonus();
		player.getPA().resetAutoCast();
		player.getItems().writeBonus();
		CombatAnimation.itemAnimations(player);
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	public void updateSlot(int slot) {
		player.getOutStream().putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(1688);
		player.getOutStream().writeByte(slot);
		player.getOutStream().writeShort(player.playerEquipment[slot] + 1);
		if (player.playerEquipmentN[slot] > 254) {
			player.getOutStream().writeByte(255);
			player.getOutStream().putInt(player.playerEquipmentN[slot]);
		} else {
			player.getOutStream().writeByte(player.playerEquipmentN[slot]);
		}
		player.getOutStream().putFrameSizeShort(offset);

	}

	public void removeItem(int slot) {
		if (player.getOutStream() != null && player != null) {
			if (player.playerEquipment[slot] > -1) {
				if (addItem(player.playerEquipment[slot],
						player.playerEquipmentN[slot])) {
					player.playerEquipment[slot] = -1;
					player.playerEquipmentN[slot] = 0;
					player.getWeaponInterface().sendWeapon(
							player.playerEquipment[player.getEquipment()
									.getWeaponId()],
							getItemName(player.playerEquipment[player
									.getEquipment().getWeaponId()]));
					resetBonus();
					getBonus();
					player.autoCast = false;
					writeBonus();
					CombatAnimation.itemAnimations(player);
					player.getOutStream().writeFrame(34);
					player.getOutStream().writeShort(6);
					player.getOutStream().writeShort(1688);
					player.getOutStream().writeByte(slot);
					player.getOutStream().writeShort(0);
					player.getOutStream().writeByte(0);
					player.flushOutStream();
					player.updateRequired = true;
					player.appearanceUpdateRequired = true;
				}
			}
		}
	}

	public void resetBank() {
		int tabId = player.getBank().getCurrentBankTab().getTabId();
		for (int i = 0; i < player.getBank().getBankTab().length; i++) {
			if (i == 0)
				continue;
			if (i != player.getBank().getBankTab().length - 1
					&& player.getBank().getBankTab()[i].size() == 0
					&& player.getBank().getBankTab()[i + 1].size() > 0) {
				for (BankItem item : player.getBank().getBankTab()[i + 1]
						.getItems()) {
					player.getBank().getBankTab()[i].add(item);
				}
				player.getBank().getBankTab()[i + 1].getItems().clear();
			}
		}
		player.getActionSender().sendConfig(600, 0);
		player.getActionSender().sendUpdateItem(58040, -1, 0, 0);
		int newSlot = -1;
		for (int i = 0; i < player.getBank().getBankTab().length; i++) {
			BankTab tab = player.getBank().getBankTab()[i];
			if (i == tabId) {
				player.getActionSender().sendConfig(600 + i, 1);
			} else {
				player.getActionSender().sendConfig(600 + i, 0);
			}
			if (tab.getTabId() != 0 && tab.size() > 0 && tab.getItem(0) != null) {
				player.getActionSender().sendInterfaceConfig(0, 58050 + i);
				player.getActionSender()
						.sendUpdateItem(
								58040 + i,
								player.getBank().getBankTab()[i].getItem(0)
										.getId() - 1,
								0,
								player.getBank().getBankTab()[i].getItem(0)
										.getAmount());
			} else if (i != 0) {
				if (newSlot == -1) {
					newSlot = i;
					player.getActionSender()
							.sendUpdateItem(58040 + i, -1, 0, 0);
					player.getActionSender().sendInterfaceConfig(0, 58050 + i);
					continue;
				}
				player.getActionSender().sendUpdateItem(58040 + i, -1, 0, 0);
				player.getActionSender().sendInterfaceConfig(1, 58050 + i);
			}
		}
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(5382); // bank
		player.getOutStream().writeShort(player.BANK_SIZE);
		BankTab tab = player.getBank().getCurrentBankTab();
		for (int i = 0; i < player.BANK_SIZE; i++) {
			if (i > tab.size() - 1) {
				player.getOutStream().writeByte(0);
				player.getOutStream().writeWordBigEndianA(0);
				continue;
			} else {
				BankItem item = tab.getItem(i);
				if (item == null)
					item = new BankItem(-1, 0);
				if (item.getAmount() > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					player.getOutStream().writeByte(item.getAmount());
				}
				if (item.getAmount() < 1)
					item.setAmount(0);
				if (item.getId() > 25000 || item.getId() < 0)
					item.setId(-1);
				player.getOutStream().writeWordBigEndianA(item.getId());
			}
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		player.getActionSender().sendString(
				"" + player.getBank().getCurrentBankTab().size(), 58061);
		player.getActionSender().sendString(Integer.toString(tabId), 5292);
	}

	public void resetTempItems() {
		int itemCount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] > -1) {
				itemCount = i;
			}
		}
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(5064);
		player.getOutStream().writeShort(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {
			if (player.playerItemsN[i] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
			} else {
				player.getOutStream().writeByte(player.playerItemsN[i]);
			}
			if (player.playerItems[i] > 25000 || player.playerItems[i] < 0) {
				player.playerItems[i] = 25000;
			}
			player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}

	public boolean addToBank(int itemID, int amount, boolean updateView) {
		if (!player.isBanking())
			return false;
		if (!player.getItems().playerHasItem(itemID))
			return false;
		if (player.getBank().getBankSearch().isSearching()) {
			player.getBank().getBankSearch().reset();
			return false;
		}

		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemID + 1, amount);
		for (BankTab t : player.getBank().getBankTab()) {
			if (t == null || t.size() == 0)
				continue;
			for (BankItem i : t.getItems()) {
				if (i.getId() == item.getId() && !isNotable(itemID)) {
					if (t.getTabId() != tab.getTabId()) {
						tab = t;
						break;
					}
				} else {
					if (isNotable(itemID) && i.getId() == item.getId() - 1) {
						item = new BankItem(itemID, amount);
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							break;
						}
					}
				}
			}
		}
		if (isNotable(itemID)) {
			item = new BankItem(itemID, amount);
		}
		if (item.getAmount() > getItemAmount(itemID))
			item.setAmount(getItemAmount(itemID));
		if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"Your bank is already holding the maximum amount of "
							+ getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots(player) == 0 && !tab.contains(item)) {
			player.getActionSender().sendMessage("Your current bank tab is full.");
			return false;
		} else {
			long totalAmount = ((long) tab.getItemAmount(item) + (long) item
					.getAmount());
			if (totalAmount >= Integer.MAX_VALUE) {
				int difference = Integer.MAX_VALUE - tab.getItemAmount(item);
				item.setAmount(difference);
				deleteItem(itemID, difference);
			} else {
				deleteItem(itemID, item.getAmount());
			}
			tab.add(item);
			if (updateView) {
				resetTempItems();
				resetBank();
			}
			return true;
		}
	}

	public boolean bankContains(int itemId) {

		for (BankTab tab : player.getBank().getBankTab())
			if (tab.contains(new BankItem(itemId + 1)))
				return true;
		return false;
	}

	public void removeFromBank(int itemId, int itemAmount, boolean updateView) {
		System.out.println("enter");
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, itemAmount);
		boolean noted = false;
		if (!player.isBanking()) {
			return;
		}
		if (itemAmount <= 0)
			return;

		if (System.currentTimeMillis() - player.lastBankDeposit < 250)
			return;
		if (!player.isBusy()) {
			System.out.println("block: " + player.isBanking());
			player.getActionSender().sendRemoveInterfacePacket();
			return;
		}
		if (!tab.contains(item))
			return;
		if (player.takeAsNote) {
			if (getItemName(itemId).trim().equalsIgnoreCase(
					getItemName(itemId + 1).trim())
					&& isNotable(itemId + 1)) {
				noted = true;
			} else
				player.getActionSender().sendMessage("This item cannot be taken out as noted.");
		}
		if (freeSlots() == 0 && !playerHasItem(itemId)) {
			player.getActionSender().sendMessage("There is not enough space in your inventory.");
			return;
		}
		if (getItemAmount(itemId) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage("Your inventory is already holding the maximum amount of " + getItemName(itemId).toLowerCase() + " possible.");
			return;
		}
		if (isStackable(item.getId() - 1) || noted) {
			long totalAmount = (long) getItemAmount(itemId) + (long) itemAmount;
			if (totalAmount > Integer.MAX_VALUE)
				item.setAmount(tab.getItemAmount(item) - getItemAmount(itemId));
		}
		if (tab.getItemAmount(item) < itemAmount) {
			item.setAmount(tab.getItemAmount(item));
		}
		if (!isStackable(item.getId() - 1) && !noted) {
			if (freeSlots() < item.getAmount())
				item.setAmount(freeSlots());
		}
		if (item.getAmount() < 0)
			item.setAmount(0);
		if (!noted)
			addItem(item.getId() - 1, item.getAmount());
		else
			addItem(item.getId(), item.getAmount());
		tab.remove(item);
		if (tab.size() == 0) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
		}
		if (updateView) {
			resetBank();
		}
		player.getItems().resetItems(5064);
	}

	public boolean addEquipmentToBank(int itemID, int slot, int amount,
			boolean updateView) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return false;
		}
		if (!player.isBanking())
			return false;
		if (player.playerEquipment[slot] != itemID
				|| player.playerEquipmentN[slot] <= 0)
			return false;
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemID + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(
				player.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemID)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							break;
						}
					} else {
						if (isNotable(itemID) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemID, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								break;
							}
						}
					}
				}
			}
		}
		if (isNotable(itemID))
			item = new BankItem(itemID, amount);
		if (item.getAmount() > player.playerEquipmentN[slot])
			item.setAmount(player.playerEquipmentN[slot]);
		if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"Your bank is already holding the maximum amount of " + getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots(player) == 0 && !tab.contains(item)) {
			player.getActionSender().sendMessage("Your current bank tab is full.");
			return false;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item
				.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.getActionSender().sendMessage(
					"Your bank is already holding the maximum amount of this item.");
			return false;
		} else
			player.playerEquipmentN[slot] -= item.getAmount();
		if (player.playerEquipmentN[slot] <= 0) {
			player.playerEquipmentN[slot] = -1;
			player.playerEquipment[slot] = -1;
		}
		player.getItems().wearItem(-1, 0, slot);
		tab.add(item);
		if (updateView) {
			resetTempItems();
			resetBank();
			updateSlot(slot);
		}
		return true;
	}

	/**
	 * Checking item amounts.
	 * 
	 * @param itemID
	 * @return
	 */
	public int itemAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemID) {
				tempAmount += player.playerItemsN[i];
			}
		}
		return tempAmount;
	}

	public boolean isStackable(int itemId) {
		if (ItemDefinition.forId(itemId) == null) {
			return false;
		}
		return ItemDefinition.forId(itemId).isStackable();
	}

	/**
	 * Update Equip tab
	 *
	 * @param wearID
	 *            the wear id
	 * @param amount
	 *            the amount
	 * @param targetSlot
	 *            the target slot
	 */

	public void setEquipment(int wearID, int amount, int targetSlot) {
		player.getOutStream().putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(1688);
		player.getOutStream().writeByte(targetSlot);
		player.getOutStream().writeShort(wearID + 1);
		if (amount > 254) {
			player.getOutStream().writeByte(255);
			player.getOutStream().putInt(amount);
		} else {
			player.getOutStream().writeByte(amount);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		player.playerEquipment[targetSlot] = wearID;
		player.playerEquipmentN[targetSlot] = amount;
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	/**
	 * Move Items
	 */

	public void swapBankItem(int from, int to) {
		BankItem item = player.getBank().getCurrentBankTab().getItem(from);
		player.getBank()
				.getCurrentBankTab()
				.setItem(from, player.getBank().getCurrentBankTab().getItem(to));
		player.getBank().getCurrentBankTab().setItem(to, item);
	}

	public void swap(int from, int to, int moveWindow, boolean insertMode) {
		if (moveWindow == 3214) {
			int tempI;
			int tempN;
			tempI = player.playerItems[from];
			tempN = player.playerItemsN[from];
			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
		}
		if (moveWindow == 5382) {
			if (!player.isBanking()) {
				player.getActionSender().sendRemoveInterfacePacket();
				resetBank();
				return;
			}
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().reset();
				return;
			}

			if (to > 999) {
				int tabId = to - 1000;
				if (tabId < 0)
					tabId = 0;
				if (tabId == player.getBank().getCurrentBankTab().getTabId()) {
					player.getActionSender().sendMessage("You cannot add an item from it's tab to the same tab.");
					resetBank();
					return;
				}
				if (from >= player.getBank().getCurrentBankTab().size()) {
					resetBank();
					return;
				}
				BankItem item = player.getBank().getCurrentBankTab()
						.getItem(from);
				if (item == null) {
					resetBank();
					return;
				}
				if (player.getBank().getBankTab()[tabId].size() >= player.BANK_SIZE) {
					player.getActionSender().sendMessage("You cannot move anymore items to that tab.");
					resetBank();
					return;
				}
				player.getBank().getCurrentBankTab().remove(item);
				player.getBank().getBankTab()[tabId].add(item);
			} else {
				if (from > player.getBank().getCurrentBankTab().size() - 1
						|| to > player.getBank().getCurrentBankTab().size() - 1) {
					resetBank();
					return;
				}
				if (!insertMode) {
					BankItem item = player.getBank().getCurrentBankTab()
							.getItem(from);
					player.getBank()
							.getCurrentBankTab()
							.setItem(
									from,
									player.getBank().getCurrentBankTab()
											.getItem(to));
					player.getBank().getCurrentBankTab().setItem(to, item);
				} else {
					int tempFrom = from;
					for (int tempTo = to; tempFrom != tempTo;)
						if (tempFrom > tempTo) {
							swapBankItem(tempFrom, tempFrom - 1);
							tempFrom--;
						} else if (tempFrom < tempTo) {
							swapBankItem(tempFrom, tempFrom + 1);
							tempFrom++;
						}
				}
			}
		}
		if (moveWindow == 5382) {
			resetBank();
		}
		if (moveWindow == 5064) {
			int tempI;
			int tempN;
			tempI = player.playerItems[from];
			tempN = player.playerItemsN[from];

			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
			resetItems(3214);
		}
		resetTempItems();
		if (moveWindow == 3214) {
			resetItems(3214);
		}

	}

	/**
	 * delete Item
	 */
	public void deleteEquipmentSlot(int id, int slot) {

		if (World.getWorld().getPlayers().get(player.getIndex()) == null) {
			return;
		}
		if (id < 0) {
			return;
		}
		player.playerEquipment[slot] = -1;
		player.playerEquipmentN[slot] = player.playerEquipmentN[slot] - 1;
		player.getOutStream().writeFrame(34);
		player.getOutStream().writeShort(6);
		player.getOutStream().writeShort(1688);
		player.getOutStream().writeByte(slot);
		player.getOutStream().writeShort(0);
		player.getOutStream().writeByte(0);
		getBonus();
		if (slot == player.getEquipment().getWeaponId()) {
			player.getWeaponInterface().sendWeapon(-1, "Unarmed");
		}
		resetBonus();
		getBonus();
		writeBonus();
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	public void deleteItem(final int id, int amount) {
		if (id <= 0) {
			return;
		}
		for (int j = 0; j < player.playerItems.length; j++) {
			if (amount <= 0) {
				break;
			}
			if (player.playerItems[j] == id + 1) {
				if (player.playerItemsN[j] > amount) {
					player.playerItemsN[j] -= amount;
					if (player.playerItemsN[j] < 0) {
						player.playerItemsN[j] = 0;
						player.playerItems[j] = 0;
					}
					amount = 0;
				} else {
					player.playerItems[j] = 0;
					player.playerItemsN[j] = 0;
					amount--;
				}
			}
		}
		resetItems(3214);
	}

	public void deleteItem(int id) {
		deleteItem(id, 1);
	}

	public void deleteItem(int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (player.playerItems[slot] == (id + 1)) {
			if (player.playerItemsN[slot] > amount) {
				player.playerItemsN[slot] -= amount;
			} else {
				player.playerItemsN[slot] = 0;
				player.playerItems[slot] = 0;
			}
			resetItems(3214);
		}
	}

	public void remove(Item item) {
		deleteItem(item.id, item.amount);
	}

	public void remove(Item item, int slot, int amount) {
		deleteItem(item.id, slot, amount);
	}

	public void deleteItems(Item... item) {
		Arrays.stream(item).forEach(this::remove);
	}

	public void remove(Item item, int slot) {
		deleteItem(item.id, slot, item.amount);
	}

	/**
	 * Delete Arrows
	 */
	public void deleteArrow() {
		if (player.playerEquipment[player.getEquipment().getCapeId()] == 10499
				&& Utility.getRandom(5) != 1
				|| player.playerEquipment[player.getEquipment().getCapeId()] == 19111
				&& Utility.getRandom(5) != 1
				&& player.playerEquipment[player.getEquipment().getQuiverId()] != 4740)
			return;
		if (player.playerEquipmentN[player.getEquipment().getQuiverId()] == 1) {
			player.getItems()
					.deleteEquipmentSlot(
							player.playerEquipment[player.getEquipment()
									.getQuiverId()],
							player.getEquipment().getQuiverId());
		}

		if (player.playerEquipmentN[player.getEquipment().getQuiverId()] != 0) {
			player.getOutStream().putFrameVarShort(34);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(1688);
			player.getOutStream()
					.writeByte(player.getEquipment().getQuiverId());
			player.getOutStream()
					.writeShort(
							player.playerEquipment[player.getEquipment()
									.getQuiverId()] + 1);
			if (player.playerEquipmentN[player.getEquipment().getQuiverId()] - 1 > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().putInt(
						player.playerEquipmentN[player.getEquipment()
								.getQuiverId()] - 1);
			} else {
				player.getOutStream().writeByte(
						player.playerEquipmentN[player.getEquipment()
								.getQuiverId()] - 1);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
			player.playerEquipmentN[player.getEquipment().getQuiverId()] -= 1;
		}
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	public void deleteAmmo() {
		boolean avaSave = player.playerEquipment[player.getEquipment()
				.getCapeId()] == 10499;
		boolean otherSave = player.playerEquipment[player.getEquipment()
				.getCapeId()] == 19111;
		if (Utility.getRandom(5) != 1 && (avaSave || otherSave)) {
			// arrow saved
			return;
		}
		if (player.playerEquipmentN[player.getEquipment().getWeaponId()] != 0) {
			player.getOutStream().putFrameVarShort(34);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(1688);
			player.getOutStream()
					.writeByte(player.getEquipment().getWeaponId());
			player.getOutStream()
					.writeShort(
							player.playerEquipment[player.getEquipment()
									.getWeaponId()] + 1);
			if (player.playerEquipmentN[player.getEquipment().getWeaponId()] - 1 > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().putInt(
						player.playerEquipmentN[player.getEquipment()
								.getWeaponId()] - 1);
			} else {
				player.getOutStream().writeByte(
						player.playerEquipmentN[player.getEquipment()
								.getWeaponId()] - 1);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
			player.playerEquipmentN[player.getEquipment().getWeaponId()] -= 1;
		}
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	public void removeEquipment() {
		if (player.playerEquipmentN[player.getEquipment().getWeaponId()] != 0) {
			player.getOutStream().putFrameVarShort(34);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(1688);
			player.getOutStream()
					.writeByte(player.getEquipment().getWeaponId());
			player.getOutStream()
					.writeShort(
							player.playerEquipment[player.getEquipment()
									.getWeaponId()] + 1);
			if (player.playerEquipmentN[player.getEquipment().getWeaponId()] - 1 > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().putInt(
						player.playerEquipmentN[player.getEquipment()
								.getWeaponId()] - 1);
			} else {
				player.getOutStream().writeByte(
						player.playerEquipmentN[player.getEquipment()
								.getWeaponId()] - 1);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
			player.playerEquipmentN[player.getEquipment().getWeaponId()] -= 1;
		}
		player.updateRequired = true;
		player.appearanceUpdateRequired = true;
	}

	/**
	 * Dropping Arrows
	 */
	public void dropArrowUnderTarget() {
		// Chinchompas: don't drop ammo
		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 10033
				|| player.playerEquipment[player.getEquipment().getWeaponId()] == 10034) {
			return;
		}
		Entity target = player.getCombat().target;
		int enemyX = target.getX();
		int enemyY = target.getY();
		int enemyHeight = target.heightLevel;
		// Avas equipment: don't drop ammo
		if (player.playerEquipment[player.getEquipment().getCapeId()] == 10499
				|| player.playerEquipment[player.getEquipment().getCapeId()] == 19111)
			return;
		int ammoId = player.playerEquipment[player.getEquipment().getQuiverId()];
		if (Utility.getRandom(10) >= 4) {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(ammoId),
					enemyX, enemyY, enemyHeight, player));
		}
	}

	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public boolean spaceFor(Item item) {
		boolean stackable = isStackable(item.getId());
		if (stackable) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == item.getId()) {
					int totalCount = item.getAmount() + player.playerItemsN[i];
					if (totalCount >= Integer.MAX_VALUE || totalCount < 1) {
						return false;
					}
					return true;
				}
			}
			int slot = freeSlots();
			return slot != -1;
		}

		int slots = freeSlots();
		return slots >= item.getAmount();
	}

	public int freeBankSlots() {
		int freeS = 0;
		for (int i = 0; i < player.bankItems.length; i++) {
			if (player.bankItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public String getItemName(int ItemID) {
		if (ItemID < 0 || ItemDefinition.forId(ItemID) == null) {
			return "Unarmed";
		}
		return ItemDefinition.forId(ItemID).getName();
	}

	public int getItemSlot(int ItemID) {
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	public Item getItemFromSlot(int slot) {
		if (slot == -1 || slot >= player.playerItems.length
				|| player.playerItems[slot] == 0) {
			return null;
		}
		return new Item(player.playerItems[slot] - 1, player.playerItemsN[slot]);
	}

	/**
	 * Checks how many {@code item} the player has in his inventory
	 * 
	 * @param item
	 *            The item we're checking
	 * @return The amount
	 */
	public int getItemAmount(int item) {
		int amount = 0;
		for (int inventory = 0; inventory < player.playerItems.length; inventory++) {
			if ((player.playerItems[inventory] - 1) == item) {
				amount += player.playerItemsN[inventory];
			}
		}
		return amount;
	}

	public boolean playerHasItem(int itemID, int amt, int slot) {
		itemID++;
		int found = 0;
		if (player.playerItems[slot] == (itemID)) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == itemID) {
					if (player.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			return found >= amt;
		}
		return false;
	}

	public boolean playerHasItem(int item) {
		item++;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == item)
				return true;
		}
		return false;
	}

	public boolean playerHasItem(Item item) {
		return playerHasItem(item.id, item.amount);
	}

	public boolean playerHasItems(Item... item) {
		return Arrays.stream(item).allMatch(this::playerHasItem);
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemID) {
				if (player.playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		return found >= amt;
	}

	/**
	 * Sends an item to the bank in any tab possible.
	 *
	 * @param itemId
	 *            the item id
	 * @param amount
	 *            the item amount
	 */
	public void sendItemToAnyTab(int itemId, int amount) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage(
					"You can't do that in the wilderness.");
			return;
		}
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.freeSlots(player) > 0 || tab.contains(item)) {
				player.getBank().setCurrentBankTab(tab);
				addItemToBank(itemId, amount);
				return;
			}
		}
		addItemToBank(itemId, amount);
	}

	public boolean alreadyHasItem(int id) {
		return playerHasItem(id) || bankContains(id);
	}

	public boolean playerOwnsAnyItems(int... ids) {
		return Arrays.stream(ids).anyMatch(this::alreadyHasItem);
	}

	public void sendItemToAnyTabOffline(int itemId, int amount, boolean online) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage("You can't do that in the wilderness.");
			return;
		}
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.freeSlots(player) > 0 || tab.contains(item)) {
				player.getBank().setCurrentBankTab(tab);
				addItemToBank(itemId, amount, online);
				return;
			}
		}
		addItemToBank(itemId, amount, online);
	}

	/**
	 * Adds an item to the players inventory, bank, or drops it. It will do this
	 * under any circumstance so if it cannot be added to the inventory it will
	 * next try to send it to the bank and if it cannot, it will drop it.
	 * 
	 * @param itemId
	 *            the item
	 * @param amount
	 *            the amount of said item
	 */
	public void addItemUnderAnyCircumstance(int itemId, int amount) {
		if (!addItem(itemId, amount)) {
			sendItemToAnyTabOrDrop(new BankItem(itemId, amount), player.getX(),
					player.getY());
		}
	}

	/**
	 * The x and y represents the possible x and y location of the dropped item
	 * if in fact it cannot be added to the bank.
	 * 
	 * @param item
	 * @param x
	 * @param y
	 */
	public void sendItemToAnyTabOrDrop(BankItem item, int x, int y) {
		item = new BankItem(item.getId() + 1, item.getAmount());
		if (bankContains(item.getId() - 2)) {
			if (isBankSpaceAvailable(item)) {
				sendItemToAnyTab(item.getId() - 1, item.getAmount());
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item
						.getId(), item.getAmount()), player.getX(), player
						.getY(), player.getZ(), player));
			}
		} else {
			sendItemToAnyTab(item.getId() - 1, item.getAmount());
		}
	}

	public boolean isBankSpaceAvailable(BankItem item) {
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.contains(item)) {
				return tab.spaceAvailable(item);
			}
		}
		return false;
	}

	public boolean removeFromAnyTabWithoutAdding(int itemId, int itemAmount,
			boolean updateView) {
		BankTab tab = null;
		BankItem item = new BankItem(itemId + 1, itemAmount);
		for (BankTab searchedTab : player.getBank().getBankTab()) {
			if (searchedTab.contains(item)) {
				tab = searchedTab;
				break;
			}
		}
		if (tab == null) {
			return false;
		}
		if (itemAmount <= 0)
			return false;
		if (!tab.contains(item))
			return false;
		if (tab.getItemAmount(item) < itemAmount) {
			item.setAmount(tab.getItemAmount(item));
		}
		if (item.getAmount() < 0)
			item.setAmount(0);
		tab.remove(item);
		if (tab.size() == 0) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
		}
		if (updateView) {
			resetBank();
		}
		player.getItems().resetItems(5064);
		return true;
	}

	public boolean bankContains(int itemId, int itemAmount) {
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.containsAmount(new BankItem(itemId + 1, itemAmount))) {
				return true;
			}
		}
		return false;
	}

	public void clearSlot(int slot) {
		if (slot < 0 || slot > 27)
			return;
		player.playerItems[slot] = 0;
		player.playerItemsN[slot] = 0;
		resetItems(3214);
	}

	public void replaceSlot(int item, int slot) {
		clearSlot(slot);
		addItem(item, 1, slot);
	}

	public boolean addItem(int item, int amount, int slot) {
		if (amount < 1) {
			amount = 1;
		}
		if (item <= 0) {
			return false;
		}
		if (player.playerItems[slot] > 0 || player.playerItemsN[slot] > 0) {
			return addItem(item, amount);
		}
		player.playerItems[slot] = (item + 1);
		if (player.playerItemsN[slot] + amount < Integer.MAX_VALUE
				&& player.playerItemsN[slot] + amount > -1) {
			player.playerItemsN[slot] += amount;
		} else {
			player.playerItemsN[slot] = Integer.MAX_VALUE;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarShort(34);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(3214);
			player.getOutStream().writeByte(slot);
			player.getOutStream().writeShort(player.playerItems[slot]);
			if (player.playerItemsN[slot] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().putInt(player.playerItemsN[slot]);
			} else {
				player.getOutStream().writeByte(player.playerItemsN[slot]);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
		}
		return false;
	}

	public void reset() {
		if (player.getArea().inWild() && player.rights != Rights.ADMINISTRATOR) {
			return;
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			deleteItem(player.playerItems[i] - 1, player.getItems()
					.getItemSlot(player.playerItems[i] - 1),
					player.playerItemsN[i]);
		}
	}

	public void addOrSendToBank(int item, int amount) {
		if (freeSlots() > 0) {
			addItem(item, amount);
		} else {
			addToBank(item, amount, true);
			player.getActionSender().sendMessage("Invntory full, the item was sent to your bank.");
		}
	}

	public void addOrCreateGroundItem(Item item) {
		if (freeSlots() > 0) {
			addItem(item.getId(), item.getAmount());
		} else if ((item.getAmount() > 1) && (!ItemDefinition.forId(item.getId()).isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		}
	}

	public void removeEquipment(int wearID, int slot) {
		if (player.getOutStream() != null && player != null) {
			if (player.playerEquipment[slot] > -1) {
				if (addItem(player.playerEquipment[slot],
						player.playerEquipmentN[slot])) {
					player.playerEquipment[slot] = -1;
					player.playerEquipmentN[slot] = 0;
					player.getWeaponInterface().sendWeapon(
							player.playerEquipment[player.getEquipment()
									.getWeaponId()],
							getItemName(player.playerEquipment[player
									.getEquipment().getWeaponId()]));
					resetBonus();
					getBonus();
					writeBonus();
					CombatAnimation.itemAnimations(player);
					player.getOutStream().writeFrame(34);
					player.getOutStream().writeShort(6);
					player.getOutStream().writeShort(1688);
					player.getOutStream().writeByte(slot);
					player.getOutStream().writeShort(0);
					player.getOutStream().writeByte(0);
					player.flushOutStream();
					player.updateRequired = true;
					player.appearanceUpdateRequired = true;
				}
			}
		}
	}

	public int search(int... ids) {
		for (int id : ids)
			if (playerHasItem(id))
				return id;
		return -1;
	}

	/**
	 * Checks how many we have of an certain item
	 * 
	 * @param item
	 *            The item we're deleting
	 * @return The amount of the {linkplain item} we've found.
	 */
	public int checkAmount(int item) {
		int found = 0;
		boolean stackable = new Item(item).getDefinition().isStackable();
		for (int i = 0; i < player.playerItems.length; i++) {
			// System.out.println("item["+i+"]="+player.playerItems[i]);
			if (player.playerItems[i] == (item + 1)) {
				if (stackable) {
					return player.playerItemsN[i];
				} else {
					found += player.playerItemsN[i];
				}
			}
		}
		// System.out.println(found);
		return found;
	}

}