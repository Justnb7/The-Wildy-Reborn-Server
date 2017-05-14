package com.model.game.character.combat.npcs.script.godwars.bandos;

import java.util.Random;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

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
			Hit hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MELEE, false);

			// Send the hit task
			Combat.hitEvent(attacker, victim, 1, hitInfo, CombatStyle.MELEE);
			break;

		case RANGE:
			attacker.playAnimation(Animation.create(7021));
			victim.playGraphics(Graphic.create(1203, 0, 0));

			// Set the projectile speed based on distance
			int speedEquation;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				speedEquation = 70;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				speedEquation = 90;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				speedEquation = 110;
			} else {
				speedEquation = 130;
			}

			// Send the projectile
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1202, 45, 50, speedEquation, 43, 35, victim.getProjectileLockonIndex(), 10, 48));

			// Calculate max hit first
			randomHit = Utility.random(maxHit);

			// Create the hit instance
			hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.RANGE, false);

			// Send the hit task
			Combat.hitEvent(attacker, victim, 2, hitInfo, CombatStyle.RANGE);
			break;

		default:
			break;

		}

		// Graardor has a attacker timer of 3.6 seconds A.K.A 6 ticks
		((NPC) attacker).attackTimer = 6;
	}

	@Override
	public int distance(Entity attacker) {
		// Graardor has a attack distance of 3 tiles, not sure on this.
		return 3;
	}
	
	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}