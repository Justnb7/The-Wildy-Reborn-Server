package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class GiantSquirrel extends Dialogue {
	
	private final int PET = 7334;

	@Override
	protected void start(Object... parameters) {
		int random = new Random().nextInt(2);
		
		if(random == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "So how come you are so agile?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "What's up with all that squirrel fur? I guess fleas need a home too.");
			setPhase(2);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Did you ever notice how big squirrels' teeth are?");
			setPhase(3);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "If you were so nutty about nuts, maybe you would understand the great lengths we go to!");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "You're pushing your luck! Stop it or you'll face my squirrely wrath.");
			setPhase(1);
			break;
		case 3:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "No...");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.PLAYER, Expression.DEFAULT, "You could land a gnome glider on those things!");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Watch it, I'll crush your nuts!");
			setPhase(1);
			break;
		}
	}

}