package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import java.util.Random;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class RiftGuardian extends Dialogue {
	
	/**
	 * The pet identifier
	 */
	private int petId;
	
	private final Random r = new Random();

	@Override
	protected void start(Object... parameters) {
		int random = r.nextInt(2); 
		
		if(random == 0) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Can you see your own rift?");
			setPhase(0);
		} else if(random == 1) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Where would you like me to take you today Rifty?");
			setPhase(2);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "Hey! What's that!");
			player.message("You quickly poke your hand through the rift guardian's rift.");
			setPhase(4);
		}
	}
	
	@Override
	protected void next() {
		petId = player.getPet();
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "No. From time to time I feel it shift and change inside me though. It is an odd feeling.");
			setPhase(1);
			break;
		case 1:
			stop();
			break;
		case 2:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Please do not call me that... we are a species of honour "+player.getUsername()+".");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sorry.");
			setPhase(1);
			break;
		case 4:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Huh, what?! Where?");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Not the best guardian it seems.");
			setPhase(1);
			break;
		}
	}
}