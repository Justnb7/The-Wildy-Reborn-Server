package com.model.game.character.player.dialogue.impl.teleport;

import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.location.Position;

/**
 * The class which represents functionality for the clue scroll teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class TeleportCard extends Dialogue {
	
	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Ranging guild", "Seers village", "Mcgrubors woods", "West Ardougne", "Next" };
	
	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_2 = { "Clock tower", "Rimmington", "Previous" };
	
	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ -1, -1, 0 }, //Ranging guild area
			{ -1, -1, 0 }, //Seers
			{ -1, -1, 0 }, //Mcgrubors woods
			{ -1, -1, 0 }, //West Ardougne
			{ 0, 0, 0 } //Next
	};
	
	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_2_TELEPORT = {
			{ -1, -1, 0 }, //Clock tower
			{ -1, -1, 0 }, //Rimmington
			{ 0, 0, 0 } //Previous
	};

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
		phase = 0;
	}
	
	@Override
	public void select(int index) {
		System.out.println("Phase: " + phase + " index : " + index);
		if (phase == 0) {
			if (index == 5) {
				phase = 1;
				send(Type.CHOICE, DEFAULT_OPTION_TITLE, OPTION_2[0], OPTION_2[1], OPTION_2[2], OPTION_2[3], OPTION_2[4]);
			} else {
				TeleportExecutor.teleport(player, new Position(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]));
			}
		} else if (phase == 1) {
			if (index == 3) {
				phase = 0;
				send(Type.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
			} else {
				TeleportExecutor.teleport(player, new Position(OPTION_2_TELEPORT[index - 1][0], OPTION_2_TELEPORT[index - 1][1], OPTION_2_TELEPORT[index - 1][2]));
			}
		}
	}

}
