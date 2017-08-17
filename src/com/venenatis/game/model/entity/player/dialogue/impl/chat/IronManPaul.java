package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

public class IronManPaul extends Dialogue {
	
	/**
	 * The id of the npc
	 */
	public static final int NPC_ID = 317;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.CALM_TALK, "Hello there " + player.getUsername() + ", how can i help you?");
	}
	
	@Override
	public void next() {
		if (isPhase(0)) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Change game mode", "I've lost my ironman armour", "Nevermind");
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Become an ironman", "Become an regular account", "nevermind");
				setPhase(1);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.VERY_SAD, "I've lost my ironman armour.");
				setPhase(2);
			} else if (index == 3) {
				stop();
			}
		} else if(isPhase(1)) {
			if (index == 1) {
				if(player.getRights().isIron(player)) {
					player.getActionSender().sendMessage("You're already an ironman.");
					stop();
				} else {
					player.getDialogueManager().start("CLEAR_ACCOUNT", player);
				}
			} else if (index == 2) {
				if(player.getAccount().isRegular()) {
					player.getActionSender().sendMessage("You're already playing as an regular account.");
					stop();
				} else {
					player.getAccount().setType(Account.REGULAR_TYPE);
					player.setRights(Rights.PLAYER);
					stop();
				}
			} else {
				stop();
			}
		}
	}
}
