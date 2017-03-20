package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();

		player.playAnimation(Animation.create(1074));

		RangeData.msbSpecProjectile(player);

		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				RangeData.msbSpecProjectile(player);
				this.stop();
			}
		});

		int dam1 = Utility.getRandom(player.getCombat().calculateRangeMaxHit());
		int dam2 = Utility.getRandom(player.getCombat().calculateRangeMaxHit());
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
		if (player.usingBow) {
			return true;
		}
		if (player.playerEquipment[player.getEquipment().getQuiverId()] < 2) {
			player.message("You need atleast 2 arrows to perform this special.");
			player.setUsingSpecial(false);
			return false;
		}
		return false;
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