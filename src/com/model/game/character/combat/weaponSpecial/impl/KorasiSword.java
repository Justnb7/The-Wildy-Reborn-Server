package com.model.game.character.combat.weaponSpecial.impl;

import java.util.Random;

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
import com.model.utility.Utility;

public class KorasiSword implements SpecialAttack {
	
	private static Random r = new Random();

	@Override
	public int[] weapons() {
		return new int[] { 19780 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(70);
		player.playAnimation(Animation.create(1058));
		
		if(player instanceof Player) {
			
			Player targPlayer = (Player) target;
			
			if (damage > 70) {
				damage = 70;
			}
			
			targPlayer.playGraphics(Graphic.create(1213));
			
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			
			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MAGIC)) {
				damage = (int)damage * 5 / 10;
			}
			
			if (targPlayer.hasVengeance()) {
				targPlayer.getCombat().vengeance(player, damage, 1);
			}
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targPlayer.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		} else {
			Npc targNpc = (Npc) target;
			
			targNpc.playGraphics(Graphic.create(1213));
			if (!(CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
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
	public boolean meetsRequirements(Player player, Entity victim) {
		return true;
	}
	
	@Override
	public double getAccuracyMultiplier() {
		return 8.0;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.0;
	}
}