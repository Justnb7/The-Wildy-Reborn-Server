package com.venenatis.game.model.combat.npcs.impl.godwars.armadyl;

import java.util.Collection;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class Kreearra extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}

		NPC npc = (NPC) attacker;
		Player pVcitim = (Player)victim;
		final Collection<Player> localPlayers = RegionStoreManager.get().getLocalPlayers(attacker);
		CombatStyle style = CombatStyle.MELEE;

		int maxHit;
		int randomHit;
		int hitDelay;

		switch(random.nextInt(5)) {
		case 0:
			style = CombatStyle.MELEE;	
			break;
		case 1:
		case 2:
			style = CombatStyle.MAGIC;
			break;
		default:
			style = CombatStyle.RANGE;
			break;
		}
		pVcitim.debug("attack style: "+style);
		/*if ((style == CombatStyle.MAGIC || style == CombatStyle.RANGE) && !ProjectilePathFinder.clippedProjectile(attacker, victim)) {
			Following.combatFollow(attacker, victim);
			return;
		}*/
		switch(style) {
		case MELEE:
			maxHit = 26;
			attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
			hitDelay = 1;
			maxHit = npc.getDefinition().getMaxHit();
			randomHit = Utility.random(maxHit);
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(hitDelay);
			break;
			
		case MAGIC:
			maxHit = 21;
			attacker.playAnimation(Animation.create(6978));
			
			for(final Player near : localPlayers) {
				if(near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
						// Set the projectile speed based on distance
						int rClientSpeed;
						int rGfxDelay;
						if(attacker.getLocation().isWithinDistance(attacker, near, 1)) {
							rClientSpeed = 70;
							rGfxDelay = 80;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 5)) {
							rClientSpeed = 90;
							rGfxDelay = 100;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 8)) {
							rClientSpeed = 110;
							rGfxDelay = 120;
						} else {
							rClientSpeed = 130;
							rGfxDelay = 140;
						}
						hitDelay = (rGfxDelay / 20) - 1;
						
						// Send the projectile
						attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1198, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
						
						// Calculate max hit first
						randomHit = Utility.random(maxHit);
						
						near.playGraphic(Graphic.create(1196, rGfxDelay, 100));
						
						// Create the hit instance
						near.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
						
						int pullback = random.nextInt(5);
						
						if(pullback == 2) {
							pushBack((Player) victim, (NPC) attacker);
						}
					}
				}
			}
			break;	
			
		default:
		case RANGE:
			maxHit = 71;
			attacker.playAnimation(Animation.create(6978));
			
			for(final Player near : localPlayers) {
				if(near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
						// Set the projectile speed based on distance
						int rClientSpeed;
						int rGfxDelay;
						if(attacker.getLocation().isWithinDistance(attacker, near, 1)) {
							rClientSpeed = 70;
							rGfxDelay = 80;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 5)) {
							rClientSpeed = 90;
							rGfxDelay = 100;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 8)) {
							rClientSpeed = 110;
							rGfxDelay = 120;
						} else {
							rClientSpeed = 130;
							rGfxDelay = 140;
						}
						hitDelay = (rGfxDelay / 20) - 1;
						
						// Send the projectile
						attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1199, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
						
						// Calculate max hit first
						randomHit = Utility.random(maxHit);
						// Create the hit instance
						near.take_hit(attacker, randomHit, CombatStyle.RANGE).send(hitDelay);
					}
				}
			}
			break;
		}		

		attacker.getCombatState().setAttackDelay(7);
		pVcitim.debug("attk delay: "+attacker.getCombatState().getAttackDelay());
		//attacker.getCombatState().setSpellDelay(4);
	}
	
	private static final void pushBack(Player p, NPC kreeArra) {
		if (p.getRandom().nextInt(11) < 4) {
			return;
		}
		Location l = kreeArra.getLocation().transform(kreeArra.getWidth() >> 1, kreeArra.getZ() >> 1, 0);
		Location delta = l.getDelta(p.getLocation());
		boolean horizontal = (delta.getX() < 0 ? -delta.getX() : delta.getX()) > (delta.getY() < 0 ? -delta.getY() : delta.getY());
		Location loc = p.getLocation();
		if (horizontal) {
			if (delta.getX() < 0) {
				loc = p.getLocation().transform(-1, 0, 0);
			} else {
				loc = p.getLocation().transform(1, 0, 0);
			}
		} else {
			if (delta.getY() < 0) {
				loc = p.getLocation().transform(0, -1, 0);
			} else {
				loc = p.getLocation().transform(0, 1, 0);
			}
		}
		//if (PrimitivePathFinder.canMove(p.getLocation(), Directions.directionFor(p.getLocation(), loc))) {
			p.setTeleportTarget(loc);
		//}
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}
