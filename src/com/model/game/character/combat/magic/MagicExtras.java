package com.model.game.character.combat.magic;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

public class MagicExtras {

	public static void multiSpellEffectNPC(Player c, int npcId, int damage) {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 12891:
		case 12881:
			if (World.getWorld().getNpcs().get(npcId).refreezeTicks < -4) {
				World.getWorld().getNpcs().get(npcId).refreezeTicks = c.getCombat().getFreezeTime();
			}
			break;
		}
	}

	public static boolean checkMultiBarrageReqsNPC(int i) {
		if (World.getWorld().getNpcs().get(i) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkMultiBarrageReqs(Player player, int i) {
		Player target = World.getWorld().getPlayers().get(i);
		if (World.getWorld().getPlayers().get(i) == null) {
			return false;
		}
		if (i == player.getIndex())
			return false;
		if (!World.getWorld().getPlayers().get(i).getArea().inWild()) {
			return false;
		}
		if (!player.getAccount().getType().attackableTypes().contains(target.getAccount().getType())) {
			return false;
		}
		int combatDif1 = CombatRequirements.getCombatDifference(player.combatLevel, World.getWorld().getPlayers().get(i).combatLevel);
		if (combatDif1 > player.wildLevel || combatDif1 > World.getWorld().getPlayers().get(i).wildLevel) {
			player.write(new SendMessagePacket("Your combat level difference is too great to attack that player here."));
			return false;

		}
		if (!World.getWorld().getPlayers().get(i).getArea().inMulti()) { // single combat  zones
			if (World.getWorld().getPlayers().get(i).underAttackBy != player.getIndex() && World.getWorld().getPlayers().get(i).underAttackBy != 0) {
				return false;
			}
			if (World.getWorld().getPlayers().get(i).getIndex() != player.underAttackBy && player.underAttackBy != 0) {
				player.write(new SendMessagePacket("You are already in combat."));
				return false;
			}
		}
		return true;
	}

	public static void appendMultiBarrageNPC(Player player, int npcId, boolean splashed) {
		if (World.getWorld().getNpcs().get(npcId) != null) {
			Npc n = World.getWorld().getNpcs().get(npcId);
			if (n.isDead)
				return;
			if (checkMultiBarrageReqsNPC(npcId)) {
				player.barrageCount++;
				player.multiAttacking = true;
				n.underAttackBy = player.getIndex();
				n.underAttack = true;
				if (!player.magicFailed && CombatFormulas.getAccuracy(player,  World.getWorld().getNpcs().get(npcId), 2, 1.0)) {
					int gfxId = player.MAGIC_SPELLS[player.oldSpellId][5];
					/*if (gfxId == 369 && n.freezeTimer > 0) {
						n.gfx100(1677);
					} else */
					if (player.getCombat().getEndGfxHeight() == 100) { // end GFX
						n.playGraphics(Graphic.create(gfxId, 0, 100));
					} else {
						n.playGraphics(Graphic.create(gfxId, 0, 0));
					}
                    int damage = MagicCalculations.magicMaxHitModifier(player);
					if (n.currentHealth - damage < 0) {
						damage = n.currentHealth;
					}
					n.addDamageReceived(player.getRealUsername(), damage);
					n.damage(new Hit(damage));
					player.totalPlayerDamageDealt += damage;
					player.getCombat().multiSpellEffectNPC(npcId, damage);
					player.getCombat().multiSpellEffectNPC(npcId, damage);
				} else {
					n.playGraphics(Graphic.create(85, 0, 100));
				}
			}
		}
	}

	public static void multiSpellEffect(Player player, int playerId, int damage) {
		switch (player.MAGIC_SPELLS[player.oldSpellId][0]) {
		case 13011:
		case 13023:
			if (System.currentTimeMillis() - World.getWorld().getPlayers().get(playerId).reduceStat > 35000) {
				World.getWorld().getPlayers().get(playerId).reduceStat = System.currentTimeMillis();
				World.getWorld().getPlayers().get(playerId).getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevelForExperience(Skills.ATTACK) * 10 / 100);
			}
			break;
		case 12919: // blood spells
		case 12929:
			int heal = damage / 4;
			if(player.getSkills().getLevel(Skills.HITPOINTS) + heal >= player.getMaximumHealth()) {
				player.getSkills().setLevel(Skills.HITPOINTS, player.getMaximumHealth());
			} else {
				player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + heal);
			}
			break;
		case 12891:
		case 12881:
			if (World.getWorld().getPlayers().get(playerId).refreezeTicks < -4) {
				World.getWorld().getPlayers().get(playerId).refreezeTicks = player.getCombat().getFreezeTime();
				World.getWorld().getPlayers().get(playerId).stopMovement();
			}
			break;
		}
	}

	public static void appendMultiBarrage(Player player, int playerId, boolean splashed) {
		if (World.getWorld().getPlayers().get(playerId) != null) {
			Player victim = World.getWorld().getPlayers().get(playerId);
			if (victim.isDead())
				return;
			if (player.getCombat().checkMultiBarrageReqs(playerId)) {
				player.barrageCount++;
				if (!player.magicFailed && CombatFormulas.getAccuracy(player,  victim, 2, 1.0)) {
					int gfxId = player.MAGIC_SPELLS[player.oldSpellId][5];
					/*if (gfxId == 369 && c2.wasFrozen) {
						c2.gfx100(1677);
					} else */
					if (player.getCombat().getEndGfxHeight() == 100) { // end GFX
						victim.playGraphics(Graphic.create(gfxId, 0, 100));
					} else {
						victim.playGraphics(Graphic.create(gfxId, 0, 0));
					}
                    int damage = MagicCalculations.magicMaxHitModifier(player);
					if (victim.isActivePrayer(Prayer.MYSTIC_LORE)) {
						damage *= (int) (.60);
					}
					if(victim.getSkills().getLevel(Skills.HITPOINTS) - damage < 0) {
						victim.getSkills().setLevel(Skills.HITPOINTS, damage);
					}
					player.getSkills().addExperience(Skills.MAGIC, player.MAGIC_SPELLS[player.oldSpellId][7] + damage * Constants.EXP_MODIFIER);
					player.getSkills().addExperience(Skills.HITPOINTS, player.MAGIC_SPELLS[player.oldSpellId][7] + damage * Constants.EXP_MODIFIER / 3);
					World.getWorld().getPlayers().get(playerId).damage(new Hit(damage));
					World.getWorld().getPlayers().get(playerId).addDamageReceived(player.getName(), damage);
					player.totalPlayerDamageDealt += damage;
					player.getCombat().multiSpellEffect(playerId, damage);
				} else {
					victim.playGraphics(Graphic.create(85, 0, 0));
				}
			}
		}
	}
}