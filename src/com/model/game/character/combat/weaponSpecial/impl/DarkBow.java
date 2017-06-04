package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

public class DarkBow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11235, 12765, 12766, 12767, 12768 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		
		
	}

	@Override
	public int amountRequired() {
		return 65;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 2) {
			player.getActionSender().sendMessage("You need at least two arrows to use this special attack.");
			return false;
		}
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1.75;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}
