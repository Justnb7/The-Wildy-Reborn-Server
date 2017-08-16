package com.venenatis.game.model.combat.npcs.impl.wilderness;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class ZombiesChampion extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
        
        NPC npc = (NPC) attacker;
        CombatStyle style = attacker.getLocation().distance(victim.getLocation()) <= 2 ? CombatStyle.MELEE : Utility.random(9) <= 4 ? CombatStyle.MAGIC : CombatStyle.RANGE;
        int maxHit = 45;
        int clientSpeed;
		int gfxDelay;
		npc.playAnimation(Animation.create(npc.getAttackAnimation()));
        
		switch (style) {
        case MELEE:
			int randomHit = Utility.getRandom(maxHit);
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send();
			break;
		case RANGE:
			randomHit = Utility.random(maxHit);
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
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 856, 25, 50, clientSpeed, 43, 36, 10, 48));
			victim.getActionSender().stillGfx(755, victim.getLocation());
			victim.take_hit(attacker, randomHit, CombatStyle.RANGE).send(delay);
			break;
		case MAGIC:
			randomHit = Utility.random(maxHit);
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
			delay = (gfxDelay / 20) - 1;
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 782, 25, 50, clientSpeed, 43, 36, 10, 48));
			victim.getActionSender().stillGfx(783, victim.getLocation());
			victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(delay);
			break;
		default:
			break;
        }
		
		attacker.getCombatState().setAttackDelay(5);
	}

	@Override
	public int distance(Entity attacker) {
		return 10;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		//TODO add custom pet
	}

}
