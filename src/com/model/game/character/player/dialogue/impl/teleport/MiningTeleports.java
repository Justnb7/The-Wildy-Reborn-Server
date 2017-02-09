package com.model.game.character.player.dialogue.impl.teleport;

import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.location.Location;

/**
 * The class which represents functionality for the mining teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class MiningTeleports extends Dialogue {

	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Lumbridge", "Varrock", "Falador", "Nevermind" };

	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ 3148, 3155, 0 }, //Lumbridge
			{ 3285, 3371, 0 }, // Varrock
			{ 3044, 9785, 0 } // Falador
	};

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3]);
		phase = 0;
	}

	@Override
	public void select(int index) {
		System.out.println("Phase: " + phase + " index : " + index);
		if (phase == 0) {
			if(index == 4) {
				player.write(new SendClearScreen());
			} else {
				TeleportExecutor.teleport(player, new Location(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]));
			}
		}
	}
}