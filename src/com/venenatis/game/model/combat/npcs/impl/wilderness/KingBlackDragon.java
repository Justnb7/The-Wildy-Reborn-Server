package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.Random;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class KingBlackDragon extends AbstractBossCombat {
	
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
		Player pVictim = (Player) victim;
		int maxHit;
		int damage;
		int randomHit;
		final int hitDelay;
		int fireProjectileId = -1;

		CombatStyle style = CombatStyle.MAGIC;

		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			if (attacker.getRandom().nextInt(10) < 7) {
				style = CombatStyle.MELEE;
			}
		}
		switch(style) {
		default:
		case MELEE:
			Animation anim = Animation.create(npc.getAttackAnimation());
			if(random.nextInt(2) == 1) {
				anim = Animation.create(91);
			}
			attacker.playAnimation(anim);

			hitDelay = 1;
			maxHit = 25;
			damage = Utility.random(maxHit);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > pVictim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = pVictim.getSkills().getLevel(Skills.HITPOINTS);
			}
			break;
		case MAGIC:
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
			hitDelay = (gfxDelay / 25) - 1;
			fireProjectileId = Utility.random(393, 396);
            maxHit = 65;
            switch (fireProjectileId) {
                case 394:
                case 395:
                case 396:
                    boolean dragonfireShield = pVictim.getEquipment() != null && (pVictim.getEquipment().contains(1540) || pVictim.getEquipment().contains(11283) || pVictim.getEquipment().contains(11284) || pVictim.getEquipment().contains(11285));
                    boolean dragonfirePotion = false;
                    if (victim.hasAttribute("antiFire")) {
                        dragonfirePotion = System.currentTimeMillis() - (long)victim.getAttribute("antiFire", 0L) < 360000;
                    }
                    if (dragonfireShield && !dragonfirePotion || (!dragonfireShield && dragonfirePotion)) {
                        maxHit = 10;
                    } else if (dragonfireShield && dragonfirePotion) {
                        maxHit = 0;
                    }
                    break;
            }
			attacker.playAnimation(Animation.create(81));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), fireProjectileId, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = Utility.random(maxHit);

			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if(randomHit > pVictim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = pVictim.getSkills().getLevel(Skills.HITPOINTS);
			}
			if (fireProjectileId == 393) {
				double dragonfireReduction = CombatFormulae.dragonfireReduction(victim);
				if(dragonfireReduction > 0) {
					randomHit -= (randomHit * dragonfireReduction);
					if (randomHit < 0) {
						randomHit = 0;
					}
				}
			}
			break;
		}		

		attacker.getCombatState().setAttackDelay(4);
		final int dmg = randomHit;
		CombatStyle preStyle = style;
		final int fireProjectileId_ = fireProjectileId;
		World.getWorld().schedule(new Task(hitDelay) {
			@Override
			public void execute() {
				victim.take_hit(attacker, dmg, preStyle).send();
				if (fireProjectileId_ != -1) {
					switch (fireProjectileId_) {
					case 393:
						if(victim instanceof Player) {
							//TODO add dfs charges
						}
						break;
					case 394:
						if(victim.getPoisonDamage().get() < 1 && random.nextInt(10) < 7 && !victim.isPoisoned()) {
							victim.poison(PoisonType.DEFAULT_NPC);
						}
						break;
					case 395:
						if (victim.getRandom().nextInt(10) < 7) {
							if(!victim.frozen()) {
								int freezeTimer = 25;
								int finalTimer = freezeTimer;
								if(pVictim.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
									finalTimer = freezeTimer / 2;
								}
								
								pVictim.getWalkingQueue().reset();
								if(victim.getActionSender() != null) {
									victim.getActionSender().sendMessage("You have been frozen!");
									victim.freeze(finalTimer);
								}
							}
						}
						break;
					case 396:
						if (victim.getRandom().nextInt(10) < 3) {
							Player player = (Player) victim;
							player.getSkills().decreaseLevelToZero(player.getRandom().nextInt(3), 5);
							player.getActionSender().sendMessage("You have been shocked.");
						}
						break;
					}
				}
				this.stop();
			}			
		});
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12653));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Prince black dragon pet.", false);
			} else {
				Pets pets = Pets.PRINCE_BLACK_DRAGON;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Prince black dragon pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
		
	}
	
	@Override
	public int distance(Entity attacker) {
		return 3;
	}

}
