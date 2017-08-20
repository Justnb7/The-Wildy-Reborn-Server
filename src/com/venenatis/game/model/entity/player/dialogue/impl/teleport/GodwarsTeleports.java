package com.venenatis.game.model.entity.player.dialogue.impl.teleport;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The class which represents functionality for the godwars dungeon chamber teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class GodwarsTeleports extends Dialogue {
	
	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Armadyl", "Bandos", "Saradomin", "Zamorak", "Nevermind" };

	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ 2839, 5290, 2 }, // Armadyl
			{ 2858, 5354, 2 }, // Bandos
			{ 2912, 5268, 0 }, // Saradomin
			{ 2925, 5341, 2 }, // Zamorak
			{ 0, 0, 0 } // Nevermind
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
			if(index == 5) {
				player.getActionSender().removeAllInterfaces();
			} else {
				player.getTeleportAction().teleport(new Location(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]), TeleportTypes.SPELL_BOOK, false);
			}
		}
	}

}
