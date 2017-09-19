package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.Random;

import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;

public class CrazyArchaeologist extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	private static final String[] MESSAGES = { "I'm Bellock - respect me!", "Get off my site!",
			"No-one messes with Bellock's dig!", "These ruins are mine!", "Taste my knowledge!",
			"You belong in a museum!", };
	
	private static final String SPECIAL_MESSAGE = "Rain of knowledge!";
	
	private static final String DEATH_MESSAGE = "Ow!";

	@Override
	public void execute(final Entity attacker, final Entity victim) {

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		int maxHit = 15;
		int randomHit;
		final int hit;
		int clientSpeed;
		int gfxDelay;
		int preHit = 0;
		((NPC) attacker).sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
		
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 6;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}
	
	

}
