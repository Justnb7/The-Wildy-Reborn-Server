package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class ToxicBlowpipe implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 12926 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateRangeMaxHit());
		if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
			damage = 0;
		}
		player.playAnimation(Animation.create(5061));

		//TODO implement gfx 1043
		target.take_hit(player, damage, CombatStyle.RANGE).giveXP(player).send();
	}

	@Override
	public int amountRequired() {
		return 50;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1.50;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}
