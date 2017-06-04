package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {

	}

	@Override
	public int amountRequired() {
		return 50;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
		if (player.getCombatType() == CombatStyle.RANGE) {
			return true;
		}
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 2) {
			player.getActionSender().sendMessage("You need atleast 2 arrows to perform this special.");
			player.setUsingSpecial(false);
			return false;
		}
		return false;
	}

	
	@Override
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}
}