package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Ghommal extends Dialogue {
	
	private static final int NPC_ID = 2457;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "You not pass. You to weedy.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "What? But I'm a warrior!");
			setPhase(1);
		} else if (getPhase() == 1) {
			send(DialogueType.NPC, NPC_ID, Expression.LAUGH, "Heehee... he say he warrior... I not heard that one", "for... at leas' 5 minutes!");
			setPhase(2);
		} else if (getPhase() == 2) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Go on, let me in, you know you want to. I could...", "make it worth your while...");
			setPhase(3);
		} else if (getPhase() == 3) {
			send(DialogueType.NPC, NPC_ID, Expression.MAD, "No! You is not a strong warrior, you enter till you", "bigger. Ghommal does not take bribes.");
			setPhase(4);
		} else if (getPhase() == 4) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Why not?");
			setPhase(5);
		} else if (getPhase() == 5) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Ghommal stick to Warrior's Code of Honour. When", "you a bigger, stronger warrior, you come back.");
			setPhase(6);
		} else if (getPhase() == 6) {
			stop();
		}
	}

}
