package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.content.achievements.Achievements;
import com.venenatis.game.content.achievements.Achievements.Achievement;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class ArmadylGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11802, 20593, 20368 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());

		player.playAnimation(Animation.create(7061));
		player.playGraphic(Graphic.highGraphic(1211));
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send();
        if (damage >= 75 && target.isPlayer()) {
        	player.unlockRngGod(true);
			Achievements.activate(player, Achievement.TOO_OP, 1);
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
		return 3.0;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.375;
	}

}
