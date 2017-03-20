package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatStyle;
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
		
		boolean missedFirstHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedFirstHit)
			firstHit = 0;
		
		boolean missedSecondHit = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missedSecondHit)
			secondHit = 0;
		
		// Set up a Hit instance
        Hit hitInfo = target.take_hit(player, firstHit, CombatStyle.MELEE).giveXP(player);

        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MELEE);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {

			@Override
			public void execute() {
				// Set up a Hit instance
		        Hit hitInfo = target.take_hit(player, finalDamage, CombatStyle.MELEE).giveXP(player);

		        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MELEE);
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