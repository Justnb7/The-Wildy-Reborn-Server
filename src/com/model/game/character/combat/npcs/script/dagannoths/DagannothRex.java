package com.model.game.character.combat.npcs.script.dagannoths;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.utility.Utility;

public class DagannothRex extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		//The npc instance
		NPC npc = (NPC) attacker;
		
		//The player instance
		Player player = (Player) victim;
		
		//Attack style
		CombatStyle style = CombatStyle.MELEE;
		
		//Are we in attack distance
		if(Utility.getDistance(attacker.getLocation(), victim.getLocation()) > 4) {
			return;
		}
		
		int maxHit;
		int hitDelay;
		int randomHit;
		int hit;
		switch(style) {
		default:
		case MELEE:
			maxHit = npc.getDefinition().getMaxHit();
			hitDelay = 1;
			attacker.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));

			randomHit = Utility.random(maxHit);
			if(randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			
			// Create the hit instance
			Hit hitInfo = victim.take_hit(attacker, hit, style, false);

			// Send the hit task
			Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
			break;
		}		
		
		((NPC)attacker).attackTimer = 6;
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		//TODO pet drop
	}

}
