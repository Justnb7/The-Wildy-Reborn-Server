package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class AbyssalDagger implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13265, 13267, 13269, 13271 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int firstHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		int secondHit = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		final int finalDamage = secondHit;
		
		player.playAnimation(Animation.create(3300));
		player.playGraphics(Graphic.highGraphic(1283));
		
		if (firstHit > 40 || secondHit > 40) {
			firstHit = 40;
			secondHit = 40;
		}
		
		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				firstHit = 0;
			}
			
			if (firstHit == 0 || !(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				secondHit = 0;
			}
			
			if (targPlayer.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				firstHit = (int) (firstHit * 0.6);
				secondHit = (int) (finalDamage * 0.6);
			}
			
			if (player.hasVengeance()) {
				targPlayer.getCombatState().vengeance(targPlayer, firstHit, 1);
			}
			
			CombatExperience.handleCombatExperience(player, firstHit, CombatStyle.MELEE);
			targPlayer.damage(new Hit(firstHit, firstHit > 0 ? HitType.NORMAL : HitType.BLOCKED));
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					CombatExperience.handleCombatExperience(player, finalDamage, CombatStyle.MELEE);
					targPlayer.damage(new Hit(finalDamage, finalDamage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		} else {
			NPC targNpc = (NPC) target;
			
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				firstHit = 0;
			}
			
			if (firstHit == 0 || !(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				secondHit = 0;
			}
			CombatExperience.handleCombatExperience(player, firstHit, CombatStyle.MELEE);
			targNpc.damage(new Hit(firstHit, firstHit > 0 ? HitType.NORMAL : HitType.BLOCKED));
			
			if(firstHit == 0) {
				secondHit = 0;
			}
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					CombatExperience.handleCombatExperience(player, finalDamage, CombatStyle.MELEE);
					targNpc.damage(new Hit(finalDamage, finalDamage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		}
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
		return 2.5;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
