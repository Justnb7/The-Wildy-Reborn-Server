package com.venenatis.game.model.entity.player.dialogue.impl.teleport;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The class which represents functionality for the agility teleports.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">_Patrick_</a>
 */
public class AgilityTeleports extends Dialogue {

	/**
	 * An array for all the dialogue strings.
	 */
	private static final String[] OPTION_1 = { "Gnome agility course", "Barbarian agility course", "Wilderness agility course (50+ wild)", "Nevermind" };

	/**
	 * An array for all corresponding dialogue strings which holds all the
	 * teleport locations.
	 */
	private static final int[][] OPTION_1_TELEPORT = {
			{ 2480, 3437, 0 }, //Gnome course
			{ 2546, 3551, 0 }, // Barbarian course
			{ 2998, 3915, 0 } // Wilderness course
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
				player.getActionSender().removeAllInterfaces();
			} else if(index == 3) {
				player.getActionSender().sendMessage("We currently do not have support yet for this agility course.");
			} else {
				player.getTeleportAction().teleport(new Location(OPTION_1_TELEPORT[index - 1][0], OPTION_1_TELEPORT[index - 1][1], OPTION_1_TELEPORT[index - 1][2]), TeleportTypes.SPELL_BOOK, false);
			}
		}
	}
}