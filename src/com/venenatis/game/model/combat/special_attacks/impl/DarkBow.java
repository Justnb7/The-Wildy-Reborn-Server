package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

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
		
		target.take_hit(player, first, CombatStyle.RANGE).giveXP(player).send();
		target.take_hit(player, second, CombatStyle.RANGE).giveXP(player).send(2);

		boolean dragArrow = player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() == 11212;
		Server.getTaskScheduler().schedule(new Task(1) {
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
		Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT); 
		if (ammo.getId() < 2) {
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
