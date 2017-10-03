package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.content.achievements.Achievements;
import com.venenatis.game.content.achievements.Achievements.Achievement;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.MagicSpell;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

/**
 * Handles casting the low alchemy spell.
 * 
 * @author Daniel
 *
 */
public class HighAlchemy implements MagicSpell {

	@Override
	public boolean execute(Player player) {

		if (System.currentTimeMillis() - player.getMagic().getDelay() < 2500) {
			return false;
		}

		final Item item = player.getMagic().getItemUsed();

		if (item == null) {
			return false;
		}

		if (item.getId() == 995) {
			player.getActionSender().sendMessage("You can not alch this item!");
			return false;
		}

		if (!player.getInventory().hasSpaceFor(new Item(995))) {
			player.getActionSender().sendMessage("You have no inventory space to alch this item!");
			return false;
		}

		final int coins = item.getHighAlch();

		player.playAnimation(new Animation(713));
		player.playGraphic(new Graphic(113, 100));

		player.getInventory().remove(item.getId(), 1);
		
		
		player.getInventory().add(995, coins == 0 ? 1 : coins);

		player.getInventory().refresh();
		
		player.getActionSender().sendTab(6);

		player.getMagic().setDelay(System.currentTimeMillis());
		
		Achievements.activate(player, Achievement.NOVICE_THIEF, 1);

		return true;
	}

	@Override
	public double getExperience() {
		return 65;
	}

	@Override
	public int getLevel() {
		return 55;
	}

	@Override
	public String getName() {
		return "High alchemy";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(554, 5), new Item(561, 1) };
	}

}