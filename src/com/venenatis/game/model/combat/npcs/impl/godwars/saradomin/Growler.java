package com.venenatis.game.model.combat.npcs.impl.godwars.saradomin;

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
import com.venenatis.game.world.World;

public class Growler  extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
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
		attacker.playAnimation(Animation.create(7037));
		attacker.playGraphics(Graphic.create(1182, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1183, 0, 50, clientSpeed, 0, 35, victim.getProjectileLockonIndex(), 10, 48));
		int randomHit = Utility.random(16);
		
		attacker.getCombatState().setAttackDelay(5);
		World.getWorld().schedule(new Task(delay) {

			@Override
			public void execute() {
				this.stop();

				victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(delay);
			}
		});
	}

	@Override
	public int distance(Entity attacker) {
		return 6;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
