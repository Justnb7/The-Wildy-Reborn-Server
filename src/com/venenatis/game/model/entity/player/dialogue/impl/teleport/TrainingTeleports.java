package com.venenatis.game.model.entity.player.dialogue.impl.teleport;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The class which represents functionality for the training teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class TrainingTeleports extends Dialogue {
	
	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Rock crabs", "Hill giants", "Mithril dragons", "Nevermind" };

	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ 2674, 3712, 0 }, //Rock crabs
			{ 3113, 9837, 0 }, //Hill giants
			{ 1748, 5330, 0 } //Mithril dragons
	};

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3]);
		phase = 0;
	}

	@Override
	public void select(int index) {
		System.out.println("Phase: " + phase + " index : " + index);
		if (phase == 0) {
			if(index == 4) {
				player.getActionSender().removeAllInterfaces();
			} else {
				player.getTeleportAction().teleport(new Location(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]), TeleportTypes.SPELL_BOOK, false);
			}
		}
	}
}
