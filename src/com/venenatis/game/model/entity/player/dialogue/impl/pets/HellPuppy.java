package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class HellPuppy extends Dialogue {
	
	private final int PET = 964;
	
	private final Random random = new Random();

	@Override
	protected void start(Object... parameters) {
		int random_roll = random.nextInt(4);
		
		if (random_roll == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "How many souls have you devoured?");
			setPhase(0);
		} else if (random_roll == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "I wonder if I need to invest in a trowel when I take you out for a walk.");
			setPhase(5);
		} else if (random_roll == 2) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why were the hot dogs shivering?");
			setPhase(6);
		} else if (random_roll == 3) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Hell yeah! Such a cute puppy!");
			setPhase(10);
		} else if (random_roll == 4) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "What a cute puppy, how nice to meet you.");
			setPhase(13);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "None.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Awww p-");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Yet.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh.");
			setPhase(4);
			break;
		case 4:
			stop();
			break;
		case 5:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "More like a shovel.");
			setPhase(4);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Grrrrr...");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Because they were served-");
			setPhase(8);
			break;
		case 8:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "GRRRRRR...");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "-with... chilli?");
			setPhase(4);
			break;
		case 10:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Silence mortal! Or I'll eat your soul.");
			setPhase(11);
			break;
		case 11:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Would that go well with lemon?");
			setPhase(12);
			break;
		case 12:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Grrr...");
			setPhase(4);
			break;
		case 13:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "It'd be nice to meat you too...");
			setPhase(14);
			break;
		case 14:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Urk... nice doggy.");
			setPhase(15);
			break;
		case 15:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Grrr...");
			setPhase(4);
			break;
		}
	}
}