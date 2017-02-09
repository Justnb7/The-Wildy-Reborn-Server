package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
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

public class DarkBow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11235, 12765, 12766, 12767, 12768 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.setCombatType(CombatType.RANGED);
		player.playAnimation(Animation.create(426));
		
		player.dbowSpec = true;
		
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
		
		// Need to investigate what these variables are used for
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
		
		player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
		
		// On rapid, the attack delay is 1 tick faster.
		if (player.getAttackStyle() == 2)
			player.attackDelay--;
		
		// Send the projectile TODO adjust the height and duration for the 2nd arrow
		if (player.playerIndex > 0) {
			player.getCombat().fireProjectilePlayer();
		} else if (player.npcIndex > 0) {
			player.getCombat().fireProjectileNpc();
		}
		
		int first = Utility.random(player.getCombat().calculateRangeMaxHit());
		int second = Utility.random(player.getCombat().calculateRangeMaxHit());
		
		boolean success = CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (!success) {
			// We missed. The hits are changed to 4 or 8 later in the code.
			first = 0;
			second = 0;
		}

		if (target instanceof Player && (((Player) target).isActivePrayer(Prayer.PROTECT_FROM_MISSILE))) {
			first = (int) (first * 0.6);
			second = (int) (second * 0.6);
			
			// Dark bow has minimum hits.
			if (first < 4)
				first = 4;
			if (second < 4)
				second = 4;
		} else {
			if (first < 8)
				first = 8;
			if (second < 8)
				second = 8;
		}
		
		// Cast target to a type which supports the damage() method -- damage is in MobileChar not Entity
		Entity targ = (Entity) target;
		
		// Hit 1st
		targ.damage(new Hit(first, first > 0 ? HitType.NORMAL : HitType.BLOCKED));
		
		final int finalSecond = second;
		
		// Apply 2nd hitsplat 1 tick later
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				targ.damage(new Hit(finalSecond, finalSecond > 0 ? HitType.NORMAL : HitType.BLOCKED));
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
		if (player.playerEquipmentN[Equipment.SLOT_ARROWS] < 2) {
			player.write(new SendMessagePacket("You need at least two arrows to use this special attack."));
			return false;
		}
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1.75;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1.50;
	}

}
