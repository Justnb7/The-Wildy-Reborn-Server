package com.venenatis.game.content.skills.slayer.interfaceController;

import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * @author Harambe_ Class represents and handles the Slayer Interface
 *
 */
public class TaskInterface {
	
	public void write(Player player) {
		try {

			String currentTask = player.getSlayerTask() != null ? "" + player.getSlayerTask() + " X " + player.getSlayerTaskAmount() : "Nothing";
			player.getActionSender().sendString("" + currentTask, 23208);

			for (int i = 0; i < 6; i++) {
				player.getActionSender().sendString("Empty", 23220 + i);
				player.getActionSender().sendString("<col=-8434673>Unblock Task</col>", 23232 + i);
				player.getActionSender().sendString(" " + player.getSlayerInterface().getBlockedTasks().get(i), 23220 + i);
				player.getActionSender().sendString("<col=ffa500>Unblock Task </col>", 23232 + i);
			}

		} catch (Exception name) {

		}
	}

}