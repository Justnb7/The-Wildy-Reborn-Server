package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class GraniteMaul implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 4153, 12848 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		player.faceEntity(target);
		int damage = Utility.random(CombatFormulae.calculateMeleeMaxHit(player, target));
		
		player.playAnimation(Animation.create(1667));
		player.playGraphic(Graphic.create(337, 0, 100));

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