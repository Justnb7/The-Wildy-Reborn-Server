package com.model.game.character.combat.npcs.script.wilderness;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Chaos_Elemental extends AbstractBossCombat {
	
	

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		Player player = (Player)victim;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int randomHit;
		int hitDelay;
		int clientSpeed;
		int gfxDelay;
		int preHit = 0;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(Utility.random(4)) {
			default:
				style = CombatStyle.MAGIC;	
				break;
			case 1:
				style = CombatStyle.RANGE;
				break;
			case 2:
				style = CombatStyle.TELEOTHER;
				break;
			case 3:
				style = CombatStyle.DISARM;
				break;
			}
		}
		
		switch(style) {
		case DISARM:
			attacker.playAnimation(Animation.create(3146));
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
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 551, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			break;
		case TELEOTHER:
			attacker.playAnimation(Animation.create(3146));
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
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 554, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			break;
		case RANGE:
			maxHit = 28;
			attacker.playAnimation(Animation.create(3146));
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
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 557, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			if(player.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
				maxHit = 8;
			}
			randomHit = Utility.random(maxHit);
			if(randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		case MAGIC:
			maxHit = 28;
			attacker.playAnimation(Animation.create(3146));
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
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 557, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			if(player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 7;
			}
			randomHit = Utility.random(maxHit);
			if(randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		default:
			preHit = 0;
			hitDelay = 1;
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
		final int dmg = preHit;
		CombatStyle preStyle = style;
		World.getWorld().schedule(new ScheduledTask(hitDelay) {
			@Override
			public void execute() {
				this.stop();
				victim.playGraphics(breakGraphic(preStyle));
				switch(preStyle) {
				default:
				case MAGIC:
				case RANGE:
					Hit hitInfo = victim.take_hit(attacker, dmg, preStyle, false, false);
			        Combat.hitEvent(attacker, victim, 1, hitInfo, preStyle);
					break;
				case TELEOTHER:
				case DISARM:
					applyEffect(victim, preStyle);
					break;
				}
			}			
		});
	}

	@Override
	public int distance(Entity attacker) {
		return 7;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}
	
	private Graphic breakGraphic(CombatStyle style) {
		int id;
		switch(style) {
		default:
		case MAGIC:
		case RANGE:
			id = 558;
			break;
		case TELEOTHER:
			id = 555;
			break;
		case DISARM:
			id = 552;
			break;
		}
		return Graphic.create(id, 0, 100);
	}
	
	private void applyEffect(final Entity victim, CombatStyle style) {
		Player p = (Player)victim;
		switch(style) {
		case TELEOTHER:
			if(victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("The fiend teleports you away.");
			}
			World.getWorld().schedule(new ScheduledTask(1) {
				public void execute() {
					this.stop();
					victim.setTeleportTarget(generateLocation());	
				}
			});
			break;
		case DISARM:
			if(victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("The fiend attempts to disarm you.");
			}
			int slot = 0;
			if (p.getEquipment().getTakenSlots() == 0) {
				return;
			}
			while (p.getEquipment().get(slot) == null) {
				slot++;
			}
			if (p.getInventory().getFreeSlots() != 0) {
				p.getEquipment().unequip(slot);
			} else {
				return;
			}
			p.playGraphics(Graphic.create(557));
			p.getActionSender().sendMessage("The Chaos Elemental has removed some of your worn equipment.");
			break;
		default:
			break;
		
		}
	}
	
	private Location generateLocation() {
		Location loc = Location.create(3230 + Utility.random(3), 3917 + Utility.random(3));
		if(Utility.random(1) > 0) {
			loc = Location.create(3275 + Utility.random(2), 3912 + Utility.random(2));
		}
		return loc;
	}

}
