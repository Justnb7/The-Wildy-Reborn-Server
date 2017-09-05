package com.venenatis.game.model.combat.npcs.impl.zulrah.attack;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class RangeAndMageZulrah extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}
		
		// The npc instance
		NPC npc = (NPC) attacker;
		
		Player player = (Player)victim;
		
		npc.setFacePlayer(true);
		
		Animation anim = Animation.create(npc.getAttackAnimation());
		attacker.playAnimation(anim);
		
		int randomHit = Utility.getRandom(npc.getDefinition().getMaxHit());
		
		CombatStyle style = CombatStyle.RANGE;
		
		int chance = 1;
		
		if (player != null) {
			if (player.getZulrahEvent().getStage() == 9) {
				chance = 2;
			}
		}
		
		chance = Utility.random(chance);
		
		if (chance < 2) {
			style = CombatStyle.RANGE;
		} else {
			style = CombatStyle.MAGIC;
		}
		
		int gfxSpeed;
		int gfxDelay;
		
		switch (style) {
		case MAGIC:
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
			int hitDelay = (gfxDelay / 20) - 1;
			
			// Send the projectile
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1046, 45, 50, gfxSpeed, 60, 31, victim.getProjectileLockonIndex(), 10, 48));
			
			victim.take_hit(attacker, randomHit, style).send(hitDelay);
			
			attacker.getCombatState().setSpellDelay(4);
			break;
			
		default:
		case RANGE:
			
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
			
			// Send the projectile
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1044, 45, 50, gfxSpeed, 60, 31, victim.getProjectileLockonIndex(), 10, 48));
			
			victim.take_hit(attacker, randomHit, style).send(hitDelay);
			break;
		}
		attacker.getCombatState().setAttackDelay(7);
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}