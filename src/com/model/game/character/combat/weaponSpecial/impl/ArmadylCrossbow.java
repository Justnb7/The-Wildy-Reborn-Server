package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;
import com.model.utility.Utility;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		player.setCombatType(CombatStyle.RANGE);
		player.playAnimation(Animation.create(4230));
		player.getItems().deleteArrow();

		player.getItems().dropArrowUnderTarget();

		player.setCombatType(CombatStyle.RANGE);
		
		//player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 0));
		//TODO implement gfx 301
		int d = player.getLocation().distanceToEntity(player, target);
		player.playProjectile(Projectile.create(player.getLocation(), target, 301, 60, 50, 65 + (d * 5), 43, 35, 10, 36));
		player.getCombat().fireProjectileAtTarget();

		// Step 1: calculate a hit
		int dam1 = Utility.getRandom(player.getCombat().calculateRangeMaxHit());

		// Step 2: check if it missed
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam1 = 0;
		}

		// Step 3: check target's protection prayers
		Hit hit = target.take_hit(player, dam1, CombatStyle.RANGE, false, false);

		// Step 4: submit an Event where the hit appears.
		Combat.hitEvent(player, target, 2, hit, CombatStyle.RANGE);
	}

	@Override
	public int amountRequired() {
		return 40;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		if (player.getEquipment().getId(Equipment.ARROWS_SLOT) < 1) {
			player.getActionSender().sendMessage("You need atleast 1 bolt to perform this special.");
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
