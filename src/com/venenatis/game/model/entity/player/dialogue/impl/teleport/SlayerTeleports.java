package com.venenatis.game.model.entity.player.dialogue.impl.teleport;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The class which represents functionality for the slayer teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class SlayerTeleports extends Dialogue {

	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Slayer masters", "Slayer tower", "Relleka slayer dungeon", "Lumbridge slayer cave", "More..." };
	
	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_2 = { "Stronhold slayer cave", "Taverly Dungeon", "Brimhaven dungeon", "Back..." };

	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ 0, 0, 0 }, //Slayer masters
			{ 3427, 3538, 0},//Slayer tower
			{ 2805, 10001, 0 },//Relleka slayer dungeon
			{ 3167, 9572, 0 },//Lumbridge slayer cave
			{ 0, 0, 0 } // More
	};
	
	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_2_TELEPORT = {
			{ 2439, 9812, 0 }, //Stronhold slayer cave
			{ 2884, 9798, 0}, // Taverly Dungeon
			{ 2712, 9564, 0 }, //Brimhaven dungeon
			{ 0, 0, 0 },
	};

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
		phase = 0;
	}

	@Override
	public void select(int index) {
		System.out.println("Phase: " + phase + " index : " + index);
		
		if (phase == 0) {
			if (index == 5) {
				phase = 1;
				send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, OPTION_2[0], OPTION_2[1], OPTION_2[2], OPTION_2[3]);
			} else if(index == 1) {
					player.getDialogueManager().start("ENCHANTED_GEM_TELEPORT", player);
			} else {
				player.getTeleportAction().teleport(new Location(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]), TeleportTypes.SPELL_BOOK, false);
			}
		} else if (phase == 1) {
			if (index == 4) {
				phase = 0;
				send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
			} else {
				player.getTeleportAction().teleport(new Location(OPTION_2_TELEPORT[index - 1][0], OPTION_2_TELEPORT[index - 1][1], OPTION_2_TELEPORT[index - 1][2]), TeleportTypes.SPELL_BOOK, false);
			}
		}
	}

}
