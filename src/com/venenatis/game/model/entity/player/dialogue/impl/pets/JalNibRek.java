package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class JalNibRek  extends Dialogue {
	
	//TODO
	private final int PET = -1;
	
	private final Random random = new Random();

	@Override
	protected void start(Object... parameters) {
		int random_roll = random.nextInt(4);
		
		if (random_roll == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Yo Nib, what's going on?");
			setPhase(0);
		} else if (random_roll == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "What'd you have for dinner?");
			setPhase(4);
		} else if (random_roll == 2) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Can you speak like a human can Nib?");
			setPhase(8);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Nibnib? Kl-Rek Nib?");
			player.message("Jal-Nib-Rek nips you.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "What'd you do that for?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Heh Nib get you");
			setPhase(3);
			break;
		case 3:
			stop();
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Nibblings!");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Nibblings of what exactly?");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Nib.");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh no! That's horrible.");
			setPhase(3);
			break;
		case 8:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "No, I most definitely can not.");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Aren't you speaking like a human right now...?");
			setPhase(10);
			break;
		case 11:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Jal-Nib-Rek Nib Kl-Jal, Zuk is mum.");
			setPhase(12);
			break;
		case 12:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Interesting.");
			setPhase(3);
			break;
		}
	}
}
