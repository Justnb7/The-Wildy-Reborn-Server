package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class ToxicBlowpipe implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 12926 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateRangeMaxHit());
		player.playAnimation(Animation.create(5061));

		//TODO implement gfx 1043
		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			targPlayer.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		} else {
			NPC targNpc = (NPC) target;
			
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			targNpc.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
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
		return 1.50;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}
