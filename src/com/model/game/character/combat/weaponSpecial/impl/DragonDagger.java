package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class DragonDagger implements SpecialAttack {
	
	
	@Override
	public int[] weapons() {
		return new int[] { 1215, 5680, 5698 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		int firstHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		final int finalDamage = secondHit;
		
		player.playAnimation(Animation.create(1062));
		player.playGraphics(Graphic.highGraphic(252));
		
		boolean missedFirstHit = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedFirstHit)
			firstHit = 0;
		
		boolean missedSecondHit = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedSecondHit)
			secondHit = 0;
		
		target.take_hit(player, firstHit, CombatType.MELEE).giveXP(player);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {

			@Override
			public void execute() {
				target.take_hit(player, finalDamage, CombatType.MELEE).giveXP(player);
				this.stop();
			}
		});
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