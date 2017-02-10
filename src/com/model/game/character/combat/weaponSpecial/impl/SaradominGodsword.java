package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.utility.Utility;

public class SaradominGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11806 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int hitpointsHeal = damage / 2;
		int prayerHeal = damage / 4;
		
		player.playAnimation(Animation.create(7058));
		player.playGraphics(Graphic.create(1209, 0, 0));
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, hitpointsHeal);
		player.getSkills().increaseLevelToMaximum(Skills.PRAYER, prayerHeal);
		
		target.take_hit(player, damage, CombatType.MELEE).giveXP(player);
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
