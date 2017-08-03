package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class Ballista implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 19481 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.playAnimation(Animation.create(7222));
		
		//TODO implement projectile
		
		int damage = Utility.random(player.getCombatState().calculateRangeMaxHit());
		boolean success = CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (!success) {
			damage = 0;
		}

		target.take_hit(player, damage, CombatStyle.RANGE).giveXP(player).send(2);
		
	}

	@Override
	public int amountRequired() {
		return 65;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT); 
		if (ammo.getId() < 1) {
			player.message("You need at least one javalin to use this special attack.");
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