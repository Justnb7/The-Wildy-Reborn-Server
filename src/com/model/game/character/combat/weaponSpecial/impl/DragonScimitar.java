package com.model.game.character.combat.weaponSpecial.impl;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;

public class DragonScimitar implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 4587 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		
		int damage = Utility.random(player.getCombat().calculateMeleeMaxHit());
		player.playAnimation(Animation.create(1872));
		player.playGraphics(Graphic.create(347, (100 << 16)));
		
		boolean missed = !CombatFormulas.getAccuracy((Entity)player, (Entity)target, 0, getAccuracyMultiplier());
		if (missed)
			damage = 0;
		if(target instanceof Player) {
			Player targPlayer = (Player) target;
			boolean hasProtection = false;
			if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MAGIC)) {
				hasProtection = true;
			} else if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MISSILE)) {
				hasProtection = true;
			} else if (targPlayer.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				hasProtection = true;
			}

			if (hasProtection && damage > 0) {
				targPlayer.setPrayerIcon(-1);
				targPlayer.getPA().requestUpdates();
				player.write(new SendMessagePacket("You have cancelled the protection prayer of " + targPlayer.getName() + "."));
				targPlayer.write(new SendMessagePacket("Your protection prayer has been cancelled by " + player.getName()));
				targPlayer.cannotUsePrayer.reset();
			}
		}
		
		target.take_hit(player, damage, CombatType.MELEE).giveXP(player);
		
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
