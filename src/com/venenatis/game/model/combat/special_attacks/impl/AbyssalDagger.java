package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class AbyssalDagger implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13265, 13267, 13269, 13271 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int firstHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		
		player.playAnimation(Animation.create(3300));
		player.playGraphics(Graphic.highGraphic(1283));
		
		if (firstHit > 40 || secondHit > 40) {
			firstHit = 40;
			secondHit = 40;
		}
		if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
			firstHit = 0;
		}

		if (firstHit == 0 || !(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
			secondHit = 0;
		}
		target.take_hit(player, firstHit, CombatStyle.MELEE).giveXP(player).send();
		target.take_hit(player, secondHit, CombatStyle.MELEE).giveXP(player).send();
	}

	@Override
	public int amountRequired(Player player) {
		return 50 * (int) player.getAttribute("vigour");
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 2.5;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
