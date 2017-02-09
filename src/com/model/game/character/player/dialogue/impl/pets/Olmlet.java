package com.model.game.character.player.dialogue.impl.pets;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * The Olmet pet chat dialogue
 * 
 * @author Patrick van Elderen
 *
 */
public class Olmlet extends Dialogue {
	
	private final int PET = 7519;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, PET, Expression.OLM_LAUGH, "Hee hee! What shall we talk about, human?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(Type.PLAYER, Expression.DEFAULT, "Where do creatures like you come from?");
			setPhase(1);
			break;
		case 1:
			send(Type.NPC, PET, Expression.OLM, "From eggs, of course! You can't make an olmlet", "without breaking an egg.");
			setPhase(2);
			break;
		case 2:
			send(Type.PLAYER, Expression.DEFAULT, "That's... informative. Thank you.");
			setPhase(3);
			break;
		case 3:
			send(Type.NPC, PET, Expression.OLM_LAUGH, "Hee hee! What's next, human?");
			setPhase(4);
			break;
		case 4:
			send(Type.PLAYER, Expression.DEFAULT, "You look like a dragon.");
			setPhase(5);
			break;
		case 5:
			send(Type.NPC, PET, Expression.OLM, "And humans look like monkeys. Badly shaved", "monkeys. What's your point, human?");
			setPhase(6);
			break;
		case 6:
			send(Type.PLAYER, Expression.DEFAULT, "Are you related to dragons?");
			setPhase(7);
			break;
		case 7:
			send(Type.NPC, PET, Expression.OLM, "My sire was an olm. I'm an olm. I don't go around", "asking you about your parents' species, do I?");
			setPhase(8);
			break;
		case 8:
			send(Type.PLAYER, Expression.DEFAULT, "no, I suppose you don't.");
			setPhase(9);
			break;
		case 9:
			send(Type.NPC, PET, Expression.OLM_LAUGH, "Hee hee! Let's change the subject before someone", "gets insulted. What shall we talk about instead, human?");
			setPhase(10);
			break;
		case 10:
			send(Type.PLAYER, Expression.DEFAULT, "Can you tell me secrets about your home?");
			setPhase(11);
			break;
		case 11:
			send(Type.NPC, PET, Expression.OLM, "Ooh, it was lovely. I lived in an eggshell.", "I was safe in there, dreaming of the life I would lead", "when I hatched, and the caverns I could rule.");
			setPhase(12);
			break;
		case 12:
			send(Type.NPC, PET, Expression.OLM, "Then suddenly I felt a trembling of the ground,", "and my shell shattered.");
			setPhase(13);
			break;
		case 13:
			send(Type.NPC, PET, Expression.OLM, "Through its cracks I saw the world for the first time,", "just in time to watch my sire die.");
			setPhase(14);
			break;
		case 14:
			send(Type.NPC, PET, Expression.OLM, "It was a terrible shock for a newly hatched olmlet,", "but I try not to let it affect my mood. So", "what else shall we talk about, human?");
			setPhase(15);
			break;
		case 15:
			send(Type.PLAYER, Expression.DEFAULT, "Maybe another time.");
			setPhase(16);
			break;
		case 16:
			stop();
			break;
		}
	}
	
	@Override
	protected void select(int index) {
		
	}

}
