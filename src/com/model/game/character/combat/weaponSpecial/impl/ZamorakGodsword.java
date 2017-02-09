package com.model.game.character.combat.weaponSpecial.impl;

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
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;

public class ZamorakGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11808 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());
		int freezeTimer = 30;
		
		player.playAnimation(Animation.create(7057));
		player.playGraphics(Graphic.create(1210, 0, 0));
		
		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			
			targPlayer.playGraphics(Graphic.create(369, 0, 0));
			
			if (targPlayer.freezeTimer <= 0) {
				/*
				 * If the enemy has protect from magic, freeze time is halved.
				 */
				if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MAGIC)) {
					targPlayer.freezeTimer = freezeTimer / 2;
				} else {
					targPlayer.freezeTimer = freezeTimer;
				}
				System.out.println("freezetimer: "+freezeTimer+ " player frozen timer: "+targPlayer.freezeTimer);
				targPlayer.write(new SendMessagePacket("You have been frozen"));
				targPlayer.frozenBy = player.getIndex();
				targPlayer.stopMovement();
			}
			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				damage = (int) (damage * 0.6);
			}
			if (targPlayer.hasVengeance()) {
				targPlayer.getCombat().vengeance(player, damage, 1);
			}
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targPlayer.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		} else {
			Npc targNpc = (Npc) target;
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			
			targNpc.playGraphics(Graphic.create(369, 0, 0));
			
			if (targNpc.freezeTimer <= 0) {
				targNpc.freezeTimer = 30;
			}
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targNpc.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		}
	}

	@Override
	public int amountRequired() {
		return 55;
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