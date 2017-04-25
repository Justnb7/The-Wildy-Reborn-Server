package com.model.game.item;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.Constants;
import com.model.game.character.Entity;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.item.bank.BankItem;
import com.model.game.item.container.impl.Equipment;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

import java.util.Arrays;

public class ItemAssistant {

	private Player player;

	public ItemAssistant(Player client) {
		this.player = client;
	}

	public void updateInventory() {
		this.resetItems(3214);
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

	public void resetItems(int WriteFrame) {
		System.out.println("nty");
		// synchronized (c) {
		/*if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarShort(53);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(WriteFrame);
			player.getOutStream().writeShort(player.playerInventory.length);
			for (int i = 0; i < player.playerInventory.length; i++) {
				if (player.itemAmount[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.itemAmount[i]);
				} else {
					player.getOutStream().writeByte(player.itemAmount[i]);
				}
				player.getOutStream()
						.writeWordBigEndianA(player.playerInventory[i]);
			}
			player.getOutStream().putFrameSizeShort(offset);
			player.flushOutStream();
		}*/
		// }
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

			for (int i = 0; i < player.playerInventory.length; i++) {
				if ((player.playerInventory[i] == (item + 1))
						&& ItemDefinition.forId(item).isStackable()
						&& (player.playerInventory[i] > 0)) {
					return true;
				}
			}

			for (int i = 0; i < player.playerInventory.length; i++) {
				if (player.playerInventory[i] <= 0) {
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
			for (int i = 0; i < player.playerInventory.length; i++) {
				if ((player.playerInventory[i] == (item + 1))
						&& ItemDefinition.forId(item).isStackable()
						&& (player.playerInventory[i] > 0)) {
					player.playerInventory[i] = (item + 1);
					if (((player.itemAmount[i] + amount) < Constants.MAXITEM_AMOUNT)
							&& ((player.itemAmount[i] + amount) > -1)) {
						player.itemAmount[i] += amount;
					} else {
						player.itemAmount[i] = Constants.MAXITEM_AMOUNT;
					}
					if (player.getOutStream() != null && player != null) {
						player.getOutStream().putFrameVarShort(34);
						int offset = player.getOutStream().offset;
						player.getOutStream().writeShort(3214);
						player.getOutStream().writeByte(i);
						player.getOutStream().writeShort(player.playerInventory[i]);
						if (player.itemAmount[i] > 254) {
							player.getOutStream().writeByte(255);
							player.getOutStream()
									.putInt(player.itemAmount[i]);
						} else {
							player.getOutStream().writeByte(
									player.itemAmount[i]);
						}
						player.getOutStream().putFrameSizeShort(offset);
						player.flushOutStream();
					}
					i = 30;
					return true;
				}
			}
			for (int i = 0; i < player.playerInventory.length; i++) {
				if (player.playerInventory[i] <= 0) {
					player.playerInventory[i] = item + 1;
					if ((amount < Constants.MAXITEM_AMOUNT) && (amount > -1)) {
						player.itemAmount[i] = 1;
						if (amount > 1) {
							player.getItems().addItem(item, amount - 1);
							return true;
						}
					} else {
						player.itemAmount[i] = Constants.MAXITEM_AMOUNT;
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

	public void addItem(Item item) {
		addItem(item.getId(), item.getAmount());
	}
	
	public void deleteArrow() {
		if (player.getEquipment().getId(Equipment.CAPE_SLOT) == 10499 && Utility.getRandom(5) != 1 || player.getEquipment().getId(Equipment.CAPE_SLOT) == 19111 && Utility.getRandom(5) != 1 && player.getEquipment().getId(Equipment.ARROWS_SLOT) != 4740)
			return;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void deleteAmmo() {
		boolean avaSave = player.getEquipment().getId(Equipment.CAPE_SLOT) == 10499;
		boolean otherSave = player.getEquipment().getId(Equipment.CAPE_SLOT) == 19111;
		if (Utility.getRandom(5) != 1 && (avaSave || otherSave)) {
			// arrow saved
			return;
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Dropping Arrows
	 */
	public void dropArrowUnderTarget() {
		// Chinchompas: don't drop ammo
		if (player.getEquipment().getId(Equipment.WEAPON_SLOT) == 10033 || player.getEquipment().getId(Equipment.WEAPON_SLOT) == 10034) {
			return;
		}
		Entity target = player.getCombat().target;
		int enemyX = target.getX();
		int enemyY = target.getY();
		int enemyHeight = target.heightLevel;
		// Avas equipment: don't drop ammo
		if (player.getEquipment().getId(Equipment.CAPE_SLOT) == 10499 || player.getEquipment().getId(Equipment.CAPE_SLOT) == 19111)
			return;
		int ammoId = player.getEquipment().getId(Equipment.ARROWS_SLOT);
		if (Utility.getRandom(10) >= 4) {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(ammoId), enemyX, enemyY, enemyHeight, player));
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
	 * Checking item amounts.
	 * 
	 * @param itemID
	 * @return
	 */
	public int itemAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < player.playerInventory.length; i++) {
			if (player.playerInventory[i] == itemID) {
				tempAmount += player.itemAmount[i];
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
	
	public boolean isNotable(int itemId) {
		boolean withdrawAsNote = ItemDefinition.forId(itemId).isNoted();

		if (withdrawAsNote)
			return true;
		return false;
	}

	/**
	 * Move Items
	 */
	public void swap(int from, int to, int moveWindow, boolean insertMode) {
		if (moveWindow == 3214) {
			int tempI;
			int tempN;
			tempI = player.playerInventory[from];
			tempN = player.itemAmount[from];
			player.playerInventory[from] = player.playerInventory[to];
			player.itemAmount[from] = player.itemAmount[to];
			player.playerInventory[to] = tempI;
			player.itemAmount[to] = tempN;
		}
		if (moveWindow == 5382) {
			if (!player.isBanking()) {
				player.getActionSender().sendRemoveInterfacePacket();
				player.getBank().resetBank();
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
					player.getBank().resetBank();
					return;
				}
				if (from >= player.getBank().getCurrentBankTab().size()) {
					player.getBank().resetBank();
					return;
				}
				BankItem item = player.getBank().getCurrentBankTab().getItem(from);
				if (item == null) {
					player.getBank().resetBank();
					return;
				}
				if (player.getBank().getBankTab()[tabId].size() >= player.BANK_SIZE) {
					player.getActionSender().sendMessage("You cannot move anymore items to that tab.");
					player.getBank().resetBank();
					return;
				}
				player.getBank().getCurrentBankTab().remove(item);
				player.getBank().getBankTab()[tabId].add(item);
			} else {
				if (from > player.getBank().getCurrentBankTab().size() - 1 || to > player.getBank().getCurrentBankTab().size() - 1) {
					player.getBank().resetBank();
					return;
				}
				if (!insertMode) {
					BankItem item = player.getBank().getCurrentBankTab().getItem(from);
					player.getBank().getCurrentBankTab().setItem(from, player.getBank().getCurrentBankTab().getItem(to));
					player.getBank().getCurrentBankTab().setItem(to, item);
				} else {
					int tempFrom = from;
					for (int tempTo = to; tempFrom != tempTo;)
						if (tempFrom > tempTo) {
							player.getBank().swapBankItem(tempFrom, tempFrom - 1);
							tempFrom--;
						} else if (tempFrom < tempTo) {
							player.getBank().swapBankItem(tempFrom, tempFrom + 1);
							tempFrom++;
						}
				}
			}
		}
		if (moveWindow == 5382) {
			player.getBank().resetBank();
		}
		if (moveWindow == 5064) {
			int tempI;
			int tempN;
			tempI = player.playerInventory[from];
			tempN = player.itemAmount[from];

			player.playerInventory[from] = player.playerInventory[to];
			player.itemAmount[from] = player.itemAmount[to];
			player.playerInventory[to] = tempI;
			player.itemAmount[to] = tempN;
			resetItems(3214);
		}
		player.getBank().resetTempItems();
		if (moveWindow == 3214) {
			resetItems(3214);
		}

	}

	public void deleteItem(final int id, int amount) {
		if (id <= 0) {
			return;
		}
		for (int j = 0; j < player.playerInventory.length; j++) {
			if (amount <= 0) {
				break;
			}
			if (player.playerInventory[j] == id + 1) {
				if (player.itemAmount[j] > amount) {
					player.itemAmount[j] -= amount;
					if (player.itemAmount[j] < 0) {
						player.itemAmount[j] = 0;
						player.playerInventory[j] = 0;
					}
					amount = 0;
				} else {
					player.playerInventory[j] = 0;
					player.itemAmount[j] = 0;
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
		if (player.playerInventory[slot] == (id + 1)) {
			if (player.itemAmount[slot] > amount) {
				player.itemAmount[slot] -= amount;
			} else {
				player.itemAmount[slot] = 0;
				player.playerInventory[slot] = 0;
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

	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < player.playerInventory.length; i++) {
			if (player.playerInventory[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public boolean spaceFor(Item item) {
		boolean stackable = isStackable(item.getId());
		if (stackable) {
			for (int i = 0; i < player.playerInventory.length; i++) {
				if (player.playerInventory[i] == item.getId()) {
					int totalCount = item.getAmount() + player.itemAmount[i];
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

	public String getItemName(int ItemID) {
		if (ItemID < 0 || ItemDefinition.forId(ItemID) == null) {
			return "Unarmed";
		}
		return ItemDefinition.forId(ItemID).getName();
	}

	// returns the slot which matches itemID
	public int getItemSlot(int ItemID) {
		for (int i = 0; i < player.playerInventory.length; i++) {
			if ((player.playerInventory[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	// returns Item instance of id,amount, using the slot. you have to know the slot.
	public Item getItemFromSlot(int slot) {
		if (slot == -1 || slot >= player.playerInventory.length
				|| player.playerInventory[slot] == 0) {
			return null;
		}
		return new Item(player.playerInventory[slot] - 1, player.itemAmount[slot]);
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
		for (int inventory = 0; inventory < player.playerInventory.length; inventory++) {
			if ((player.playerInventory[inventory] - 1) == item) {
				amount += player.itemAmount[inventory];
			}
		}
		return amount;
	}

	public boolean playerHasItem(int itemID, int amt, int slot) {
		itemID++;
		int found = 0;
		if (player.playerInventory[slot] == (itemID)) {
			for (int i = 0; i < player.playerInventory.length; i++) {
				if (player.playerInventory[i] == itemID) {
					if (player.itemAmount[i] >= amt) {
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
		for (int i = 0; i < player.playerInventory.length; i++) {
			if (player.playerInventory[i] == item)
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
		for (int i = 0; i < player.playerInventory.length; i++) {
			if (player.playerInventory[i] == itemID) {
				if (player.itemAmount[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		return found >= amt;
	}

	public boolean alreadyHasItem(int id) {
		return playerHasItem(id) || player.getBank().bankContains(id);
	}

	public boolean playerOwnsAnyItems(int... ids) {
		return Arrays.stream(ids).anyMatch(this::alreadyHasItem);
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
			player.getBank().sendItemToAnyTabOrDrop(new BankItem(itemId, amount), player.getX(), player.getY());
		}
	}

	public void clearSlot(int slot) {
		if (slot < 0 || slot > 27)
			return;
		player.playerInventory[slot] = 0;
		player.itemAmount[slot] = 0;
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
		if (player.playerInventory[slot] > 0 || player.itemAmount[slot] > 0) {
			return addItem(item, amount);
		}
		player.playerInventory[slot] = (item + 1);
		if (player.itemAmount[slot] + amount < Integer.MAX_VALUE
				&& player.itemAmount[slot] + amount > -1) {
			player.itemAmount[slot] += amount;
		} else {
			player.itemAmount[slot] = Integer.MAX_VALUE;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarShort(34);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeShort(3214);
			player.getOutStream().writeByte(slot);
			player.getOutStream().writeShort(player.playerInventory[slot]);
			if (player.itemAmount[slot] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().putInt(player.itemAmount[slot]);
			} else {
				player.getOutStream().writeByte(player.itemAmount[slot]);
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
		for (int i = 0; i < player.playerInventory.length; i++) {
			deleteItem(player.playerInventory[i] - 1, player.getItems()
					.getItemSlot(player.playerInventory[i] - 1),
					player.itemAmount[i]);
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
		for (int i = 0; i < player.playerInventory.length; i++) {
			// System.out.println("item["+i+"]="+player.playerItems[i]);
			if (player.playerInventory[i] == (item + 1)) {
				if (stackable) {
					return player.itemAmount[i];
				} else {
					found += player.itemAmount[i];
				}
			}
		}
		// System.out.println(found);
		return found;
	}

}