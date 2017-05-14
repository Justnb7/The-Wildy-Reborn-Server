package com.model.game.character.combat.npcs.script;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Kraken extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}

		CombatStyle style = CombatStyle.MAGIC;

		// The npc instance
		NPC npc = (NPC) attacker;

		// Calculate max hit first
		int maxHit = 28;
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
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 162, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));

		// Create the hit instance
		Hit hitInfo = victim.take_hit(attacker, hit, style, false, false);

		// Send the hit task
		Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
			@Override
			public void execute() {
				victim.playGraphics(Graphic.create(hit > 0 ? 163 : 85, 0, 100));
				this.stop();
			}
		});
		
		((NPC) attacker).attackTimer = 6;
	}

	@Override
	public int distance(Entity attacker) {
		return 10;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {

	}

}
