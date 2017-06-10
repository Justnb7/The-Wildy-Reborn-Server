package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Ballista implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 19481 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.playAnimation(Animation.create(7222));

		player.getCombatState().fireProjectileAtTarget();
		
		int damage = Utility.random(player.getCombatState().calculateRangeMaxHit());
		boolean success = CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (!success) {
			damage = 0;
		}

		if (target instanceof Player && (((Player) target).isActivePrayer(PrayerHandler.Prayers.PROTECT_FROM_MISSILE))) {
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
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 1) {
			player.getActionSender().sendMessage("You need at least one javalin to use this special attack.");
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