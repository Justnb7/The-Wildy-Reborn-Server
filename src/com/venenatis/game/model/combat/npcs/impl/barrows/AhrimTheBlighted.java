package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class AhrimTheBlighted extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
		
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
		attacker.playAnimation(Animation.create(727));
		attacker.playGraphic(Graphic.create(155, 0, 0));
		attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 31, victim.getProjectileLockonIndex(), 0, 36));
		int randomHit = Utility.random(25);
		victim.playGraphic(randomHit <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
		
		victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(delay);
         
        attacker.getCombatState().setAttackDelay(5);
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
