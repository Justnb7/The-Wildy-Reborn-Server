package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

public class Ballista implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 19481 };
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
		Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT); 
		if (ammo.getId() < 1) {
			player.getActionSender().sendMessage("You need at least one javalin to use this special attack.");
			return false;
		}
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 3.75;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}