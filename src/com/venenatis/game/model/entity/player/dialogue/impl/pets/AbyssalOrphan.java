package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class AbyssalOrphan extends Dialogue {
	
	private final int PET = 5883;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, PET, Expression.DEFAULT, "You killed my father.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Yeah, don't take it personally", "No, I am your father.");
			setPhase(1);
			break;
		case 2:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Yeah, don't take it personally.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "In his dying moment, my father poured his", "last ounce of strength into my creation. My being is formed", "from his remains.");
			setPhase(4);
			break;
		case 4:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "When your own body is consumed to", "nourish the Nexus, and an army of scions arises from your", "corpse, I trust you will not take it personally either.");
			setPhase(5);
			break;
		case 5:
			stop();
			break;
		case 6:
			send(DialogueType.PLAYER, Expression.DEFAULT, "No, I am your father.");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "No you're not.");
			setPhase(5);
			break;
		case 8:
			send(DialogueType.PLAYER, Expression.DEFAULT, "No, I am your father.");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.NPC, PET, Expression.DEFAULT, "Human biology may be unfamiliar to me, but nevertheless I doubt that very much.");
			setPhase(5);
			break;
		}
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 1) {
			switch (index) {
			case 1:
				setPhase(2);
				break;
			case 2:
				//Male
				if(player.getAppearance().getGender() == 0) {
					setPhase(6);
				//Female
				} else {
					setPhase(8);
				}
				break;
			}
		}
	}
}