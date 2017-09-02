package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class SaradominSword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11838, 12809 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());

		player.playAnimation(Animation.create(1132));
		target.playGraphic(Graphic.highGraphic(1196));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MAGIC).giveXP(player).send();
	}

	@Override
	public int amountRequired() {
		return 100;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.4;
	}

}
