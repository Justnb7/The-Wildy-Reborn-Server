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

public class KetZek extends AbstractBossCombat {

	public KetZek(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Entity attacker, Entity victim) {
		
		if(!attacker.isNPC()) {
			return;
		}
		CombatType style = attacker.getPosition().distanceToEntity(attacker, victim) <= 1 ? CombatType.MELEE : CombatType.MAGIC;
		NPC npc = (NPC) attacker;
		
		int maxHit = style == CombatType.MAGIC ? 48 : 54;
		
		switch (style) {
		case MELEE:
			npc.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
			int randomHit = Utility.random(maxHit);
			
            Hit hitInfo = victim.take_hit(attacker, randomHit, CombatType.MELEE, false);

            Combat.hitEvent(attacker, victim, 1, hitInfo, CombatType.MELEE);
			
			break;
		case MAGIC:
			npc.playAnimation(Animation.create(2647));
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
			
			
			npc.playProjectile(Projectile.create(npc.getPosition(), victim, 
					445, 25, 5, clientSpeed, 43, 36, 10, 48));
			// its either angle or radius thats wrong, so is it wrong in the packet then with converting
			//cuz i literaly tested on the hyperion and it shoots right? yh
			
			randomHit = Utility.random(maxHit);
			
            hitInfo = victim.take_hit(attacker, randomHit, CombatType.MAGIC, false);

            Combat.hitEvent(attacker, victim, delay, hitInfo, CombatType.MAGIC);
            
            Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
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
		((NPC)attacker).attackTimer = (style == CombatType.MAGIC ? 5 : 4);
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

}
