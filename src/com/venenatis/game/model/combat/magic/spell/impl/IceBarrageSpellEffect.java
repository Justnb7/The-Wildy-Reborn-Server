package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.model.combat.magic.spell.SpellEffect;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the special effect of the ice barrage spell
 * 
 * @author Arithium
 * 
 */
public class IceBarrageSpellEffect implements SpellEffect {

	@Override
	public void handle(Player attacker, Entity victim) {
		victim.freeze(20);
	}

	@Override
	public boolean noEffect(Player attacker, Entity victim) {
		if (victim.frozen() || ((Boolean) victim.getAttribute("frozen_immunity"))) {
			return true;
		}
		return false;
	}

	@Override
	public int spellId() {
		return 12891;
	}
}