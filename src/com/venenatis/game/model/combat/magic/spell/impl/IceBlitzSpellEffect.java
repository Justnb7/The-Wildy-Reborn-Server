package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.model.combat.magic.spell.SpellEffect;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

public class IceBlitzSpellEffect implements SpellEffect {

	@Override
	public void handle(Player attacker, Entity victim) {
		victim.freeze(25);
	}

	@Override
	public boolean noEffect(Player attacker, Entity victim) {
		return victim.frozen();
	}

	@Override
	public int spellId() {
		return 43;
	}
}