package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.Collection;
import java.util.Random;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class Venenatis extends AbstractBossCombat {

	private static final Animation MELEE_ANIMATION = Animation.create(5327);

	private static final Animation MAGIC_ANIMATION = Animation.create(5330);

	private static final Graphic EARTH_BLAST_START = Graphic.create(164, 0, 100);

	private static final int EARTH_BLAST_PROJECTILE = 165;
	
	private static final int SPIDER_WEB_PROJECTILE = 1254;
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}
		
		CombatStyle style = CombatStyle.MELEE;
		
		int hitDelay;
		int maxHit = 50;
		int randomHit;
		int attackDelay = 6;
		int spellDelay = 7;

		switch (Utility.random(7)) {
			case 0:
			case 1:
			case 2:
				style = CombatStyle.MAGIC;
				break;
			case 5:
				style = CombatStyle.DRAIN_PRAYER;
				break;
			case 3:
			case 4:
				style = CombatStyle.WEB;
				break;
			case 6:
			case 7:
				style = CombatStyle.MELEE;
		}

		switch (style) {
		case MELEE:
			attacker.playAnimation(MELEE_ANIMATION);
			randomHit = Utility.random(maxHit);
			victim.take_hit(attacker, randomHit, style).send(1);
			break;
		case MAGIC:
			attacker.playAnimation(MAGIC_ANIMATION);
			attacker.playGraphic(EARTH_BLAST_START);
			
			final Collection<Player> localPlayers = RegionStoreManager.get().getLocalPlayers(attacker);
			
			for(final Player near : localPlayers) {
				if(near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
						// Set the projectile speed based on distance
						
						int rClientSpeed;
						int rGfxDelay;
						if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
							rClientSpeed = 70;
							rGfxDelay = 80;
						} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
							rClientSpeed = 90;
							rGfxDelay = 100;
						} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
							rClientSpeed = 110;
							rGfxDelay = 120;
						} else {
							rClientSpeed = 130;
							rGfxDelay = 140;
						}
						hitDelay = (rGfxDelay / 20) - 1;
						
						// Send the projectile
						attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), EARTH_BLAST_PROJECTILE, 45, 50, rClientSpeed, 35, 35, near.getProjectileLockonIndex(), 10, 48));
						
						// Calculate max hit first
						randomHit = Utility.random(maxHit);
						
						near.playGraphic(Graphic.create(166, rGfxDelay, 100));
						
						// Create the hit instance
						near.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
					}
				}
			}
			break;
		case DRAIN_PRAYER:
			attacker.playAnimation(MAGIC_ANIMATION);

			final Collection<Player> targets = RegionStoreManager.get().getLocalPlayers(attacker);
			
			for (final Player near : targets) {
				if (near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					// Calculate max hit first
					randomHit = Utility.random(maxHit);
					

					near.getActionSender().sendMessage("Your prayer was drained!");
					near.getSkills().setLevel(Skills.PRAYER, (int) (near.getSkills().getLevel(Skills.PRAYER) * .3));

					near.playGraphic(Graphic.create(172, 1, 100));

					// Create the hit instance
					near.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(1);
				}
			}
			break;
			
		case WEB:
			attacker.playAnimation(MAGIC_ANIMATION);
			randomHit = Utility.random(maxHit);
			int rClientSpeed;
			int rGfxDelay;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				rClientSpeed = 70;
				rGfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				rClientSpeed = 90;
				rGfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				rClientSpeed = 110;
				rGfxDelay = 120;
			} else {
				rClientSpeed = 130;
				rGfxDelay = 140;
			}
			hitDelay = (rGfxDelay / 20) - 1;
			
			// Send the projectile
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), SPIDER_WEB_PROJECTILE, 45, 50, rClientSpeed, 35, 35, victim.getProjectileLockonIndex(), 10, 48));
			
			int sentTwice = random.nextInt(10);
			if(sentTwice > 5) {
				//Sometimes venenatis does his special move simultaneously
				victim.take_hit(attacker, randomHit, CombatStyle.MAGIC, false, true).send(hitDelay);
			}
			
			// Create the hit instance
			victim.take_hit(attacker, randomHit, CombatStyle.MAGIC, false, true).send(hitDelay);
			break;
		default:
			break;

		}

		attacker.getCombatState().setAttackDelay(attackDelay);
		attacker.getCombatState().setSpellDelay(spellDelay);

	}

	@Override
	public int distance(Entity attacker) {
		return 6;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);

		Pets pets = Pets.VENENATIS_SPIDERLING;
		Pet pet = new Pet(player, pets.getNpc());
		if (player.alreadyHasPet(player, 13177) || player.getPet() == pets.getNpc()) {
			return;
		}

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(13177));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Venenatis spiderling pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Venenatis spiderling pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}

	}

}
