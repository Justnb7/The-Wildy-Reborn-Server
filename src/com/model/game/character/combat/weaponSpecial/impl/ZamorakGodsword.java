package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class ZamorakGodsword implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11808 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		
		player.playAnimation(Animation.create(7057));
		player.playGraphics(Graphic.create(1210, 0, 0));
			
		target.playGraphics(Graphic.create(369, 0, 0));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		
		target.freeze(33);
		target.frozenBy(player);
		
		/*if (target.isPlayer() && ((Player) target).getPrayers()[Prayer.PROTECT_FROM_MAGIC.ordinal()]) {
			// TODO int spellFeezeTime = 15; // f2p spell freeze time
			//target.freeze(spellFreezeTime);
		}*/
		
		// Set up a Hit instance
        Hit hitInfo = target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player);

        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MELEE);
			
	}

	@Override
	public int amountRequired() {
		return 55;
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