package com.venenatis.game.model.combat.npcs.impl.godwars.zamorak;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class ZaklnGritch extends AbstractBossCombat {
	
	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		//The npc instance
		NPC npc = (NPC) attacker;
		
		int maxHit = 21;
		int randomHit;
		int hitDelay;
		final int hit;
		
		int gfxSpeed;
		int gfxDelay;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			gfxSpeed = 70;
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			gfxSpeed = 90;
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			gfxSpeed = 110;
			gfxDelay = 120;
		} else {
			gfxSpeed = 130;
			gfxDelay = 140;
		}
		hitDelay = (gfxDelay / 20) - 1;

		attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
		attacker.playGraphics(Graphic.create(1222, 0, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1223, 45, 50, gfxSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		randomHit = Utility.random(maxHit);
		
		hit = randomHit;
		
		// Create the hit instance
		Hit hitInfo = victim.take_hit(attacker, hit, CombatStyle.RANGE, false, false);

		// Send the hit task
		Combat.hitEvent(attacker, victim, hitDelay, hitInfo, CombatStyle.RANGE);
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
