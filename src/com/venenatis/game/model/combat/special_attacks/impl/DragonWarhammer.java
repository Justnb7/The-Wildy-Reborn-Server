package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class DragonWarhammer implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13576 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());

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
				targPlayer.getActionSender().sendMessage("Your defence has been lowered.");
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
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send();
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
