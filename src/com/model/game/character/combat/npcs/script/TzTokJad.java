package com.model.game.character.combat.npcs.script;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class TzTokJad extends AbstractBossCombat {

	public TzTokJad(int npcId) {
		super(npcId);
	}

	/**
	 * The ranged animation.
	 */
	private static final Animation RANGE_ANIMATION = Animation.create(2652);

	/**
	 * The range gfx.
	 */
	private static final Graphic RANGE_GFX = Graphic.create(451);

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
	public void execute(Entity attacker, Entity victim) {

		if(!attacker.isNPC()) {
			return;
		}
		CombatType style = CombatType.MAGIC;
        CombatType[] styles = {CombatType.MAGIC, CombatType.RANGE, CombatType.MELEE};
		NPC npc = (NPC) attacker;
		int maxHit;
        if (attacker.getPosition().distance(victim.getPosition()) <= 2) {
            style = styles[Utility.random(styles.length)];
        } else {
            style = styles[Utility.random(1)];
        }
		style = attacker.getPosition().distance(victim.getPosition()) <= 2 ? CombatType.MELEE : Utility.random(9) <= 4 ? CombatType.MAGIC : CombatType.RANGE;
		int speed = (int) (46 + (attacker.getPosition().distance(victim.getPosition()) * 10));
		switch (style) {
		case MELEE:
			npc.playAnimation(MELEE_ANIMATION);
			maxHit = 98;
			break;
		case RANGE:
			npc.playAnimation(RANGE_ANIMATION);
			Server.getTaskScheduler().schedule(new ScheduledTask(2) {

				@Override
				public void execute() {
					victim.playGraphics(RANGE_GFX);
				}
			});
			maxHit = 97;
			break;
		default:
			npc.playAnimation(MAGIC_ANIMATION);
			maxHit = 97;
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					this.stop();
					npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 448, 25, 5, speed, 43, 36, 10, 48));
				}
				
			});
			
			Server.getTaskScheduler().schedule(new ScheduledTask(2) {

				@Override
				public void execute() {
					victim.playGraphics(MAGIC_GFX);
				}
			});
			break;
		}
		int clientSpeed;
		int gfxDelay;
		if(attacker.getPosition().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(attacker.getPosition().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(attacker.getPosition().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int delay = (gfxDelay / 20) - 1;
		CombatType combat_type = style;
		if (style == CombatType.MELEE) {
			delay = 1;
		}

		int randomHit;

		if (combat_type == CombatType.RANGE) {
			victim.playGraphics(RANGE_END_GFX);
		}
		if (combat_type == CombatType.MELEE) {
			randomHit = Utility.random(maxHit);
		} else if (combat_type == CombatType.RANGE) {
			randomHit = Utility.random(maxHit);
		} else {
			randomHit = Utility.random(maxHit);
		}
		
		Hit hitInfo = victim.take_hit(attacker, randomHit, combat_type, false);

        Combat.hitEvent(attacker, victim, 1, hitInfo, combat_type);
		
		npc.attackTimer = 8;
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

}