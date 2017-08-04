package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
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
		player.playAnimation(Animation.create(5061));
		player.playProjectile(Projectile.create(player.getLocation(), target, 1043, 20, 50, 35, 38, 36, 13, 64));

		// Step 1: calculate a hit
		int randomHit = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());

		// Step 2: check if it missed
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) {
			randomHit = 0;
		}

		//Increase hitpoints
        player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, (randomHit / 2) + 1);

		// Step 3: check target's protection prayers
		target.take_hit(player, randomHit, CombatStyle.RANGE).send(2);
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
