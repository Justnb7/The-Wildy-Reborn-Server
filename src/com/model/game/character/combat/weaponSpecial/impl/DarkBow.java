package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.*;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class DarkBow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11235, 12765, 12766, 12767, 12768 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.playAnimation(Animation.create(426));
		
		player.playGraphics(Graphic.create(player.getCombatState().getRangeStartGFX(), 0, 100));
		
		// Send the projectile TODO adjust the height and duration for the 2nd arrow
		player.getCombatState().fireProjectileAtTarget();
		
		int first = Utility.random(player.getCombatState().calculateRangeMaxHit());
		int second = Utility.random(player.getCombatState().calculateRangeMaxHit());
		
		boolean success = CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (!success) {
			// We missed. The hits are changed to 4 or 8 later in the code.
			first = 0;
			second = 0;
		}

		if (target instanceof Player && (((Player) target).isActivePrayer(PrayerHandler.Prayers.PROTECT_FROM_MISSILE))) {
			first = (int) (first * 0.6);
			second = (int) (second * 0.6);
			
			// Dark bow has minimum hits.
			if (first < 4)
				first = 4;
			if (second < 4)
				second = 4;
		} else {
			// Minimum hit without pray range is 8/8
			if (first < 8)
				first = 8;
			if (second < 8)
				second = 8;
		}
		// Dark bow is a special type where there is minimum damage of 4/4 or 8/8
		// So we can't use the take_hit method - we need to put veng in here.. 
		
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

		boolean dragArrow = player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() == 11212;
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				target.playGraphics(Graphic.create(dragArrow ? 1100 : 1103, 0, 0));
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
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 2) {
			player.getActionSender().sendMessage("You need at least two arrows to use this special attack.");
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
