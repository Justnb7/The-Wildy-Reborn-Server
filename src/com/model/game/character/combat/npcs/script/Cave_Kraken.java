package com.model.game.character.combat.npcs.script;

import com.model.game.character.Entity;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;

public class Cave_Kraken extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		
	}

	@Override
	public int distance(Entity attacker) {
		return 10;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
