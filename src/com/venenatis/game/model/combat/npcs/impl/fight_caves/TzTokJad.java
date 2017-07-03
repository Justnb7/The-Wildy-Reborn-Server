package com.venenatis.game.model.combat.npcs.impl.fight_caves;

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

public class TzTokJad extends AbstractBossCombat {

	/**
	 * The ranged animation.
	 */
	private static final Animation RANGE_ANIMATION = Animation.create(2652);

	/**
	 * The range gfx.
	 */
	private static final Graphic RANGE_GFX = Graphic.create(451, 35);

	/**
	 * The range end gfx.
	 */
	private static final Graphic RANGE_END_GFX = Graphic.create(157);

	/**
	 * The melee animation.
	 */
	private static final Animation MELEE_ANIMATION = Animation.create(2655);

	/**
	 * The magic animation.
	 */
	private static final Animation MAGIC_ANIMATION = Animation.create(2656);

	/**
	 * The magic gfx.
	 */
	private static final Graphic MAGIC_GFX = Graphic.create(448);

	@Override
	public void execute(Entity attacker, final Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
        
        NPC npc = (NPC) attacker;
        CombatStyle style = attacker.getLocation().distance(victim.getLocation()) <= 2 ? CombatStyle.MELEE : Utility.random(9) <= 4 ? CombatStyle.MAGIC : CombatStyle.RANGE;
		int maxHit = 97;
		
		switch (style) {
		case MELEE:
			npc.playAnimation(MELEE_ANIMATION);
			
			int randomHit = Utility.getRandom(maxHit);
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE, false, false).send();
			break;
		case RANGE:
			npc.playAnimation(RANGE_ANIMATION);
			victim.playGraphics(RANGE_GFX);
			
			randomHit = Utility.getRandom(maxHit);
			
			// Special case: the player actually has 2 ticks where they can change pray before getting smacked
			Server.getTaskScheduler().schedule(new Task(2) {
				@Override
				public void execute() {
					this.stop();
					victim.take_hit(attacker, randomHit, CombatStyle.RANGE, false, false).send();
				}
			});
			
            Server.getTaskScheduler().schedule(new Task(3) {
				@Override
				public void execute() {
					this.stop();
					victim.playGraphics(RANGE_END_GFX);
				}
			});
			break;
			
		default:
			npc.playAnimation(MAGIC_ANIMATION);
			
			randomHit = Utility.getRandom(maxHit);
			
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
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, MAGIC_GFX.getId(), 25, 50, clientSpeed, 110, 36, 10, 48));
			
			// Special case: the player actually has 2 ticks where they can change pray before getting smacked
			Server.getTaskScheduler().schedule(new Task(2) {
				@Override
				public void execute() {
					this.stop();
					victim.take_hit(attacker, randomHit, CombatStyle.MAGIC, false, false).send(delay-2);
				}
			});

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