package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.content.BrokenItem;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Perdu extends Dialogue {

	private final int REPAIR_COST_MULTIPLIER = 500_000;

	/**
	 * The id of the npc
	 */
	public static final int NPC_ID = 7456;

	@Override
	protected void start(Object... parameters) {
		for (BrokenItem item : BrokenItem.values()) {
			boolean hasBrokenItem = player.getInventory().containsAny(item.getBrokenItem());
			player.debug(""+hasBrokenItem);
			
			if (hasBrokenItem) {
				setPhase(0);
			} else {
				send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Hello. I'm afraid I haven't got anything for you today.");
				setPhase(1);
			}
		}
	}
	
	@Override
	public void next() {
		if (getPhase() == 0) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Repair", "Nevermind");
		} else if(getPhase() == 1) {
			player.getActionSender().removeAllInterfaces();
		} else if(getPhase() == 2) {
			player.getActionSender().removeAllInterfaces();
		}
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch (index) {
			case 1:
				repair(player);
				break;
			case 2:
				player.getActionSender().removeAllInterfaces();
				break;
			}
		}
	}

	/**
	 * Gets the total cost of repairing a player's broken items.
	 * 
	 * @param player
	 *            The player
	 */
	private int getRepairCost(Player player) {
		int cost = 0;
		for (BrokenItem b : BrokenItem.values()) {
			final int amt = player.getInventory().getAmount(b.getBrokenItem());
			if (amt > 0) {
				cost += REPAIR_COST_MULTIPLIER * amt;
			}
		}
		return cost;
	}

	/**
	 * Repairs all broken items for a player.
	 * 
	 * @param player
	 *            The player we repair the items of
	 */
	private void repair(Player player) {
		boolean fixed = false;

		for (BrokenItem b : BrokenItem.values()) {
			final int amount = player.getInventory().getAmount(b.getBrokenItem());
			if (amount > 0) {
				final int cost = ((int) (ItemDefinition.get(b.getOriginalItem()).getValue() * getRepairCost(player)) * amount);
				if (player.getInventory().getAmount(995) >= cost) {
					player.getInventory().remove(995, cost);
					player.getInventory().remove(b.getBrokenItem(), amount);
					player.getInventory().add(b.getOriginalItem(), amount);
					fixed = true;
				} else {
					player.getActionSender().sendMessage("You could not afford fixing all your items.");
					break;
				}
			}
		}

		if (fixed) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "There you go, Be more carefull with it in future!");
			setPhase(2);
		} else {
			player.getActionSender().removeAllInterfaces();
		}
	}

}
