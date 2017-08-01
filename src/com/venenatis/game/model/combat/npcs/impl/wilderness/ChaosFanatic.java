package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
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

		Player player = (Player) victim;

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
		
		Location firstLocation = victim.getLocation().clone();
		Location secondLocation = victim.getLocation().clone().add(Direction.SOUTH_EAST);
		Location thirdLocation = victim.getLocation().clone().add(Direction.SOUTH_WEST);

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
			if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 7;
			}
			randomHit = Utility.random(maxHit);
			if (randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
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
			player.debug(String.format("First location: %s%n ", firstLocation.toString()));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), firstLocation, 551, 45, 50, 70, 43, 35, 0, 10, 10));
			//attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), firstLocation, 551, 45, 50,
					//clientSpeed, 43, 35, 0, 10, 48));
			//attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), secondLocation, 551, 45, 50,
					//clientSpeed, 43, 35, 0, 10, 48));
			//attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), thirdLocation, 551, 45, 50,
					//clientSpeed, 43, 35, 0, 10, 48));
			
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
						if (p.getLocation().equals(firstLocation) || p.getLocation().equals(secondLocation)
								|| p.getLocation().equals(thirdLocation)) {
							if (p == victim) {
								continue;
							}
							enemies.add(p);
						}
					}
					player.debug(String.format("First location still gfx:  %s%n ", firstLocation));
					//victim.getActionSender().stillGfx(157, firstLocation.getX(), firstLocation.getY(), player.getZ(), 5);
					//victim.getActionSender().stillGfx(157, player.getX() -3, player.getY() -3, player.getZ(), 0);
					victim.getActionSender().stillGfx(157, firstLocation); // fine
					//victim.getActionSender().stillGfx(157, secondLocation.getX(), secondLocation.getY(), player.getZ(), 5);
					//victim.getActionSender().stillGfx(157, thirdLocation.getX(), thirdLocation.getY(), player.getZ(), 5);

					if (!victim.getLocation().equals(firstLocation) && !victim.getLocation().equals(secondLocation)
							&& !victim.getLocation().equals(thirdLocation)) {
						doDamage = false;
					}
					if (doDamage) {
						victim.take_hit(attacker, dmg, preStyle).send();
						Player pVictim = (Player)victim;
						pVictim.debug("we didn't dodge so we got hit.");
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
		// TODO Auto-generated method stub

	}
}