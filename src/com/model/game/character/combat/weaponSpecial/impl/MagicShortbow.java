package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		Entity entity = player.getCombat().target;
		player.setCombatType(CombatType.RANGED);
		player.usingBow = true;			
		player.bowSpecShot = 1;
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
		player.playAnimation(Animation.create(1074));
		player.setCombatType(CombatType.RANGED);
		player.getCombat().fireProjectileAtTarget();
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				player.playAnimation(Animation.create(1074));
				player.playGraphics(Graphic.highGraphic(256));
				this.stop();
			}
		});
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				player.getCombat().fireProjectileAtTarget();
				player.playGraphics(Graphic.create(256));
				this.stop();
			}
		});

		Server.getTaskScheduler().schedule(new ScheduledTask(CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase()) - 1) {
			public void execute() {
				// TODO hit code which i put in notepad
				this.stop();
			}
		});
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