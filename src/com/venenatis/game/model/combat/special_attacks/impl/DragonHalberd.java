package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

public class DragonHalberd implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 3204 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int firstHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
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
		
		Server.getTaskScheduler().schedule(new Task(1) {

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
