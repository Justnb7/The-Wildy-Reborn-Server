package com.venenatis.game.model.combat.npcs.impl.godwars.bandos;

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

import java.util.Collection;
import java.util.Random;

public class GeneralGraardor extends AbstractBossCombat {

	/**
	 * All graardors message stored in a single String
	 */
	private static final String[] MESSAGES = { "Death to our enemies!", "Brargh!", "Break their bones!",
			"For the glory of Bandos!", "Split their skulls!", "We feast on the bones of our enemies tonight!",
			"CHAAARGE!", "Crush them underfoot!", "All glory to Bandos!", "GRAAAAAAAAAR!",
			"FOR THE GLORY OF THE BIG HIGH WAR GOD!" };

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * The timer of Graardors shouts
	 */
	private long lastMessage;

	@Override
	public void execute(Entity attacker, Entity victim) {

		if (!attacker.isNPC()) {
			return;
		}

		// Sent a random shout
		if (random.nextInt(3) == 2 && System.currentTimeMillis() - lastMessage > 3000) {
			attacker.sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
			lastMessage = System.currentTimeMillis();
		}

		// Sends the combat style 1 tile away sends Melee attacks and more then
		// 1 sends Ranging attacks
		CombatStyle style = attacker.getLocation().distanceToEntity(attacker, victim) <= 1 ? CombatStyle.MELEE : CombatStyle.RANGE;

		// The npc instance
		NPC npc = (NPC) attacker;

		// Calculate max hit first
		int maxHit = style == CombatStyle.RANGE ? 35 : 60;

		switch (style) {
		case MELEE:
			Animation anim = Animation.create(npc.getAttackAnimation());
			attacker.playAnimation(anim);

			int randomHit = Utility.random(maxHit);

			// Create the hit instance
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send();
			break;

		case RANGE:
			attacker.playAnimation(Animation.create(7021));
			victim.playGraphics(Graphic.create(1203, 0, 0));
			
			final Collection<Player> localPlayers = RegionStoreManager.get().getLocalPlayers(attacker);
			
			for(final Player near : localPlayers) {
				if(near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
						// Set the projectile speed based on distance
						int speedEquation;
						if(attacker.getLocation().isWithinDistance(attacker, near, 1)) {
							speedEquation = 70;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 5)) {
							speedEquation = 90;
						} else if(attacker.getLocation().isWithinDistance(attacker, near, 8)) {
							speedEquation = 110;
						} else {
							speedEquation = 130;
						}
						// Send the projectile
						attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1202, 45, 50, speedEquation, 43, 35, near.getProjectileLockonIndex(), 10, 48));
					}
					// Calculate max hit first
					randomHit = Utility.random(maxHit);

					// Create the hit instance
					near.take_hit(attacker, randomHit, CombatStyle.RANGE).send(2);
				}
			}
			break;

		default:
			break;

		}

		// Graardor has a attacker timer of 3.6 seconds A.K.A 6 ticks
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		// Graardor has a attack distance of 3 tiles, not sure on this.
		return 3;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12650));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the General graardor Jr. pet.", false);
			} else {
				Pets pets = Pets.GENERAL_GRAARDOR;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the General graardor Jr. pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}