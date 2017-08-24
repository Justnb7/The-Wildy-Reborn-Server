package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

public class MagicBook extends Dialogue {
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Modern", "Ancients", "Lunars", "Nevermind");
	}

	@Override
	public void next() {

	}

	@Override
	public void select(int index) {
		switch(index) {
		case 1: //Normal spellbook option
			player.getActionSender().removeAllInterfaces();
			player.setSpellBook(SpellBook.MODERN_MAGICS);
			player.getActionSender().sendSidebarInterface(6, 1151);
			stop();
			break;
		case 2: //Ancient spellbook option
			player.getActionSender().removeAllInterfaces();
			player.setSpellBook(SpellBook.ANCIENT_MAGICKS);
			player.getActionSender().sendSidebarInterface(6, 12855);
			stop();
			break;
		case 3: //Lunar spellbook option
			player.getActionSender().removeAllInterfaces();
			player.setSpellBook(SpellBook.LUNAR_MAGICS);
			player.getActionSender().sendSidebarInterface(6, 29999);
			stop();
			break;
		case 4: //Cancel option
			player.getActionSender().removeAllInterfaces();
			stop();
			break;
		}
	}

}
