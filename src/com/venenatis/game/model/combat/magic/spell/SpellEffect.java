package com.venenatis.game.model.combat.magic.spell;

import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

/**
 * Represents a single spell
 * @author Home
 *
 */
public interface SpellEffect {
	
	public void handle(Player attacker, Entity victim);
	
	public boolean noEffect(Player attacker, Entity victim);
	
	public int spellId();

}