package com.venenatis.game.model.combat.magic.lunar;

import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

public class CombatSpells {
	
	public static void vengeance(Player venger, Entity entity_attacker, final int damage, int delay) {
		/*
		 * Minimum hit required
		 */
		if (damage < 2 || !venger.hasVengeance()) {
			return;
		}
		venger.sendForcedMessage("Taste vengeance!");
		venger.setVengeance(false);
		entity_attacker.take_hit(venger, (int)(damage*.75), null).send(delay); // no combat xp given from veng damage
	}

}
