package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class DragonClaws implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13652 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int first = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int second = first / 2;
		int third = second / 2;
		int fourth = second / 2;

		final int damage = third;
		final int finalDmg = fourth;

		player.playAnimation(Animation.create(5283));
		player.playGraphics(Graphic.highGraphic(1171));

		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulas.getAccuracy((Entity) player, (Entity) target, 0, getAccuracyMultiplier()))) {
				first = 0;
				second = 0;
				third = 0;
				fourth = 0;
			}

			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				first = (int) (first * 0.6);
				second = (int) (second * 0.6);
				third = (int) (damage * 0.6);
				fourth = (int) (finalDmg * 0.6);
			}

			if (targPlayer.hasVengeance()) {
				targPlayer.getCombat().vengeance(player, damage, 1);
			}

			CombatExperience.handleCombatExperience(player, first, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, second, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, finalDmg, CombatType.MELEE);

			targPlayer.damage(new Hit(first, first > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(second, second > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED),
					new Hit(finalDmg, finalDmg > 0 ? HitType.NORMAL : HitType.BLOCKED));

		} else {
			Npc targNpc = (Npc) target;

			if (!(CombatFormulas.getAccuracy((Entity) player, (Entity) target, 0, getAccuracyMultiplier()))) {
				first = 0;
				second = 0;
				third = 0;
				fourth = 0;
			}
			CombatExperience.handleCombatExperience(player, first, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, second, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			CombatExperience.handleCombatExperience(player, finalDmg, CombatType.MELEE);
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
