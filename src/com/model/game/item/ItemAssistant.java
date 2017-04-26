package com.model.game.item;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.Entity;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

public class ItemAssistant {

	private Player player;

	public ItemAssistant(Player client) {
		this.player = client;
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
	/*public boolean isItemAddable(int item, int amount) {
		if (amount < 1) {
			amount = 1;
		}

		if (item <= 0) {
			return false;
		}

		if ((((player.getInventory().remaining() >= 1) || player.getInventory().playerHasItem(item, 1)) && ItemDefinition
				.forId(item).isStackable())
				|| ((player.getInventory().remaining() > 0) && !ItemDefinition.forId(item).isStackable())) {

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
	}*/
	
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

	public String getItemName(int ItemID) {
		if (ItemID < 0 || ItemDefinition.forId(ItemID) == null) {
			return "Unarmed";
		}
		return ItemDefinition.forId(ItemID).getName();
	}

	public void addOrCreateGroundItem(Item item) {
		if (player.getInventory().remaining() > 0) {
			player.getInventory().add(new Item(item.getId(), item.getAmount()));
		} else if ((item.getAmount() > 1) && (!ItemDefinition.forId(item.getId()).isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		}
	}
	

}