package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

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
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		target.take_hit(player, damage, CombatType.MAGIC).giveXP(player);
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