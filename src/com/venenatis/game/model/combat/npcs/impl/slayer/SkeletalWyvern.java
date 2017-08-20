package com.venenatis.game.model.combat.npcs.impl.slayer;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.CombatFormulae;
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

import java.util.Random;

public class SkeletalWyvern extends AbstractBossCombat {

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.MAGIC;

		boolean projectile = attacker.getLocation().distance(victim.getLocation()) > 1;
		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch (random.nextInt(3)) {
			case 0:
			case 1:
				style = CombatStyle.MELEE;
				break;
			case 2:
				style = CombatStyle.MAGIC;
				break;
			}
		}

		// Calculate max hit first
		int maxHit = style == CombatStyle.MAGIC ? 50 : 13;
		Animation anim = Animation.create(npc.getAttackAnimation());
		switch (style) {
		case MELEE:
			attacker.playAnimation(anim);

			int randomHit = Utility.random(maxHit);

			// Create the hit instance
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send();
			break;

		case MAGIC:
			int clientSpeed;
			int gfxDelay;
			attacker.playAnimation(anim);
			attacker.playGraphics(Graphic.create(499, 0, 100));
			if (projectile) {
				if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
					clientSpeed = 50;
					gfxDelay = 60;
				} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
					clientSpeed = 70;
					gfxDelay = 80;
				} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
					clientSpeed = 90;
					gfxDelay = 100;
				} else {
					clientSpeed = 110;
					gfxDelay = 120;
				}
				int hitDelay = (gfxDelay / 20) - 1;
				attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 500,
						45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));

				// Calculate max hit first
				randomHit = Utility.random(maxHit);

				double dragonfireReduction = CombatFormulae.dragonfireReduction(victim);
				if (dragonfireReduction > 0) {
					randomHit -= (randomHit * dragonfireReduction);
					if (randomHit < 0) {
						randomHit = 0;
					}
				}

				// Create the hit instance
				victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);

				Server.getTaskScheduler().schedule(new Task(hitDelay) {
					@Override
					public void execute() {
						if (projectile) {
							victim.playGraphics(Graphic.create(502, 0, 100));
							this.stop();
						}
					}
				});
			}

			break;

		default:
			break;

		}

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