package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

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
			send(Type.CHOICE, "Select Option", "Set all stats", "Wipe inventory");
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
				for (int i = 0; i < Skills.SKILL_COUNT; i++) {
					player.getSkills().setLevel(i, 99);
					player.getSkills().setExperience(i, 13034431);
					player.getActionSender().sendSkillLevel(i);
				}
				
	    		player.setCombatLevel(player.getSkills().getCombatLevel());
	    		stop();
				break;
			case 2:
				player.getInventory().clear(true);
	    		player.getInventory().refresh();
				player.getInventory().add(new Item(5733));
				stop();
				break;
			case 3:
				break;
			case 4:
				break;
			}
		}
	}

}
