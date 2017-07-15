package com.venenatis.game.model.combat.magic.spell;

import com.venenatis.game.model.combat.magic.spell.Spells.Spell;
import com.venenatis.game.model.entity.Entity;

/**
 * 
 * @author Mack
 *
 */
public abstract class AbstractSpellScript {
	
	public abstract void execute(Entity attacker, Entity victim, Spell spell, boolean autocast, int clientSpeed, int gfxDelay, int delay);

}