package com.venenatis.game.model.combat.npcs.impl.dragons;

import java.util.Random;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class BlueDragon extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
        NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int hitDelay;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
			switch(random.nextInt(3)) {
			case 0:
			case 1:
				style = CombatStyle.MELEE;	
				break;
			case 2:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		
		switch (style) {
		case MELEE:
			Animation anim = Animation.create(npc.getAttackAnimation());
			if(random.nextInt(2) == 1) {
				anim = Animation.create(91);
			}
			attacker.playAnimation(anim);

			hitDelay = 1;
			maxHit = Utility.random(10);
			victim.take_hit(attacker, maxHit, style).send(hitDelay);
			break;
		default:
		case MAGIC:
			attacker.playAnimation(Animation.create(81));
			attacker.playAnimation(Animation.create(81));
			attacker.playGraphic(Graphic.create(1));
			maxHit = Utility.random(50);
			
			double dragonfireReduction = CombatFormulae.dragonfireReduction(victim);
			if(dragonfireReduction > 0) {
				maxHit -= (maxHit * dragonfireReduction);
			}
			victim.take_hit(attacker, maxHit, style).send(1);
			attacker.getCombatState().setSpellDelay(5);
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}