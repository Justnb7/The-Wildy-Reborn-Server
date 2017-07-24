package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatExperience;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class DragonClaws implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13652 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int first = Utility.random(player.getCombatState().calculateMeleeMaxHit());

		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (targPlayer.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				first = (int) (first * 0.6);
			}
		}

		int second = first / 2;
		int third = first / 2;
		int fourth = second / 4;

		if (!(CombatFormulae.getAccuracy((Entity) player, (Entity) target, 0, getAccuracyMultiplier()))) {
			first = 0;
			second = 0;
			third = 0;
			fourth = 0;
		}

		player.playAnimation(Animation.create(5283));
		player.playGraphics(Graphic.highGraphic(1171));
		target.take_hit(player, first, CombatStyle.MELEE, false, true).send();
		target.take_hit(player, second, CombatStyle.MELEE, false, true).send();
		target.take_hit(player, third, CombatStyle.MELEE, false, true).send(2);
		target.take_hit(player, fourth, CombatStyle.MELEE, false, true).send(2);

		CombatExperience.handleCombatExperience(player, first+second+third+fourth, CombatStyle.MELEE);
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
		return 4.0;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.2;
	}

}
