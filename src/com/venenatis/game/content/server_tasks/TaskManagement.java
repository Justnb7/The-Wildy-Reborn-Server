package com.venenatis.game.content.server_tasks;

import java.util.HashMap;

import com.venenatis.game.model.entity.player.Player;

public class TaskManagement {
	
	private final Player player;
	
	TaskManagement(Player player) {
		this.player = player;
	}
	
	private HashMap<Tasks, Integer> task;

	/**
	 * Randomizes a task based upon a players combat level
	 */
	public void assignTask() {
		if(player.getCombatLevel() >= 3 && player.getCombatLevel() <= 55) {
			//player.setPlayerTask(task);
			//TODO Asign an random easy task
		} else if(player.getCombatLevel() > 55 && player.getCombatLevel() <= 100) {
			//TODO Asign an medium task
		} else if(player.getCombatLevel() > 100 && player.getCombatLevel() <= 115) {
			//TODO Asign an hard task
		} else if(player.getCombatLevel() > 115) {
			//TODO Asign an elite task
		}
		
	}
	
	public void activateTask(Tasks task, int increase) {
		if (increase == -1) {
			return;
		}
		
		if (task == null) {
			return;
		}
		
		if (player.getPlayerTask().get(task) >= task.getCompleteAmount()) {
			return;
		}
		
		final int current = player.getPlayerTask().get(task);

		if (current == 0) {
			player.getActionSender().sendMessage("<col=297A29>You have started the achievement: " + task.getTask() + ".");
		}
		
		player.getPlayerTask().put(task, current + increase);

		if (player.getPlayerTask().put(task, current + increase) >= task.getCompleteAmount()) {
			int points = player.getAchievementsPoints();
			player.getActionSender().sendMessage("<col=297A29>Congratulations! You have completed your task. You now have " + points + " point" + (points == 1 ? "" : "s") + ".");
		}
	}

}
