package com.venenatis.game.model.combat.magic.spell;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.combat.magic.spell.impl.IceBarrageSpellEffect;
import com.venenatis.game.model.combat.magic.spell.impl.IceBlitzSpellEffect;
import com.venenatis.game.model.combat.magic.spell.impl.IceBurstSpellEffect;
import com.venenatis.game.model.combat.magic.spell.impl.IceRushSpellEffect;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

public class SpellHandler {

	private static Map<Integer, SpellEffect> spells = new HashMap<Integer, SpellEffect>();
	
	static {
		SpellEffect effect = new IceBarrageSpellEffect();
		spells.put(effect.spellId(), effect);
		effect = new IceRushSpellEffect();
		spells.put(effect.spellId(), effect);
		effect = new IceBurstSpellEffect();
		spells.put(effect.spellId(), effect);
		effect = new IceBlitzSpellEffect();
		spells.put(effect.spellId(), effect);
		
	}
	
	public static void handleSpellEffect(Player player, Entity victim) {
		int spellId = player.getSpellId() > -1 ? player.getSpellId() : player.getAutocastId() > -1 ? player.getAutocastId() : -1;
		SpellEffect effect = forId(spellId);
		if (effect == null) {
			player.debug("return");
			return;
		}
		if (!effect.noEffect(player, victim))
			effect.handle(player, victim);
	}
	
	/**
	 * Returns the spell effect by the id of the spell
	 * @param id
	 * The id of the spell
	 * @return
	 * The spell effect
	 */
	public static SpellEffect forId(int id) {
		return spells.get(id);
	}

}