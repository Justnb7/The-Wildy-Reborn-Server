package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class BloodHound extends Dialogue {
	
	private final int PET = 7232;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(4);
		if(random == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "How come I can talk to you without an amulet?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Walkies!");
			setPhase(2);
		} else if(random == 2) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Can you help me with this clue?");
			setPhase(3);
		} else if(random == 3) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "I wonder if I could sell you to a vampire to track down dinner.");
			setPhase(7);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Hey boy, what's up?");
			setPhase(8);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Woof woof bark!* Elementary, it's due to the influence of the -SQUIRREL-!");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "...");
			setPhase(1);
			break;
		case 3:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Woof! Bark yip woof!* Sure! Eliminate the impossible first.");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.PLAYER, Expression.DEFAULT, "And then?");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Bark! Woof bark bark.* Whatever is left, however improbable, must be the answer.");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.PLAYER, Expression.DEFAULT, "So helpful.");
			setPhase(1);
			break;
		case 7:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Woof bark bark woof* I have teeth too you know, that joke was not funny.");
			setPhase(1);
			break;
		case 8:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Woof! Bark bark woof!* You smell funny.");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Err... funny strange or funny ha ha?");
			setPhase(10);
			break;
		case 10:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Bark bark woof!* You aren't funny.");
			setPhase(1);
			break;
		}
	}

}