package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.playAnimation(Animation.create(4230));
		//player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 0));

		//TODO implement gfx 301
		int d = player.getLocation().distanceToEntity(player, target);
		player.playProjectile(Projectile.create(player.getLocation(), target, 301, 60, 50, 65 + (d * 5), 43, 35, 10, 36));
		player.getCombatState().fireProjectileAtTarget();

		// Step 1: calculate a hit
		int dam1 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());

		// Step 2: check if it missed
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam1 = 0;
		}

		// Step 3: check target's protection prayers
		target.take_hit(player, dam1, CombatStyle.RANGE).send(2);
	}

	@Override
	public int amountRequired(Player player) {
		return 40 * (int) player.getAttribute("vigour");
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
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
