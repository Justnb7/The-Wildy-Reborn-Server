package com.venenatis.game.model.combat.npcs.impl;

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
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

public class Cave_Kraken extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}

		CombatStyle style = CombatStyle.MAGIC;

		// The npc instance
		NPC npc = (NPC) attacker;

		// Calculate max hit first
		int maxHit = 13;
		final int hit;
		
		Animation anim = Animation.create(npc.getAttackAnimation());
		attacker.playAnimation(anim);

		int randomHit = Utility.random(maxHit);
		hit = randomHit;
		int clientSpeed;
		int gfxDelay;
		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int hitDelay = (gfxDelay / 20) - 1;

		attacker.playAnimation(Animation.create(3992));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));

		// Create the hit instance
		Hit hitInfo = victim.take_hit(attacker, hit, style, false, false);

		// Send the hit task
		Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
		
		Server.getTaskScheduler().schedule(new Task(hitDelay) {
			@Override
			public void execute() {
				victim.playGraphics(Graphic.create(hit > 0 ? 157 : 85, 0, 100));
				this.stop();
			}
		});
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 10;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
