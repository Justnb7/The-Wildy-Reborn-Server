package com.venenatis.game.model.combat.npcs.impl.godwars.bandos;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class SergeantSteelwill extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
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
		int hitDelay = (gfxDelay / 20) - 1;

		attacker.playAnimation(Animation.create(6154));
		attacker.playGraphics(Graphic.create(1216, 0, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1217, 45, 50, gfxSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		int randomHit = Utility.random(17);
		
		victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
		
		attacker.getCombatState().setAttackDelay(5);
		
	}

	@Override
	public int distance(Entity attacker) {
		return 6;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}
