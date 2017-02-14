package com.model.game.character.player.skill.slayer;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.skill.slayer.tasks.Nieve;
import com.model.game.character.player.skill.slayer.tasks.Task;
import com.model.game.character.player.skill.slayer.tasks.Turael;
import com.model.game.character.player.skill.slayer.tasks.Vannaka;
import com.model.game.character.player.skill.slayer.tasks.Chaeldar;
import com.model.game.character.player.skill.slayer.tasks.Duradel;
import com.model.game.character.player.skill.slayer.tasks.Mazchna;
import com.model.utility.Utility;

/**
 * The class represents functionality for the slayer skill.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class SlayerTaskManagement {
	
	/**
	 * This method allows the player to teleport to it's slayer task.
	 * 
	 * @param player
	 * @return task
	 */
	public static void teleToTask(Player player) {
		switch(player.getSlayerTask()) {
		case 239: // King black dragon
			
			break;
			default:
			player.write(new SendMessagePacket("You have no slayer task to teleport to."));	
		}
	}

	
	/**
	 * This method allows you to reset your current slayer task.
	 * @param player
	 * @return
	 */
	public static boolean resetTask(Player player) {
		if (!Slayer.hasTask(player)) {
			return false;
		}
		if(player.getItems().playerHasItem(13307, 10)) {
			player.setSlayerTask(0);
			player.setSlayerTaskAmount(0);
			player.getItems().deleteItem(13307, 10);
			player.write(new SendClearScreen());
			player.write(new SendMessagePacket("Your slayer task has been reset, talk to any slayer master for a new one."));
			return true;
		} else {
			player.write(new SendMessagePacket("You do not have enough blood money in order to reset your slayer task."));
			player.write(new SendClearScreen());
			return false;
		}
	}
	
	/**
	 * Randomises a beginner task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task beginnerTask(Player player) {
		Task task = null;
		int taskAmount = 5 + Utility.random(3, 10);
		do {
			task = Turael.values()[(int)(Math.random() * Turael.values().length)];
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(0);
		return task;
	}
	
	/**
	 * Randomises an easy task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task easyTask(Player player) {
		Task task = null;
		int taskAmount = 13 + Utility.random(5, 10);
		do {
			task = Mazchna.values()[(int)(Math.random() * Mazchna.values().length)];
			if (task.getId() == player.getLastSlayerTask()) {
				task = Mazchna.values()[(int)(Math.random() * Mazchna.values().length)];
			}
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(1);
		player.setLastSlayerTask(task.getId());
		return task;
	}
	
	/**
	 * Randomises a medium task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task mediumTask(Player player) {
		Task task = null;
		int taskAmount = 25 + Utility.random(5, 15);
		do {
			task = Vannaka.values()[(int)(Math.random() * Vannaka.values().length)];
			if (task.getId() == player.getLastSlayerTask()) {
				task = Vannaka.values()[(int)(Math.random() * Vannaka.values().length)];
			}
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(2);
		player.setLastSlayerTask(task.getId());
		return task;
	}
	
	/**
	 * Randomises a hard task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task hardTask(Player player) {
		Task task = null;
		int taskAmount = 25 + Utility.random(15, 35);
		do {
			task = Chaeldar.values()[(int)(Math.random() * Chaeldar.values().length)];
			if (task.getId() == player.getLastSlayerTask()) {
				task = Chaeldar.values()[(int)(Math.random() * Chaeldar.values().length)];
			}
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(3);
		player.setLastSlayerTask(task.getId());
		return task;
	}
	
	/**
	 * Randomises an elite task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task eliteTask(Player player) {
		Task task = null;
		int taskAmount = 25 + Utility.random(15, 35);
		do {
			task = Nieve.values()[(int)(Math.random() * Nieve.values().length)];
			if (task.getId() == player.getLastSlayerTask()) {
				task = Nieve.values()[(int)(Math.random() * Nieve.values().length)];
			}
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(4);
		player.setLastSlayerTask(task.getId());
		return task;
	}
	
	/**
	 * Randomises a boss task.
	 * 
	 * @param player
	 * @return task
	 */
	public static Task bossTask(Player player) {
		Task task = null;
		int taskAmount = 25 + Utility.random(15, 35);
		do {
			task = Duradel.values()[(int)(Math.random() * Duradel.values().length)];
			if (task.getId() == player.getLastSlayerTask()) {
				task = Duradel.values()[(int)(Math.random() * Duradel.values().length)];
			}
		} while (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER));
		player.setSlayerTask(task.getId());
		player.setSlayerTaskAmount(taskAmount);
		player.setSlayerTaskDifficulty(5);
		player.setLastSlayerTask(task.getId());
		return task;
	}
	
	/**
	 * Decrease the slayer task, and reward the player when finished.
	 * @param player
	 *        The player killing the task
	 * @param npc
	 *        The slayer task
	 */
	public static void decreaseTask(Player player, Npc npc) {
		
		//Safety check
		if (player != null) {
			player = World.getWorld().getPlayers().get(npc.killedBy);
			
			//Decrease task
			if (player.getSlayerTask() == npc.npcId) {
				player.setSlayerTaskAmount(player.getSlayerTaskAmount() - 1);
				player.getSkills().addExperience(Skills.SLAYER, npc.maximumHealth);
				//player.write(new SendMessagePacket("Slayertask: "+Npc.getName(npc.npcId)+ " left: "+player.getSlayerTaskAmount()));
				player.write(new SendString("<img=28><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+Npc.getName(player.getSlayerTask()), 29511));
			}
			
			//Kills left messages
			if(player.getSlayerTaskAmount() == 25) {
				player.write(new SendMessagePacket("You still have to kill 25 more "+Npc.getName(npc.npcId)));
			} else if(player.getSlayerTaskAmount() == 10) {
				player.write(new SendMessagePacket("You still have to kill 10 more "+Npc.getName(npc.npcId)));
			}
			
			// The player has completed their task, we can go ahead and reward them.
			if (player.getSlayerTaskAmount() <= 0) {
				
				player.setSlayerTask(0);
				player.setSlayerTaskAmount(0);
				player.setSlayerTasksCompleted(player.getSlayerTasksCompleted() + 1);
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
				
				/**
				 * Beginner task (Turael).
				 */
				if (player.getSlayerTaskDifficulty() == 0) {
					player.write(new SendMessagePacket("You have completed your slayer assignment. You don't gain any slayer"));
					player.write(new SendMessagePacket("points for completing a task with Turael. Go back and speak to Turael"));
					player.write(new SendMessagePacket("to get a new assignment."));

					/**
					 * Easy task (Mazchna).
					 */
				} else if (player.getSlayerTaskDifficulty() == 1) {
					if (Constants.SLAYER_REWARDS) {
						player.write(new SendMessagePacket("You have completed your slayer assignment. Double Slayer points is active"));
						player.write(new SendMessagePacket("meaining you get @blu@4@bla@ Slayer Points. Please speak to a Slayer Master"));
						player.write(new SendMessagePacket("to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 4);
					} else {
						player.write(new SendMessagePacket("You have completed your slayer assignment. You gain @blu@2@bla@ Slayer Points!"));
						player.write(new SendMessagePacket("Please speak to a Slayer Master to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 2);
					}

					/**
					 * Medium task (Vannaka).
					 */
				} else if (player.getSlayerTaskDifficulty() == 2) {
					if (Constants.SLAYER_REWARDS) {
						player.write(new SendMessagePacket("You have completed your slayer assignment. Double Slayer points is active"));
						player.write(new SendMessagePacket("meaining you get @blu@8@bla@ Slayer Points. Please speak to a Slayer Master"));
						player.write(new SendMessagePacket("to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 8);
					} else {
						player.write(new SendMessagePacket("You have completed your slayer assignment. You gain @blu@4@bla@ Slayer Points!"));
						player.write(new SendMessagePacket("Please speak to a Slayer Master to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 4);
					}
					/**
					 * Hard task (Chaeldar).
					 */
				} else if (player.getSlayerTaskDifficulty() == 3) {
					if (Constants.SLAYER_REWARDS) {
						player.write(new SendMessagePacket("You have completed your slayer assignment. Double Slayer points is active"));
						player.write(new SendMessagePacket("meaining you get @blu@12@bla@ Slayer Points. Please speak to a Slayer Master"));
						player.write(new SendMessagePacket("to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 12);
					} else {
						player.write(new SendMessagePacket("You have completed your slayer assignment. You gain @blu@6@bla@ Slayer Points!"));
						player.write(new SendMessagePacket("Please speak to a Slayer Master to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 6);
					}

					/**
					 * Elite task (Nieve).
					 */
				} else if (player.getSlayerTaskDifficulty() == 4) {
					if (Constants.SLAYER_REWARDS) {
						player.write(new SendMessagePacket("You have completed your slayer assignment. Double Slayer points is active"));
						player.write(new SendMessagePacket("meaining you get @blu@16@bla@ Slayer Points. Please speak to a Slayer Master"));
						player.write(new SendMessagePacket("to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 16);
					} else {
						player.write(new SendMessagePacket("You have completed your slayer assignment. You gain @blu@8@bla@ Slayer Points!"));
						player.write(new SendMessagePacket("Please speak to a Slayer Master to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 8);
					}

					/**
					 * Boss task (Duradel).
					 */
				} else if (player.getSlayerTaskDifficulty() == 5) {
					if (Constants.SLAYER_REWARDS) {
						player.write(new SendMessagePacket("You have completed your slayer assignment. Double Slayer points is active"));
						player.write(new SendMessagePacket("meaining you get @blu@20@bla@ Slayer Points. Please speak to a Slayer Master"));
						player.write(new SendMessagePacket("to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 20);
					} else {
						player.write(new SendMessagePacket("You have completed your slayer assignment. You gain @blu@10@bla@ Slayer Points!"));
						player.write(new SendMessagePacket("Please speak to a Slayer Master to retrieve an another assignment."));
						player.setSlayerPoints(player.getSlayerPoints() + 10);
					}
				}
			}
		}
	}
}