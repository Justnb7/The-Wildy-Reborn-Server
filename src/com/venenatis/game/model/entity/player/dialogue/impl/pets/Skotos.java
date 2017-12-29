package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class Skotos extends Dialogue {
	
	//TODO
	private boolean usingLightSource() {
		return false;
	}
	
	private final int PET = 425;

	@Override
	protected void start(Object... parameters) {
		if (usingLightSource()) {
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I told thee to keep thy filthy light away from me!");
			setPhase(19);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "You look cute.");
			setPhase(0);
		}
	}

	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I do not thinke thou understand the depths ", "of the darkness you have unleased upon the world. To dub it in such a scintillant manner is offensive ", "to mine being.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "So why are you following me around.");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Dark forces of which ye know nought have deemed that this is my geas.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Your goose?");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Sighs* Nae. But thine is well and truly cooked.");
			setPhase(5);
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Where did you come from?");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "I am spawned of darkness. I am filled with darkness. I am darkness incarnate and to", "darkness I will return.");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sounds pretty... dark.");
			setPhase(8);
			break;
		case 8:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Knowest thou not of the cursed place? Knowest thou not about the future yet to befall your", "puny race?");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh yes, I've heard that before.");
			setPhase(10);
			break;
		case 11:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Then it is good that ye can laugh in the face of the end.");
			setPhase(12);
			break;
		case 12:
			send(DialogueType.PLAYER, Expression.DEFAULT, "The end has a face? Which end?");
			setPhase(13);
			break;
		case 14:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "*Sighs* The darkness giveth, and the darkness taketh.");
			setPhase(15);
			break;
		case 15:
			send(DialogueType.PLAYER, Expression.DEFAULT, "What can you do for me?");
			setPhase(16);
			break;
		case 16:
			send(DialogueType.NPC, PET, Expression.DEFAULT, " Nothing. Ye are already tainted in my sight by the acts of light. However they may be some", "hope for you if you continue to aid the darkness.");
			setPhase(17);
			break;
		case 17:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I do have a lantern around here somewhere.");
			setPhase(18);
			break;
		case 18:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Do not bring that foul and repellant thing near mine self.");
			setPhase(19);
			break;
		case 19:
			stop();
			break;
		}
	}
	
}