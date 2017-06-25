package com.venenatis.game.model.combat.npcs.impl.dagannoths;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

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
			Hit hitInfo = victim.take_hit(attacker, hit, style, false, false);

			// Send the hit task
			Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(6);
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
