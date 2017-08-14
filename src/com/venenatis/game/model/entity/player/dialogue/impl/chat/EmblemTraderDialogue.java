package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.content.bounty.BountyHunterEmblem;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.shop.ShopManager;

/**
 * Handles the {@link Dialogue} for the npc Lucien. Lucien can exchange emblems
 * for bounty points
 * 
 * @author Mobster
 *
 */
public class EmblemTraderDialogue extends Dialogue {

	/**
	 * The total amount of points you'll gain from this swap
	 */
	private int totalPoints;

	/**
	 * The id of the npc
	 */
	public static final int NPC_ID = 315;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.CALM_TALK, "Hello there " + player.getUsername() + ", how can i help you?");
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Open Bounty Shop", "Exchange Emblems", "Nevermind");
		} else if (isPhase(1)) {
			send(Type.NPC, NPC_ID, Expression.CALM_TALK, "Certainly, let me calculate ", "your total points from your emblems.");
			setPhase(2);
		} else if (isPhase(2)) {
			send(Type.STATEMENT, "Calculating total points...");
			totalPoints = 0;
			for (Item i : player.getInventory().toArray()) {
				int id = i.getId();
				if (id <= 0) {
					continue;
				}
				BountyHunterEmblem emblem = BountyHunterEmblem.get(id);
				if (emblem != null) {
					totalPoints += emblem.getBounties();
				}
			}
			setPhase(3);
		} else if (isPhase(3)) {
			if (totalPoints > 0) {
				send(Type.NPC, NPC_ID, Expression.CALM_TALK, "You will get a total of " + Utility.format(totalPoints) + " bounty points for ", "your emblems. Do you wish to exchange them?");
				setPhase(4);
			} else {
				send(Type.NPC, NPC_ID, Expression.CALM_TALK, "You do not have any emblems.");
				setPhase(0);
			}
		} else if (isPhase(4)) {
			send(Type.CHOICE, null, "Yes", "No");
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				ShopManager.open(player, 6);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.CALM_TALK, "I would like to exchange my emblems.");
				setPhase(1);
			} else if (index == 3) {
				stop();
			}
		} else if (isPhase(4)) {
			if (index == 1) {
				totalPoints = 0;
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					int id = player.getInventory().getId(i);
					if (id <= 0) {
						continue;
					}
					BountyHunterEmblem emblem = BountyHunterEmblem.get(id);
					if (emblem != null) {
						totalPoints += emblem.getBounties();
						player.getInventory().remove(new Item(emblem.getItemId(), i));
					}
				}
				if(player.getBountyPoints() == Integer.MAX_VALUE) {
					return;
				}
				player.setBountyPoints(player.getBountyPoints() + totalPoints);
				//Achievements.increase(player, AchievementType.BOUNTIES, totalPoints);
				send(Type.NPC, NPC_ID, Expression.HAPPY, "I've traded your emblems for " + Utility.format(totalPoints) + " bounty", "points. You now have a total of " + Utility.format(player.getBountyPoints()), "bounty points.");
				setPhase(0);
				totalPoints = 0;
			} else if (index == 2) {
				setPhase(1);
				next();
			}
		}
	}

}