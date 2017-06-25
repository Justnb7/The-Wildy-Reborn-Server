package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class KorasiSword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 19780 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(70);
		player.playAnimation(Animation.create(1058));
		target.playGraphics(Graphic.create(1213));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// Set up a Hit instance
        Hit hitInfo = target.take_hit(player, damage, CombatStyle.MAGIC).giveXP(player);

        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MAGIC);
	}

	@Override
	public int amountRequired() {
		return 60;
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