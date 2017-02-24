package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;

public class DragonWarhammer implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13576 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());

		player.playAnimation(Animation.create(1378));
		player.playGraphics(Graphic.highGraphic(1292));
		
		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			
			if (damage > 0) {
				int current = targPlayer.getSkills().getLevel(Skills.DEFENCE);
				int decrement = (int)(current * 0.7);
				targPlayer.getSkills().decreaseLevelToZero(Skills.DEFENCE, decrement);
				targPlayer.write(new SendMessagePacket("Your defence has been lowered."));
			}
			if (targPlayer.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				damage = (int) (damage * 0.6);
			}
			if (targPlayer.hasVengeance()) {
				targPlayer.getCombat().vengeance(player, damage, 1);
			}
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
			targPlayer.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
		} else {
			Npc targNpc = (Npc) target;
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			int min = targNpc.getDefinition().getMeleeDefence() * 2 / 3;
			int skill =  targNpc.getDefinition().getMeleeDefence();
			
			if (skill / 3 >= min) {
				skill -= min;
				if (skill < 1) {
					skill = 1;
				}
			}
			CombatExperience.handleCombatExperience(player, damage, CombatType.MELEE);
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
		return 1.20;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.0;
	}

}
