package com.model.game.character.combat.magic;

import com.model.Server;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.task.ScheduledTask;

public class CombatSpells {
	
	public static void vengeance(Player venger, Entity entity_attacker, final int damage, int delay) {
		
		/**
		 * Minimum hit required
		 */
		if (damage < 2) {
			return;
		}

		/**
		 * The player entity
		 */
		if (entity_attacker instanceof Player) {
			Player attacker = (Player) entity_attacker;
			if(!venger.hasVengeance()) {
				return;
			}
			
			Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
				@Override
				public void execute() {
					if (damage < 2) {
						return;
					}
					int hit = (int) (damage * 0.75);
					if (hit < 1) {
						return;
					}
					if (attacker.isDead()) {
						hit = 0;
					}
					venger.sendForcedMessage("Taste vengeance!");
					venger.setVengeance(false);
					attacker.damage(new Hit(hit > attacker.getSkills().getLevel(Skills.HITPOINTS) ? attacker.getSkills().getLevel(Skills.HITPOINTS) : hit));
					this.stop();
				}
			});
			/**
			 * The npc entity
			 */
		} else {
			NPC attacker_npc = (NPC) entity_attacker;
			
			Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
				@Override
				public void execute() {
					if (damage < 2) {
						return;
					}
					int hit = (int) (damage * 0.75);
					if (hit < 1) {
						return;
					}
					if (attacker_npc.isDead()) {
						hit = 0;
					}
					venger.sendForcedMessage("Taste vengeance!");
					venger.setVengeance(false);
					attacker_npc.damage(new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED));
					this.stop();
				}
			});
		}
	}

}
