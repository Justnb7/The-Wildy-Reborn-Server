package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Mac extends Dialogue {
	
	private static final int NPC_ID = 6481;
	
	private final int MAX_CAPE_ID = 13280;
	private final int MAX_HOOD_ID = 13281;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Hello");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(DialogueType.STATEMENT, "The man glances at you and grunts something unintelligent");
			setPhase(1);
		} else if (getPhase() == 1) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Who are you?", "What do you have in your sack?", "Why are you so dirty?", "Bye.");
			setPhase(2);
		} else if (getPhase() == 3) {
			send(DialogueType.NPC, NPC_ID, Expression.ANGRY, "Mac. What's it to you?");
			setPhase(4);
		} else if(getPhase() == 4) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Only trying to be friendly.");
			setPhase(5);
		} else if(getPhase() == 5) {
			stop();
		} else if(getPhase() == 6) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "S'me cape.");
			setPhase(7);
		} else if(getPhase() == 7) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Your cape?");
			setPhase(8);
		} else if(getPhase() == 8) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Can I have it?");
			setPhase(9);
		} else if(getPhase() == 9) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Mebe");
			setPhase(10);
		} else if(getPhase() == 10) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "I'm sure I could make it worth your while.");
			setPhase(11);
		} else if(getPhase() == 11) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "How much?");
			setPhase(12);
		} else if(getPhase() == 12) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "How about 2277000 gold?");
			setPhase(13);
		} else if(getPhase() == 13) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Yes, pay the man.", "No");
			setPhase(14);
		} else if(getPhase() == 15) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Bath XP waste.");
			setPhase(5);
		}
	}
	
	@Override
	public void select(int index) {
		System.out.println("next : phase " + getPhase());
		if (getPhase() == 2) {
			switch(index) {
			case 1:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "Who are you?");
				setPhase(3);
				break;
			case 2:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "What do you have in your sack?");
				setPhase(6);
				break;
			case 3:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "Why are you so dirty?");
				setPhase(15);
				break;
			case 4:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "Bye.");
				setPhase(5);
				break;
			}
		} else if(getPhase() == 14) {
			switch(index) {
			case 1:
				if (player.getSkills().getTotalLevel() != 2079) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "You don't have the requirements to buy this item!");
					setPhase(5);
					return;
				}
				if (!player.getInventory().hasItemAmount(995, 5000000)) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "You do not have enough money to buy the cape");
					setPhase(5);
					return;
				}
				
				player.getInventory().remove(995, 2277000);
				player.getInventory().add(MAX_CAPE_ID, 1);
				player.getInventory().add(MAX_HOOD_ID, 1);
				send(DialogueType.ITEM, MAX_CAPE_ID, "", "Mac grunts and hands over his cape, pocketing your", "money swiftly.");
				if (!player.Completionist())
					player.setCompletionist(true);
				setPhase(5);
				break;
			case 2:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "No.");
				setPhase(5);
				break;
			}
		}
	}

}
