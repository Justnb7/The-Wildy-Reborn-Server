package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class SaradominGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11806 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		int hitpointsHeal = damage / 2;
		int prayerHeal = damage / 4;
		
		player.playAnimation(Animation.create(7058));
		player.playGraphics(Graphic.create(1209, 0, 0));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, hitpointsHeal);
		player.getSkills().increaseLevelToMaximum(Skills.PRAYER, prayerHeal);
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send();
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
		return 3.0;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.375;
	}

}
