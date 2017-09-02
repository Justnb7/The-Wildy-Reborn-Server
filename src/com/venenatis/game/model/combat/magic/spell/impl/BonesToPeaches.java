package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.MagicSpell;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

/**
 * Spell for casting bones to peaches.
 * 
 * @author Daniel
 *
 */
public class BonesToPeaches implements MagicSpell {

	/* Bones Array */
	private final int[] bones = { 526, 528, 530, 534 };

	@Override
	public boolean execute(Player player) {

		int bone = 0;

		for (final int bone2 : bones) {
			if (player.getInventory().contains(bone2)) {
				bone = bone2;
				continue;
			}
		}

		final int amount = player.getInventory().getAmount(bone);

		if (amount == 0) {
			player.getActionSender().sendMessage("You have no bones to do this!");
			return false;
		}

		player.getInventory().remove(bone, amount);
		player.getInventory().add(6883, amount, true);

		player.playAnimation(new Animation(722));
		player.playGraphic(new Graphic(141, 100));

		player.getActionSender().sendMessage("You have converted " + amount + " bones to peaches.");

		return true;
	}

	@Override
	public double getExperience() {
		return 35.5;
	}

	@Override
	public int getLevel() {
		return 60;
	}

	@Override
	public String getName() {
		return "Bones to Peaches";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(557, 4), new Item(555, 4), new Item(561, 2) };
	}

}