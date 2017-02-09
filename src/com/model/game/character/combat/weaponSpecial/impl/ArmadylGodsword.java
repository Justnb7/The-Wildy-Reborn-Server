package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class ArmadylGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11802, 20593, 20368 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {

		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());

		player.playAnimation(Animation.create(7061));
		player.playGraphics(Graphic.highGraphic(1211));
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// New magical method that does all in one, saving you from writing this code in every single
		// special attack class
		((Entity)target).take_hit(player, damage);

		// Good bye now not needed code!
		/*if (target instanceof Player) {
			Player targPlayer = (Player) target;

			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				damage = (int) (damage * 0.6);
			}
			
			if (targPlayer.hasVengeance()) {
				targPlayer.getCombat().vengeance(player, damage, 1);
			}
			
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targPlayer.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		} else {
			Npc targNpc = (Npc) target;
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targNpc.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		}*/
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
		return 3.0;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.375;
	}

}
