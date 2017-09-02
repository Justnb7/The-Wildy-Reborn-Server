package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class DragonScimitar implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 4587 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		
		int damage = Utility.random(player.getCombatState().calculateMeleeMaxHit());
		player.playAnimation(Animation.create(1872));
		player.playGraphics(Graphic.create(347, (100 << 16)));
		
		boolean missed = !CombatFormulae.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		if(target instanceof Player) {
			Player targPlayer = (Player) target;
			boolean hasProtection = false;
			if (PrayerHandler.isActivated(targPlayer, PrayerHandler.PROTECT_FROM_MAGIC)) {
				hasProtection = true;
			} else if (PrayerHandler.isActivated(targPlayer, PrayerHandler.PROTECT_FROM_MISSILES)) {
				hasProtection = true;
			} else if (PrayerHandler.isActivated(targPlayer, PrayerHandler.PROTECT_FROM_MELEE)) {
				hasProtection = true;
			}

			if (hasProtection && damage > 0) {
				targPlayer.setHeadHint(-1);
				player.message("You have cancelled the protection prayer of " + targPlayer.getUsername() + ".");
				targPlayer.message("Your protection prayer has been cancelled by " + player.getUsername());
				targPlayer.cannotUsePrayer.reset();
			}
		}
		
		// Set up a Hit instance
        target.take_hit(player, damage, CombatStyle.MELEE).giveXP(player).send();
		
	}

	@Override
	public int amountRequired() {
		return 55;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
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
