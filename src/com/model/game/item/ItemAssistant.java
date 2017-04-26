package com.model.game.item;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.Entity;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Utility;

public class ItemAssistant {

	private Player player;

	public ItemAssistant(Player client) {
		this.player = client;
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
}