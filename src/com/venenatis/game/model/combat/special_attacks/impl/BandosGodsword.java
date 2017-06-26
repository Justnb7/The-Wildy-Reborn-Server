package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class BandosGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11804 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		
		player.playAnimation(Animation.create(7060));
		player.playGraphics(Graphic.create(1212, 0, 0));

		int[] skills = new int[] { Skills.DEFENCE, Skills.STRENGTH, Skills.PRAYER, Skills.ATTACK, Skills.MAGIC, Skills.RANGE };

		int newDmg = damage / 10;
		if (target instanceof Player) {
			Player targPlayer = (Player) target;
			
			if (!(CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier()))) {
				damage = 0;
			}
			
			for (int i = 0; i < skills.length; i++) {
				if (newDmg > 0) {
					if (targPlayer.getSkills().getLevel(skills[i]) > 0) {
						int before = targPlayer.getSkills().getLevel(skills[i]);
						targPlayer.getSkills().decreaseLevelToZero(skills[i], newDmg);
						int after = before - targPlayer.getSkills().getLevel(skills[i]);
						newDmg -= after;
					}
				} else {
					break;
				}
			}
		} else {
			NPC targNpc = (NPC) target;
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
		}
		// Set up a Hit instance
        Hit hitInfo = target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player);

        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MELEE);
	}

	@Override
	public int amountRequired() {
		return 60;
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
