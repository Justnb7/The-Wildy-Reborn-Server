package com.venenatis.game.model.combat.npcs.impl.godwars.saradomin;

import java.util.Random;

import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;

public class CommanderZilyana extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	private long lastMessage;
	
	private static final String[] MESSAGES = {"Death to the enemies of the light!",
		"Slay the evil ones!",
		"Saradomin lend me strength!",
		"By the power of Saradomin!",
		"May Saradomin be my sword.",
		"Good will always triumph!",
		"Forward! Our allies are with us!",
		"Saradomin is with us!",
		"In the name of Saradomin!",
		"Attack! Find the Godsword!",
		"All praise Saradomin!"};

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		if (random.nextInt(3) == 2 && System.currentTimeMillis() - lastMessage > 3000) {
			attacker.sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
			lastMessage = System.currentTimeMillis();
		}
		NPC npc = (NPC) attacker;
		Player pVictim = (Player)victim;
		
		//TODO complete combat
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}
