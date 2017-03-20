package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class DragonHalberd implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 3204 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int firstHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		final int finalDamage = secondHit;
		player.playAnimation(Animation.create(1203));
		player.playGraphics(Graphic.create(1172, 0, 0));
		
		boolean missedFirstHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedFirstHit)
			firstHit = 0;
		
		boolean missedSecondHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedSecondHit)
			secondHit = 0;
		
		target.take_hit(player, firstHit, CombatStyle.MELEE).giveXP(player);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {

			@Override
			public void execute() {
				target.take_hit(player, finalDamage, CombatStyle.MELEE).giveXP(player);
				this.stop();
			}
		});
	}

	@Override
	public int amountRequired() {
		return 30;
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
