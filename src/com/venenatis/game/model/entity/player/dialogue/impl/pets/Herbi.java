package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Herbi extends Dialogue {
	
	private final int PET = 7759;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(4);
		if(random == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Are you hungry?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Have your herbs died?");
			setPhase(8);
		} else if(random == 2) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "So you live in a hole? I would've thought Boars are surface dwelling mammals.");
			setPhase(12);
		} else if(random == 3) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Tell me... do you like Avacado?");
			setPhase(15);
		} else {
			send(DialogueType.NPC, PET, Expression.DEFAULT, "When I was a young HERBIBOAR!!");
			setPhase(19);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "That depends, what have you got?");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'm sure I could knock you up a decent salad.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I'm actually an insectivore.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh, but your name suggests that-");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I think you'll find I didn't name myself, you humans and your silly puns.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "No need to PUNish us for our incredible wit.");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Please. Stop.");
			setPhase(7);
			break;
		case 7:
			stop();
			break;
		case 8:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "These old things? I guess they've dried up... I'm getting old and I need caring for. I've chosen you to do that", "by the way.");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh fantastic! I guess I'll go shell out half a million coins to keep you safe then, what superb luck!");
			setPhase(10);
			break;
		case 10:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I could try the next person if you'd prefer?");
			setPhase(11);
			break;
		case 11:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'm just joking you old swine!");
			setPhase(7);
			break;
		case 12:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Well, I'm special! I bore down a little so I'm nice and cosy with my herbs exposed to the sun, it's all very", "interesting.");
			setPhase(13);
			break;
		case 13:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sounds rather... Boring!");
			setPhase(14);
			break;
		case 14:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "How very original...");
			setPhase(7);
			break;
		case 15:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I'm an insectivore, but even if I wasn't I'd hate Avacado!");
			setPhase(16);
			break;
		case 16:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why ever not? It's delicious!");
			setPhase(17);
			break;
		case 17:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I don't know why people like it so much... it tastes a like a ball of chewed up grass.");
			setPhase(18);
			break;
		case 18:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sometimes you can be such a bore...");
			setPhase(7);
			break;
		case 19:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'm standing right next to you, no need to shout...");
			setPhase(20);
			break;
		case 20:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I was trying to sing you a song...");
			setPhase(7);
			break;
		}
	}

}