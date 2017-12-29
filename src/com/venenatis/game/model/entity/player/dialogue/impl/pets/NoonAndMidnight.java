package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class NoonAndMidnight extends Dialogue {
	
	private final int NOON = 7891;
	
	private final int MIDNIGHT = 7893;
	
	private final Random random = new Random();

	@Override
	protected void start(Object... parameters) {
		if(player.getPet() == 7891) {
			int random_identifier = random.nextInt(2);
			if(random_identifier == 0) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Hello little one.");
				setPhase(0);
			} else if(random_identifier == 1) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "What's your favourite rock?");
				setPhase(2);
			} else if(random_identifier == 2) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Metaphorically speaking, do you have a heart of stone?");
				setPhase(4);
			}
		} else {
			int random_identifier = random.nextInt(2);
			if(random_identifier == 0) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Hello little one.");
				setPhase(5);
			} else if(random_identifier == 1) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Sometimes I'm worried you'll attack me whilst my back is", "turned.");
				setPhase(8);
			} else if(random_identifier == 2) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "I feel like our relationship is slowly eroding away.");
				setPhase(11);
			}
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, NOON, Expression.DEFAULT, "I may be small but at least I'm perfectly formed.");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, NOON, Expression.DEFAULT, "You're going tufa with that question. That's personal.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Was just trying to make light conversation, not trying to aggregate you.");
			setPhase(1);
			break;
		case 4:
			send(DialogueType.NPC, NOON, Expression.DEFAULT, "Yes, but you're not having it.");
			setPhase(1);
			break;
		case 5:
			send(DialogueType.NPC, MIDNIGHT, Expression.DEFAULT, "Other?");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Yes, don't you have a sister?");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.NPC, MIDNIGHT, Expression.DEFAULT, "I don't want to chalk about it.");
			setPhase(1);
			break;
		case 8:
			send(DialogueType.NPC, MIDNIGHT, Expression.DEFAULT, "Are you petrified of my tuffness?");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Not really, but your puns are awful.");
			setPhase(10);
			break;
		case 10:
			send(DialogueType.NPC, MIDNIGHT, Expression.DEFAULT, "I thought they were clastic.");
			setPhase(1);
			break;
		case 11:
			send(DialogueType.NPC, MIDNIGHT, Expression.DEFAULT, "Geode willing.");
			setPhase(1);
			break;
		}
	}

}
