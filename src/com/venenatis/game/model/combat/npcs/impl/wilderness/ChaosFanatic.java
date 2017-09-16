package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
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

		int maxHit = 31;
		int randomHit;
		final int hit;
		int clientSpeed;
		int gfxDelay;
		int preHit = 0;
		((NPC) attacker).sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
		CombatStyle style = CombatStyle.MAGIC;

		switch (Utility.random(3)) {
		case 3:
			style = CombatStyle.GREEN_BOMB;
			break;
		}
		Location firstLocation = victim.getLocation();
		Location secondLocation = victim.getLocation().transform(1, 1, 0);
		Location thirdLocation = victim.getLocation().transform(-1, -1, 0);
		switch (style) {
		case MAGIC:
			attacker.playAnimation(ATTACK_ANIMATION);
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 50;
				gfxDelay = 60;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else {
				clientSpeed = 110;
				gfxDelay = 120;
			}
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), RED_GFX, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = Utility.random(maxHit);
			preHit = randomHit;
			
			
			int remove_armour = Utility.random(10);
			
			if (remove_armour == 1) {
				Player p = (Player) victim;
				if (victim.getActionSender() != null) {
					victim.message("The fiend attempts to disarm you.");
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
				p.message("The Chaos fanatic has removed some of your worn equipment.");
			}
			
			break;
		case GREEN_BOMB:
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
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), firstLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), secondLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), thirdLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			break;
		default:
			gfxDelay = 0;
			break;
		}
		attacker.getCombatState().setAttackDelay(6);

		final CombatStyle preStyle = style;
		hit = preHit;
		World.getWorld().schedule(new Task((gfxDelay / 20) - 1) {

			@Override
			public void execute() {
				this.stop();
				List<Player> enemies = new ArrayList<>();
				switch (preStyle) {
				case GREEN_BOMB:
					for (Player p : victim.getLocalPlayers()) {
						if (p.getLocation().equals(firstLocation) || p.getLocation().equals(secondLocation)
								|| p.getLocation().equals(thirdLocation)) {
							if (p == victim) {
								continue;
							}
							enemies.add(p);
						}
					}
					victim.getActionSender().sendStillGFX(157, 100, firstLocation);
					victim.getActionSender().sendStillGFX(157, 100, secondLocation);
					victim.getActionSender().sendStillGFX(157, 100, thirdLocation);
					break;
				default:
					break;
				}
				int finalHit = preStyle == CombatStyle.GREEN_BOMB ? Utility.random(maxHit) : hit;
				boolean doDamage = true;
				switch (preStyle) {
				case GREEN_BOMB:
					if (!victim.getLocation().equals(firstLocation) && !victim.getLocation().equals(secondLocation)
							&& !victim.getLocation().equals(thirdLocation)) {
						doDamage = false;
					}
					break;
				default:
					break;
				}
				if (doDamage) {
					victim.take_hit(attacker, finalHit, CombatStyle.GREEN_BOMB).send();
				}
				for (Player p : enemies) {
					victim.take_hit(p, finalHit, CombatStyle.GREEN_BOMB).send();
				}
				enemies.clear();
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
				World.getWorld().sendWorldMessage(
						"<col=7f00ff>" + player.getUsername() + " has just received the Chaos elemental pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage(
						"<col=7f00ff>" + player.getUsername() + " has just received the Chaos elemental pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
}