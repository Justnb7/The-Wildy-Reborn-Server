package com.venenatis.game.model.combat.magic.spell.impl;

import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.magic.MagicSpell;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

/**
 * Handles casting the Vengeance spell.
 * 
 * @author Daniel
 *
 */
public class Vengeance implements MagicSpell {

	public static void handle(Player venger, Entity entity_attacker, int damage) {
		
		/*
		 * Minimum hit required
		 */
		if (damage < 2 || !venger.hasVengeance()) {
			venger.debug("test");
			return;
		}
		venger.sendForcedMessage("Taste vengeance!");
		venger.setVengeance(false);
		entity_attacker.take_hit(venger, (int)(damage*.75), null).send(1); // no combat xp given from veng damage
	}

	@Override
	public boolean execute(Player player) {
		if (player.getSkills().getLevel(Skills.DEFENCE) < 45) {
			player.getActionSender().sendMessage("You need a defence level of 45 to cast this spell!");
			return false;
		}
		if (System.currentTimeMillis() - player.getMagic().getLastVengeance() < 30_000L) {
			player.getActionSender().sendMessage("You can only cast vengeance once every 30 seconds.");
			return false;
		}
		if (player.hasVengeance()) {
			player.getActionSender().sendMessage("You already have vengeance casted!");
			return false;
		}
		player.playAnimation(new Animation(4410));
		player.playGraphics(new Graphic(726, 100));
		player.setVengeance(true);
		player.getMagic().setLastVengeance(System.currentTimeMillis());
		AchievementHandler.activate(player, AchievementList.TASTE_ME, 1);
		player.getActionSender().sendWidget(2, 30);
		
		return true;
	}

	@Override
	public double getExperience() {
		return 112.0D;
	}

	@Override
	public int getLevel() {
		return 94;
	}

	@Override
	public String getName() {
		return "Vengeance";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(9075, 4), new Item(557, 10), new Item(560, 2) };
	}

}