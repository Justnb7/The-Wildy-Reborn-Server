package com.venenatis.game.model.combat.combat_effects;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class DragonfireShield {
	
	public static void charge(final Player player) {
		//Your dragonfire shield is already fulled charged.
	}
	
	public static void dfsSpec(final Player player, final Entity victim) {
		
		if(player == null || victim == null) {
			return;
		}
		
		if (victim instanceof NPC) {
			NPC n = (NPC) victim;
			if (n.getHitpoints() < 1) {
				return;
			}
		} else {
			if (player.getSkills().getLevel(Skills.HITPOINTS) < 1) {
				return;
			}
		}
		
		if(victim.getCombatState().getTarget() != null) {
			player.getActionSender().sendMessage("You must be in combat to operate your dragonfire shield.");
			return;
		}
		
		if(player.getDfsTimer() > 0) {
			player.getActionSender().sendMessage("You must let the shield cool down before using it again.");
			return;
		}
		player.setDfsTimer(50);
		player.getCombatState().setAttackDelay(6);
		player.face(victim.getLocation());
		player.playAnimation(Animation.create(6696));
		player.playGraphic(Graphic.create(1165));
		World.getWorld().schedule(new Task(3) {
			public void execute() {
				int hitDelay;
				int clientSpeed;
				int gfxDelay;
				if(player.getLocation().isWithinDistance(player, victim, 1)) {
					clientSpeed = 70;
					gfxDelay = 80;
				} else if(player.getLocation().isWithinDistance(player, victim, 5)) {
					clientSpeed = 90;
					gfxDelay = 100;
				} else if(player.getLocation().isWithinDistance(player, victim, 8)) {
					clientSpeed = 110;
					gfxDelay = 120;
				} else {
					clientSpeed = 130;
					gfxDelay = 140;
				}
				hitDelay = (gfxDelay / 20) - 1;
				
				player.playProjectile(Projectile.create(player.getCentreLocation(), victim.getCentreLocation(), 1166, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
				
				victim.playGraphic(Graphic.create(1167, 0, 100));
				int hit = Utility.random(25);
				victim.take_hit(player, hit, null).send(hitDelay);//2 or hitDelay?
				stop();
			}
		});
	}

}
