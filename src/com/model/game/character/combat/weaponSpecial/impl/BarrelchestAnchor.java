package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class BarrelchestAnchor implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 10087 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());

		player.playAnimation(Animation.create(5870));
		player.playGraphics(Graphic.highGraphic(1027));

		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player);
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
		return 1.30;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.20;
	}

}
