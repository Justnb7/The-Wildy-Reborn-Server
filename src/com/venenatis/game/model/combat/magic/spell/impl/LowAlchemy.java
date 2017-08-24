package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
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
public class LowAlchemy implements MagicSpell {

	@Override
	public boolean execute(Player player) {

		if (System.currentTimeMillis() - player.getMagic().getDelay() < 1500) {
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

		final int coins = item.getLowAlch();

		player.playAnimation(new Animation(712));
		player.playGraphics(new Graphic(112, 100));

		player.getInventory().remove(item.getId(), 1);
		
		player.getInventory().add(995, coins == 0 ? 1 : coins);

		player.getInventory().refresh();

		player.getActionSender().changeSidebar(6);

		player.getMagic().setDelay(System.currentTimeMillis());
		player.getSkills().addExperience(Skills.MAGIC, 31);

		return false;
	}

	@Override
	public double getExperience() {
		return 850.5D;
	}

	@Override
	public int getLevel() {
		return 21;
	}

	@Override
	public String getName() {
		return "Low alchemy";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(554, 3), new Item(561, 1) };
	}

}