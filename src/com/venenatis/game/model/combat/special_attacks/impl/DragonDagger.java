package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class DragonDagger implements SpecialAttack {
	
	
	@Override
	public int[] weapons() {
		return new int[] { 1215, 5680, 5698 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		int firstHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		
		player.playAnimation(Animation.create(1062));
		player.playGraphic(Graphic.highGraphic(252));
		
		boolean missedFirstHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedFirstHit)
			firstHit = 0;
		
		boolean missedSecondHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedSecondHit)
			secondHit = 0;
		
		// Set up a Hit instance
        target.take_hit(player, firstHit, CombatStyle.MELEE).giveXP(player).send(); // 1st hit
		target.take_hit(player, secondHit, CombatStyle.MELEE).giveXP(player).send(2); // 2nd hit, 1 tick later
		
		AchievementHandler.activate(player, AchievementList.MR_POKEY, 1);
		if (target.isPlayer()) {
			if (firstHit >= 35 && secondHit >= 35) {
				AchievementHandler.activate(player, AchievementList.OUCH, 1);
			}			
		}
	}

	@Override
	public int amountRequired() {
		return 25;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
		return true;
	}
	
	@Override
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}
}