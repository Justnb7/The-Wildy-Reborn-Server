package com.model.game.character.combat.npcs.script.godwars.zamorak;

import java.util.Random;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.utility.Utility;

public class KrilTsutsaroth extends AbstractBossCombat {
	
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
		
		Player player = (Player) victim;

		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit = 0;
		int damage;
		int randomHit;
		int hitDelay;
		final int hit;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			if (random.nextInt(6) == 4)
				style = CombatStyle.MELEE;
		}
		
		switch(style) {
		case MELEE:
			attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
			hitDelay = 2;
			boolean ignore = false;
			if(player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				if(Utility.random(8) == 7) {
					maxHit = 49;
					ignore = true;
					victim.getActionSender().sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
					player.getSkills().decreaseLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) / 2);
				}
			} else {
				maxHit = npc.getDefinition().getMaxHit();
			}
			
			randomHit = Utility.random(maxHit);
			hit = randomHit;
			
			// Create the hit instance
			Hit hitInfo = victim.take_hit(attacker, hit, CombatStyle.RANGE, false, true);

			// Send the hit task
			Combat.hitEvent(attacker, victim, hitDelay, hitInfo, CombatStyle.RANGE);
			break;
		case MAGIC:
			break;
		default:
			break;
		
		}
		
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO add pet drop
		
	}

}
