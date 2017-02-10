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

public class DragonDagger implements SpecialAttack {
	
	
	@Override
	public int[] weapons() {
		return new int[] { 1215, 5680, 5698 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		int damage1 = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int damage2 = Utility.random(player.getCombat().calculateMeleeMaxHit());
		final int finalDamage = damage2;
		
		player.playAnimation(Animation.create(1062));
		player.playGraphics(Graphic.highGraphic(252));
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage1 = 0;
		
		target.take_hit(player, damage1, CombatType.MELEE).giveXP(player);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {

			@Override
			public void execute() {
				target.take_hit(player, finalDamage, CombatType.MELEE).giveXP(player);
				this.stop();
			}
		});
		
		/*if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage1 = 0;
			}
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage2 = 0;
			}
			
			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				damage1 = (int) (damage1 * 0.6);
				damage2 = (int) (finalDamage * 0.6);
			}
			if (player.hasVengeance()) {
				targPlayer.getCombat().vengeance(targPlayer, damage1, 1);
			}
			CombatExperience.handleCombatExperience(player, damage1, CombatType.MELEE);
			
			targPlayer.damage(new Hit(damage1, damage1 > 0 ? HitType.NORMAL : HitType.BLOCKED));
			
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
				damage1 = 0;
			}
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage2 = 0;
			}
			targNpc.damage(new Hit(damage1, damage1 > 0 ? HitType.NORMAL : HitType.BLOCKED));
			CombatExperience.handleCombatExperience(player, damage1, CombatType.MELEE);
			
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					CombatExperience.handleCombatExperience(player, finalDamage, CombatType.MELEE);
					targNpc.damage(new Hit(finalDamage, finalDamage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		}*/
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