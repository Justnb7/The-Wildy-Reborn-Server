package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;

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
        Hit hitInfo = target.take_hit(player, damage, CombatType.MELEE).giveXP(player);

        Combat.hitEvent(player, target, 1, hitInfo, CombatType.MELEE);
		
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
