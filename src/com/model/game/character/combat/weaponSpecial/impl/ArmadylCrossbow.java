package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		Entity entity = player.getCombat().target;
		player.setCombatType(CombatType.RANGED);
		player.playAnimation(Animation.create(4230));
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.getItems().deleteArrow();

		player.getItems().dropArrowUnderTarget();
		
		//TODO implement gfx 301

		player.setCombatType(CombatType.RANGED);
		
		player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 0));

		player.getCombat().fireProjectileAtTarget();

		Server.getTaskScheduler().schedule(new ScheduledTask(2) {
			public void execute() {
				// TODO notepad code
				this.stop();
			}
		});
	}

	@Override
	public int amountRequired() {
		return 40;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 2;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
