package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class AbyssalTentacle implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 12006 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {

		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		player.playAnimation(Animation.create(1658));
        target.playGraphics(Graphic.highGraphic(341));
		
		boolean missed = !CombatFormulae.getAccuracy(player, target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send(1);
		
		target.freeze(8);
	}

	@Override
	public int amountRequired(Player player) {
		return 50 * (int) player.getAttribute("vigour");
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1.10;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
