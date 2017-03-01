package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;

public class RottenPotato extends Dialogue {

	/**
	 * 0 - Eat
	 * 1 - Slice
	 * 2 - Peel
	 * 3 - UseMash
	 */
	public static int option;
	
	@Override
	protected void start(Object... parameters) {
		if(option == 0) {
			send(Type.CHOICE, "Select Option", "Set all stats", "Wipe inventory", "Teleport to player", "Spawn aggressive NPC");
			setPhase(0);
		} else if(option == 1) {
			send(Type.CHOICE, "Select Option", "invulnerability", "invincible", "invisibility");
			setPhase(2);
		} else if(option == 2) {
			send(Type.CHOICE, "Select Option", "Bank menu", "Show AMEs", "AMEs for all!", "Spawn RARE!");
			setPhase(3);
		} else if(option == 3) {
			send(Type.CHOICE, "Select Option", "Keep me logged in.", "Kick me out.", "Kill me.", "Transmogrify me...");
			setPhase(4);
		}
	}
	
	@Override
	protected void select(int index) {
		if(getPhase() == 0) {
			switch(index) {
			case 1:
				
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			}
		}
	}

}
