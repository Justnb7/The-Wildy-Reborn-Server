package com.venenatis.game.model.combat.npcs.impl.godwars.saradomin;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Bree extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			gfxDelay = 120;
		} else {
			gfxDelay = 140;
		}
		int delay = (gfxDelay / 20) - 1;
		attacker.playAnimation(Animation.create(7026));
		int newDist = attacker.getLocation().distanceToEntity(attacker, victim);
		attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, 1190, 30, 50, 40 + (newDist * 5), 63, 35, 10, 36));
		int randomHit = Utility.random(16);
		
		attacker.getCombatState().setAttackDelay(5);
		World.getWorld().schedule(new Task(delay) {

			@Override
			public void execute() {
				this.stop();
				victim.take_hit(attacker, randomHit, CombatStyle.RANGE).send(delay);
			}
		});
		
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
