package com.venenatis.game.model.combat.npcs.impl.slayer;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

public class EnormousTentacle  extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}

		// The npc instance
		NPC npc = (NPC) attacker;

		// Calculate max hit first
		int maxHit = 2;
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
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 162, 45, 48, clientSpeed, 25, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		Server.getTaskScheduler().schedule(new Task(hitDelay) {
			@Override
			public void execute() {
				victim.playGraphic(Graphic.create(hit > 0 ? 163 : 85, 0, 100));
				this.stop();
			}
		});
		victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}
}