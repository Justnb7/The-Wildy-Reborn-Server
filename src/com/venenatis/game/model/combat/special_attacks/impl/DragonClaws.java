package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatExperience;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.npc.NPC;
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
		int second = first / 2;
		int third = second / 2;
		int fourth = second / 2;

		final int damage = third;
		final int finalDmg = fourth;

		player.playAnimation(Animation.create(5283));
		player.playGraphics(Graphic.highGraphic(1171));

		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulae.getAccuracy((Entity) player, (Entity) target, 0, getAccuracyMultiplier()))) {
				first = 0;
				second = 0;
				third = 0;
				fourth = 0;
			}

			if (targPlayer.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				first = (int) (first * 0.6);
				second = (int) (second * 0.6);
				third = (int) (damage * 0.6);
				fourth = (int) (finalDmg * 0.6);
			}

			if (targPlayer.hasVengeance()) {
				targPlayer.getCombatState().vengeance(player, damage, 1);
			}

			CombatExperience.handleCombatExperience(player, first, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, second, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, damage, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, finalDmg, CombatStyle.MELEE);

			targPlayer.damage(new Hit(first, first > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(second, second > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(finalDmg, finalDmg > 0 ? HitType.NORMAL : HitType.BLOCKED));

		} else {
			NPC targNpc = (NPC) target;

			if (!(CombatFormulae.getAccuracy((Entity) player, (Entity) target, 0, getAccuracyMultiplier()))) {
				first = 0;
				second = 0;
				third = 0;
				fourth = 0;
			}
			CombatExperience.handleCombatExperience(player, first, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, second, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, damage, CombatStyle.MELEE);
			CombatExperience.handleCombatExperience(player, finalDmg, CombatStyle.MELEE);
			targNpc.damage(new Hit(first, first > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(second, second > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(finalDmg, finalDmg > 0 ? HitType.NORMAL : HitType.BLOCKED));
		}
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
