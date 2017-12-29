package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Rocky extends Dialogue {
	
	private final int PET = 7336;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(2);
		if(random == 0 ) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "*Whistles*");
			player.message("You slip your hand into Rocky's pocket.");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Is there much competition between you raccoons and the magpies?");
			setPhase(2);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Hey Rocky, do you want to commit a bank robbery with me?");
			setPhase(4);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "OY!! You're going to have to do better than that! Sheesh, what an amateur.");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Magpies have nothing on us! They're just interested in shinies.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Us raccoons have a finer taste, we can see the value in anything, whether it shines or not.");
			setPhase(1);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "If that is the level you are at, I do not wish to participate in criminal acts with you "+player.getUsername()+".");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Well what are you interested in stealing?");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "The heart of a lovely raccoon called Rodney.");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I cannot really help you there I'm afraid.");
			setPhase(1);
			break;
		}
	}

}