package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.range.RangeData;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {

		player.playAnimation(Animation.create(1074));

		RangeData.msbSpecProjectile(player);

		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				RangeData.msbSpecProjectile(player);
				this.stop();
			}
		});

		int dam1 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());
		int dam2 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam1 = 0;
		}
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam2 = 0;
		}

		// TODO maxhit, accuracy calc
		Combat.hitEvent(player, target, 1, new Hit(dam1), CombatStyle.RANGE);
		Combat.hitEvent(player, target, 1, new Hit(dam2), CombatStyle.RANGE);
	}

	@Override
	public int amountRequired() {
		return 50;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 2) {
			player.getActionSender().sendMessage("You need atleast 2 arrows to perform this special.");
			player.setUsingSpecial(false);
			return false;
		}
		return true;
	}

	
	@Override
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}
}