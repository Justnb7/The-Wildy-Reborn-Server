package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

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
		Server.getTaskScheduler().schedule(new Task(2) {
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
		Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT); 
		if (ammo.getId() < 1) {
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