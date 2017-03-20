package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		Entity entity = player.getCombat().target;
		player.setCombatType(CombatType.RANGE);
		player.playAnimation(Animation.create(4230));
		player.getItems().deleteArrow();

		player.getItems().dropArrowUnderTarget();

		player.setCombatType(CombatType.RANGE);
		
		//player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 0));
		//TODO implement gfx 301
				int d = player.getPosition().distanceToEntity(player, target);
				player.playProjectile(Projectile.create(player.getPosition(), target, 301, 60, 50, 65 + (d * 5), 43, 35, 10, 36));

		player.getCombat().fireProjectileAtTarget();

		Combat.hitEvent(player, target, 2, null, CombatType.RANGE);
	}

	@Override
	public int amountRequired() {
		return 40;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		if (player.playerEquipment[player.getEquipment().getQuiverId()] < 1) {
			player.message("You need atleast 1 bolt to perform this special.");
			player.setUsingSpecial(false);
			return false;
		}
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
