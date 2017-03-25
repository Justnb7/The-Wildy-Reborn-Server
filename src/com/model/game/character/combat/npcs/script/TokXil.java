package com.model.game.character.combat.npcs.script;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.utility.Utility;

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
		
		switch (style) {
		case MELEE:
			npc.playAnimation(Animation.create(npc.getAttackAnimation()));

			int randomHit = Utility.random(maxHit);

			Hit hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MELEE, false);

			Combat.hitEvent(attacker, victim, 1, hitInfo, CombatStyle.MELEE);

			break;
			
		case RANGE:
			npc.playAnimation(Animation.create(2633));
			randomHit = Utility.random(maxHit);
			int clientSpeed;
			int gfxDelay;
			if(attacker.getPosition().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getPosition().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getPosition().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			int delay = (gfxDelay / 20) - 1;
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 443, 25, 50, clientSpeed, 43, 36, 10, 48));
			
			hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.RANGE, false);

            Combat.hitEvent(attacker, victim, delay, hitInfo, CombatStyle.RANGE);
			break;
		default:
			break;
		}
		((NPC)attacker).attackTimer = 6;
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

}
