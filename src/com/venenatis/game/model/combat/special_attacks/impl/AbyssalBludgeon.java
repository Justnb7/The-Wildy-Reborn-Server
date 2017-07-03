package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

public class AbyssalBludgeon implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 13263 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		
		int damage = (int) ((player.getSkills().getLevelForExperience(Skills.PRAYER) - player.getSkills().getLevel(Skills.PRAYER)) * .5);
		player.playAnimation(Animation.create(3299));
        target.playGraphics(Graphic.create(1284, 0, 0));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send();
		
	}

	@Override
	public int amountRequired() {
		return 50;
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
