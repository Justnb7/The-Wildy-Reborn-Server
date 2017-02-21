package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.equipment.Equipment;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Ballista implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 19481 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.setCombatType(CombatType.RANGED);
		player.playAnimation(Animation.create(7222));
		
		player.getItems().deleteArrow();
		
		// Need to investigate what these variables are used for
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
		
		// On rapid, the attack delay is 1 tick faster.
		if (player.getAttackStyle() == 2)
			player.attackDelay--;
		
		if (player.playerIndex > 0) {
			player.getCombat().fireProjectilePlayer();
		} else if (player.npcIndex > 0) {
			player.getCombat().fireProjectileNpc();
		}
		
		int damage = Utility.random(player.getCombat().calculateRangeMaxHit());
		boolean success = CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (!success) {
			damage = 0;
		}

		if (target instanceof Player && (((Player) target).isActivePrayer(Prayer.PROTECT_FROM_MISSILE))) {
			damage = (int) (damage * 0.6);
		}
		
		Entity targ = (Entity) target;
		
		final int finalDmg = damage;
		
		// Hit
		Server.getTaskScheduler().schedule(new ScheduledTask(2) {
			@Override
			public void execute() {
				targ.damage(new Hit(finalDmg, finalDmg > 0 ? HitType.NORMAL : HitType.BLOCKED));
				this.stop();
			}
		});
		
	}

	@Override
	public int amountRequired() {
		return 65;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		if (player.playerEquipmentN[Equipment.SLOT_ARROWS] < 1) {
			player.write(new SendMessagePacket("You need at least one javalin to use this special attack."));
			return false;
		}
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 3.75;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}