package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder.Direction;

public class ChaosFanatic extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	private static final Animation ATTACK_ANIMATION = Animation.create(811);

	private static final int RED_GFX = 554;

	private static final String[] MESSAGES = { "BURN!", "WEUGH!", "Develish Oxen Roll!",
			"All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH",
			"I shall call him squidgy and he shall be my squidgy!" };

	@Override
	public void execute(final Entity attacker, final Entity victim) {
		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		Player pVictim = (Player) victim;
		
		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.MAGIC;

		int maxHit;
		int randomHit;
		int hitDelay;
		int clientSpeed;
		int gfxDelay;
		int preHit = 0;

		/*switch (Utility.random(3)) {
		default:
			style = CombatStyle.MAGIC;
			break;
		case 3:
			style = CombatStyle.GREEN_BOMB;
			break;
		}*/
		style = CombatStyle.GREEN_BOMB;
		
		Location firstLocation = pVictim.getLocation().clone();
		Location secondLocation = pVictim.getLocation().clone().add(Direction.SOUTH_EAST);
		Location thirdLocation = pVictim.getLocation().clone().add(Direction.SOUTH_WEST);

		attacker.sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);

		switch (style) {
		case MAGIC:
			maxHit = 31;
			attacker.playAnimation(ATTACK_ANIMATION);
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), RED_GFX,
					45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			if (pVictim.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 7;
			}
			randomHit = Utility.random(maxHit);
			if (randomHit > pVictim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = pVictim.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		case GREEN_BOMB:
			maxHit = 31;
			attacker.playAnimation(ATTACK_ANIMATION);
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			//TODO fix projectile sending, projectile sending for this particular MOB is off, there a couple of more mobs that use a simliar attack
			//We need to fix this in order to write those scripts aswell!!!!!
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), firstLocation, 551, 45, 50, 70, 43, 35, 0, 10, 10));
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), secondLocation, 551, 45, 50, clientSpeed, 43, 35, 0, 10, 48));
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), thirdLocation, 551, 45, 50, clientSpeed, 43, 35, 0, 10, 48));
			
			break;
		default:
			preHit = 0;
			hitDelay = 1;
			break;

		}

		attacker.getCombatState().setAttackDelay(5);
		final int dmg = preHit;
		CombatStyle preStyle = style;
		World.getWorld().schedule(new Task(hitDelay) {
			@Override
			public void execute() {
				this.stop();
				boolean doDamage = true;
				switch (preStyle) {
				default:
					
				case MAGIC:
					victim.take_hit(attacker, dmg, preStyle).send();
					break;

				case GREEN_BOMB:
					List<Player> enemies = new ArrayList<>();
					for (Player p : victim.getLocalPlayers()) {
						if (p.getLocation().equals(p.getLocation()) || p.getLocation().equals(secondLocation)
								|| p.getLocation().equals(thirdLocation)) {
							if (p == victim) {
								continue;
							}
							enemies.add(p);
						}
					}
					pVictim.getActionSender().stillGfx(157, firstLocation.getX(), firstLocation.getY(), pVictim.getZ(), 5);
					pVictim.getActionSender().stillGfx(157, secondLocation.getX(), secondLocation.getY(), pVictim.getZ(), 5);
					pVictim.getActionSender().stillGfx(157, thirdLocation.getX(), thirdLocation.getY(), pVictim.getZ(), 5);

					if (!victim.getLocation().equals(firstLocation) && !victim.getLocation().equals(secondLocation) && !victim.getLocation().equals(thirdLocation)) {
						doDamage = false;
					}
					if (doDamage) {
						victim.take_hit(attacker, dmg, preStyle).send();
					}
					for (Player p : enemies) {
						if(doDamage)
						p.take_hit(attacker, dmg, preStyle).send();
					}
					enemies.clear();
					break;
				}
			}
		});
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

		Pets pets = Pets.CHAOS_ELEMENTAL;
		Pet pet = new Pet(player, pets.getNpc());
		if (player.alreadyHasPet(player, 11995) || player.getPet() == pets.getNpc()) {
			return;
		}
		
		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(11995));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Chaos elemental pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Chaos elemental pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
}