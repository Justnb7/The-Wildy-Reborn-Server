package com.model.game.character.combat.npcs.script;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;

public class TokXil extends AbstractBossCombat {

	public TokXil(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
		CombatStyle style = attacker.getPosition().distanceToEntity(attacker, victim) <= 1 ? CombatStyle.MELEE : CombatStyle.RANGE;
		NPC npc = (NPC) attacker;

		int maxHit = style == CombatStyle.RANGE ? 14 : 13;
		
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

}
