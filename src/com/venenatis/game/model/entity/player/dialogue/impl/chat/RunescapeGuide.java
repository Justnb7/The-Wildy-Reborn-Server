package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class RunescapeGuide extends Dialogue {
	
	private static final int NPC_ID = 3308;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Welcome to The Wildy Reborn Alpha "+player.getUsername(), "Thank you for joining us.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "This Alpha is to test basic functionalities of this server ", "There are still many featured not available.");
			setPhase(1);
		} else
		if (getPhase() == 1) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "You can do the commands ::master and ::item", "Please post all bugs in the bugs section of the forums");
			setPhase(2);
		} else
		if (getPhase() == 2) {
			send(DialogueType.NPC, NPC_ID, Expression.LAUGH, "oh i forgot to mention..You will receive a special", " item for being apart of the Alpha once the server is live.");
			setPhase(3);
		} else
		if (getPhase() == 3) {
			send(DialogueType.NPC, NPC_ID, Expression.HAVE_FUN, "Have fun. Time to select your game mode.");
			setPhase(4);
			
		} else if (getPhase() == 4) {
			player.getGameModeSelection().open(player);
		}
	}
}
