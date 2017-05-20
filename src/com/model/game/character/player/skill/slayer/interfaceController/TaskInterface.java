package com.model.game.character.player.skill.slayer.interfaceController;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;

/**
 * 
 * @author Harambe_ Class represents and handles the Slayer Interface
 *
 */
public class TaskInterface {

	public void write(Player player) {
		try {

			String currentTask = player.getSlayerTask() > 0
					? "" + NPC.getName(player.getSlayerTask()) + " X " + player.getSlayerTaskAmount() : "Nothing";
			player.getActionSender().sendString("" + currentTask, 23208);

			for (int i = 0; i < 6; i++) {
				player.getActionSender().sendString("Empty", 23220 + i);
				player.getActionSender().sendString("<col=-8434673>Unblock Task</col>", 23232 + i);
				player.getActionSender()
						.sendString(" " + NPC.getName(player.getSlayerInterface().getBlockedTasks().get(i)), 23220 + i);
				player.getActionSender().sendString("<col=ffa500>Unblock Task </col>", 23232 + i);
			}

		} catch (Exception name) {

		}
	}

}