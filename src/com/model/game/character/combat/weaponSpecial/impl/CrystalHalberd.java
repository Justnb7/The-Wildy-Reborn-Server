package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
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
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class CrystalHalberd implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13080, 13091, 13092 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int firstHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombat().calculateMeleeMaxHit());
		final int finalDamage = secondHit;
		player.playAnimation(Animation.create(1203));
		player.playGraphics(Graphic.create(1232, 0, 0));
		if(target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				firstHit = 0;
			}
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				secondHit = 0;
			}
			
			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				firstHit = (int) (firstHit * 0.6);
				secondHit = (int) (finalDamage * 0.6);
			}
			if (player.hasVengeance()) {
				targPlayer.getCombat().vengeance(targPlayer, firstHit, 1);
			}
			CombatExperience.handleCombatExperience(player, firstHit, CombatType.MELEE);
			targPlayer.damage(new Hit(firstHit, firstHit > 0 ? HitType.NORMAL : HitType.BLOCKED));
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					CombatExperience.handleCombatExperience(player, finalDamage, CombatType.MELEE);
					targPlayer.damage(new Hit(finalDamage, finalDamage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		} else {
            Npc targNpc = (Npc) target;
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				firstHit = 0;
			}
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				secondHit = 0;
			}
			CombatExperience.handleCombatExperience(player, firstHit, CombatType.MELEE);
			targNpc.damage(new Hit(firstHit, firstHit > 0 ? HitType.NORMAL : HitType.BLOCKED));
			
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					CombatExperience.handleCombatExperience(player, finalDamage, CombatType.MELEE);
					targNpc.damage(new Hit(finalDamage, finalDamage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		}
		
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