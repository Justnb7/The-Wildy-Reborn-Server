package com.venenatis.game.model.combat.npcs.impl.fight_caves;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class TokXil extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
		CombatStyle style = attacker.getLocation().distanceToEntity(attacker, victim) <= 1 ? CombatStyle.MELEE : CombatStyle.RANGE;
		NPC npc = (NPC) attacker;

		int maxHit = style == CombatStyle.RANGE ? 14 : 13;
		
		switch (style) {
		case MELEE:
			npc.playAnimation(Animation.create(npc.getAttackAnimation()));

			int randomHit = Utility.random(maxHit);

			Hit hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MELEE, false, false);

			Combat.hitEvent(attacker, victim, 1, hitInfo, CombatStyle.MELEE);

			break;
			
		case RANGE:
			npc.playAnimation(Animation.create(2633));
			randomHit = Utility.random(maxHit);
			int clientSpeed;
			int gfxDelay;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			int delay = (gfxDelay / 20) - 1;
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 443, 25, 50, clientSpeed, 43, 36, 10, 48));
			
			hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.RANGE, false, false);

            Combat.hitEvent(attacker, victim, delay, hitInfo, CombatStyle.RANGE);
			break;
		default:
			break;
		}
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
