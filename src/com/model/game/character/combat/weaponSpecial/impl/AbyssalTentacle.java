package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class AbyssalTentacle implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 12006 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {

		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());
		player.playAnimation(Animation.create(1658));
        target.playGraphics(Graphic.highGraphic(341));
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		target.take_hit(player, damage, CombatType.MELEE).giveXP(player);
		
		target.freeze(8);
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
		return 1.10;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
