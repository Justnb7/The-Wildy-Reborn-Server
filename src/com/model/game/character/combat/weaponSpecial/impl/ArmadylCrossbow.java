package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		
	}

	@Override
	public int amountRequired() {
		return 40;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 1) {
			player.getActionSender().sendMessage("You need atleast 1 bolt to perform this special.");
			player.setUsingSpecial(false);
			return false;
		}
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 2;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
