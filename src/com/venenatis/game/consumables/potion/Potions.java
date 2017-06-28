package com.venenatis.game.consumables.potion;

import com.venenatis.game.consumables.Consumable;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;

/**
 * Handles drinking potions
 * 
 * @author Arithium
 * 
 */
public class Potions extends Consumable {

	/**
	 * The data of the potion attempting to be consumed
	 */
	private final PotionData data;
	/**
	 * The slot of the consumable potion
	 */
	private final int slot;

	/**
	 * Constructs a new consumable potion
	 * 
	 * @param player
	 * @param data
	 * @param slot
	 */
	public Potions(Player player, PotionData data, int slot) {
		super(player, 0, 1800);
		this.data = data;
		this.slot = slot;
	}

	@Override
	public void consume() {
		super.setCurrentDelay("potion");
		if (getPlayer().isDead()) {
			return;
		}
		
		if (getPlayer().getDuelArena().getRules().get(DuelRule.DRINKS)) {
			getPlayer().getActionSender().sendMessage("Consuming potions has been disabled!");
			return;
		}

		Item item = new Item(data.getPotionId());
		Combat.resetCombat(getPlayer());
		if(getPlayer().getCombatState().getAttackDelay() + 1 <= getPlayer().getCombatCooldownDelay() + 2) {
			getPlayer().getCombatState().increaseAttackDelay(1);
		}
		getPlayer().playAnimation(Animation.create(829));
		getPlayer().getActionSender().sendSound(334, 1, 2);
		getPlayer().getInventory().setSlot(slot, new Item(data.getReplacement(), getPlayer().getInventory().get(slot).getAmount()));
		data.getPotionEffect().handle(getPlayer());
		String message = data.getReplacement() != 229 ? "You drink a dose of the " + item.getName() + "." : "You drink the last dose of your " + item.getName() + ".";
		getPlayer().getActionSender().sendMessage(message);
	}
}