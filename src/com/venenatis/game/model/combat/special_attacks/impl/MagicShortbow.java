package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {

		player.playAnimation(Animation.create(1074));
		int distance = player.getLocation().distanceToEntity(player, target);
		
		World.getWorld().schedule(new Task(1) {
			public void execute() {
				player.playGraphics(Graphic.create(256, 0, 100));
				this.stop();
			}
		});
		player.playProjectile(Projectile.create(player.getLocation(), target, 249, 30, 50, 40 + (distance * 5), 43, 35, 10, 36));
		player.playProjectile(Projectile.create(player.getLocation(), target, 249, 60, 50, 65 + (distance * 5), 43, 35, 10, 36));

		int dam1 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());
		int dam2 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam1 = 0;
		}
		if (!CombatFormulae.getAccuracy(player, target, 1, 1.0)) { // TODO attack type set to range?
			dam2 = 0;
		}
		target.take_hit(player, dam1, CombatStyle.RANGE).giveXP(player).send(1);
		target.take_hit(player, dam2, CombatStyle.RANGE).giveXP(player).send(1);
	}

	@Override
	public int amountRequired() {
		return 50;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
		if (player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() < 2) {
			player.message("You need atleast 2 arrows to perform this special.");
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