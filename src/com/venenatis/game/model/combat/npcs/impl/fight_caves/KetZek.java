package com.venenatis.game.model.combat.npcs.impl.fight_caves;

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

public class KetZek extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		
		if(!attacker.isNPC()) {
			return;
		}
		CombatStyle style = attacker.getLocation().distanceToEntity(attacker, victim) <= 1 ? CombatStyle.MELEE : CombatStyle.MAGIC;
		NPC npc = (NPC) attacker;
		
		int maxHit = style == CombatStyle.MAGIC ? 48 : 54;
		
		switch (style) {
		case MELEE:
			npc.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
			int randomHit = Utility.random(maxHit);
			
            Hit hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MELEE, false, false);

            Combat.hitEvent(attacker, victim, 1, hitInfo, CombatStyle.MELEE);
			
			break;
		case MAGIC:
			npc.playAnimation(Animation.create(2647));
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
			
			
			npc.playProjectile(Projectile.create(npc.getLocation(), victim, 445, 25, 5, clientSpeed, 43, 36, 10, 48));
			
			randomHit = Utility.random(maxHit);
			
            hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MAGIC, false, false);

            Combat.hitEvent(attacker, victim, delay, hitInfo, CombatStyle.MAGIC);
            
            Server.getTaskScheduler().schedule(new Task(delay) {
				@Override
				public void execute() {
					this.stop();
					victim.playGraphics(Graphic.create(446));
				}
			});
			break;
		default:
			break;
		
		}
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}
	
}
