package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatExperience;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

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
			Server.getTaskScheduler().schedule(new Task(1) {

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
			Server.getTaskScheduler().schedule(new Task(1) {

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
