package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Phoenix extends Dialogue {
	
	private final int PET = 7368;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(3);
		
		if(random == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "So... The Pyromancers, they're cool, right?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.NPC, PET, Expression.PHOENIX, "...");
			setPhase(6);
		} else if(random == 2) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Who's a pretty birdy?");
			player.message("The Phoenix Gives you a smouldering look.");
			setPhase(5);
		} else {
			send(DialogueType.NPC, PET, Expression.PHOENIX, "One day I will burn so hot I'll become Sacred Ash");
			setPhase(12);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "We share a common goal..");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Which is?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "Keeping the cinders burning and preventing the long night from swallowing us all.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "That sounds scary.");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "As long as we remain vigilant and praise the Sun, all will be well.");
			setPhase(5);
			break;
		case 5:
			stop();
			break;
		case 6:
			send(DialogueType.PLAYER, Expression.DEFAULT, "What are you staring at?");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "The great Sol Supra.");
			setPhase(8);
			break;
		case 8:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Is that me?");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "No mortal. The Sun, as you would say.");
			setPhase(10);
			break;
		case 10:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Do you worship it?");
			setPhase(11);
			break;
		case 11:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "It is wonderous... If only I could be so grossly incandescent.");
			setPhase(5);
			break;
		case 12:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Aww, but you're so rare, where would I find another?");
			setPhase(13);
			break;
		case 13:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "Do not fret mortal, I will rise from the Sacred Ash greater than ever before.");
			setPhase(14);
			break;
		case 14:
			send(DialogueType.PLAYER, Expression.DEFAULT, "So you're immortal?");
			setPhase(15);
			break;
		case 15:
			send(DialogueType.NPC, PET, Expression.PHOENIX, "As long as the Sun in the sky gives me strength.");
			setPhase(16);
			break;
		case 16:
			send(DialogueType.PLAYER, Expression.DEFAULT, "...Sky?");
			setPhase(5);
			break;
		}
	}

}