package com.venenatis.game.content.skills.slayer;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.skills.slayer.tasks.Chaeldar;
import com.venenatis.game.content.skills.slayer.tasks.Duradel;
import com.venenatis.game.content.skills.slayer.tasks.Mazchna;
import com.venenatis.game.content.skills.slayer.tasks.Nieve;
import com.venenatis.game.content.skills.slayer.tasks.Task;
import com.venenatis.game.content.skills.slayer.tasks.Turael;
import com.venenatis.game.content.skills.slayer.tasks.Vannaka;
import com.venenatis.game.content.teleportation.TeleportExecutor;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.util.RandomGenerator;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * The class represents functionality for the slayer task.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class SlayerTaskManagement {
	
	
	/**
	 * This method allows you to reset your current slayer task.
	 * @param player
	 * @return
	 */
	public static boolean resetTask(Player player) {
		if (!Slayer.hasTask(player)) {
			return false;
		}
		if(player.getSlayerPoints() >= 10) {
			player.setSlayerTask(0);
			player.setSlayerTaskAmount(0);
			player.setSlayerStreak(0);
			player.setSlayerPoints(player.getSlayerPoints() - 10);
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendMessage("Your slayer task and streak has been reset , talk to any slayer master for a new one.");
			return true;
		} else {
			player.getActionSender().sendMessage("You do not have enough Slayer Points to reset your slayer task.");
			player.getActionSender().removeAllInterfaces();
			return false;
		}
	}
	
	public static int extension(Player player, int id){
		if(player.getSlayerInterface().getUnlocks().containsKey(id) || id == 270 || id == 272 || id == 274) {
			System.out.println("Adding extra points");
			return Utility.random(10, 35);
		}
		return 0;
	}
	
	/**
	 * Randomises a beginner task.
	 * 
	 * @param player
	 * @return task
	 */
	
	// checking if the task exists
	/*else for (Entry<Integer, String> entrys : player.getSlayerInterface().getUnlocks().entrySet()) {
		ButtonData button = ButtonData.buttonMap.get(entrys.getKey());
		if(button.getAction() == Action.UNLOCK_BUTTON) {
		for(int i = 0; i < button.getunlockId().length; i++){
			if(button.getunlockId()[i] != t.getId()) {
				
			}
			if(button.getunlockId()[i] == t.getId()) {
			System.out.println("PRINTING I: "+i);
			System.out.print("We found it");
			} else {
				break;
			}
		}
	}	
} */	
	public static Task beginnerTask(Player player) {
		Task task = null;
		int taskAmount = 5 + Utility.random(3, 10);
		int currentCount = 1;
		int totalPercentage = 1;
		int total = Turael.getTotal();
			ArrayList<Turael> array = new ArrayList<Turael>();
			for (Turael t : Turael.values()) {		
				if(t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
					if (player.getSlayerInterface().getBlockedTasks().contains(t.getId())) {
						System.out.println("Skipping: "+t.getId());
						total = total - t.getWeight();
						}	else {
							
						array.add(t);
						}
			}
		}
			RandomGenerator r = new RandomGenerator();
			int random = r.exclusive(1, 100);
			
			
			for (int i =0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double)array.get(i).getWeight()/(double)total) * 100));
			System.out.print("NPC "+array.get(i).name()+ " Percentage:  "+array.get(i).getPercentage()+"\n");
			totalPercentage += array.get(i).getPercentage();
		}
			System.out.println("\nRANDOM PERCENT ROLL: "+random);
		for (int i =0; i < array.size(); i++) {
					System.out.println("NAME "+array.get(i).name()+" ID: "+array.get(i).getId()+" CURRENT COUNT: "+currentCount+" Current Count limit "
					+(currentCount+array.get(i).getPercentage())+" Percentage: "+array.get(i).getPercentage());
		currentCount += array.get(i).getPercentage();
		if(currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
			System.out.println("\nCURRENT COUNT: "+currentCount);
				System.out.println("Assignment Found["+i+"] ID: "+array.get(i).getId());
				if (array.get(i).getId() == player.getLastSlayerTask()) {
				//WHAT TO DO IF ITS SAME ASSIGNMENT
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(taskAmount);
				player.setSlayerTaskDifficulty(0);
				break;
			}
		}
		System.out.println("\nTOTAL PERCENT "+totalPercentage);
		System.out.println("TOTAL WEIGHT "+total);
		array.clear();
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
		int currentCount = 1;
		int totalPercentage = 0;
		int total = Mazchna.getTotal();
		ArrayList<Mazchna> array = new ArrayList<Mazchna>();
		for (Mazchna t : Mazchna.values()) {
			if (t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
				if (player.getSlayerInterface().getBlockedTasks().contains(t.getId())) {
					total = total - t.getWeight();
				} else {
					array.add(t);
				}
			}
		}
		RandomGenerator r = new RandomGenerator();
		int random = r.exclusive(1, 100);
		for (int i = 0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double) array.get(i).getWeight() / (double) total) * 100));
			totalPercentage += array.get(i).getPercentage();
		}
		for (int i = 0; i < array.size(); i++) {
			currentCount += array.get(i).getPercentage();
			if (currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
				if (array.get(i).getId() == player.getLastSlayerTask()) {
					task = Mazchna.values()[(int) (Math.random() * Mazchna.values().length)];
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(taskAmount);
				player.setSlayerTaskDifficulty(1);
				break;
			}
		}
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
		int currentCount = 1;
		int totalPercentage = 0;
		int total = Vannaka.getTotal();
		ArrayList<Vannaka> array = new ArrayList<Vannaka>();
		for (Vannaka t : Vannaka.values()) {
			if(t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
				if (player.getSlayerInterface().getBlockedTasks().contains(t.getId())) {
					total = total - t.getWeight();
					}	else {
						
					array.add(t);
					}
		}
	}
		RandomGenerator r = new RandomGenerator();
		int random = r.exclusive(1, 100);
			
		for (int i =0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double)array.get(i).getWeight()/(double)total) * 100));
			totalPercentage += array.get(i).getPercentage();
		}
		for (int i =0; i < array.size(); i++) {
			currentCount += array.get(i).getPercentage();
		if(currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
				if (array.get(i).getId() == player.getLastSlayerTask()) {
					task = Vannaka.values()[(int)(Math.random() * Vannaka.values().length)];
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(taskAmount);
				player.setSlayerTaskDifficulty(2);
				break;
			}
			
		}
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
		int currentCount = 1;
		int totalPercentage = 1;
		int total = Chaeldar.getTotal();
		ArrayList<Chaeldar> array = new ArrayList<Chaeldar>();
		for (Chaeldar t : Chaeldar.values()) {
			if(t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
				if (player.getSlayerInterface().getBlockedTasks().contains(t.getId())) {
					System.out.println("Skipping: "+t.getId());
					total = total - t.getWeight();
					}	else {
						
					array.add(t);
					}
		}
	}
		RandomGenerator r = new RandomGenerator();
		int random = r.exclusive(1, 100);
			
		for (int i =0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double)array.get(i).getWeight()/(double)total) * 100));
			totalPercentage += array.get(i).getPercentage();
		} 
		for (int i =0; i < array.size(); i++) {
			currentCount += array.get(i).getPercentage();
			if(currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
				if (array.get(i).getId() == player.getLastSlayerTask()) {
					task = Chaeldar.values()[(int)(Math.random() * Chaeldar.values().length)];
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(taskAmount);
				player.setSlayerTaskDifficulty(3);
				break;
			}
			
		}
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
		int currentCount = 1;
		int totalPercentage = 1;
		int total = Nieve.getTotal();
		ArrayList<Nieve> array = new ArrayList<Nieve>();
		
		for (Nieve t : Nieve.values()) {
			if(t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
				if (player.getSlayerInterface().getBlockedTasks().contains(t.getId()) ||
						t.getId() == 2919 && !player.getSlayerInterface().getUnlocks().containsKey(91116) ||
						t.getId() == 247 && !player.getSlayerInterface().getUnlocks().containsKey(91115)) {
					System.out.println("Skipping: "+t.getId());
					total = total - t.getWeight();
					}	else {
					array.add(t);
					}
		}
	}
		RandomGenerator r = new RandomGenerator();
		int random = r.exclusive(1, 100);
			
		for (int i =0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double)array.get(i).getWeight()/(double)total) * 100));
			//System.out.print("NPC "+array.get(i).name()+ " Percentage:  "+array.get(i).getPercentage()+"\n");
			totalPercentage += array.get(i).getPercentage();
		} 
		
		
		for (int i =0; i < array.size(); i++) {
			currentCount += array.get(i).getPercentage();
		if(currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
				if (array.get(i).getId() == player.getLastSlayerTask()) {
					task = Nieve.values()[(int)(Math.random() * Nieve.values().length)];
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(assignTaskAmount(player, array.get(i).getId(), 4));
				player.setSlayerTaskDifficulty(4);
				break;
			}
			
		}
		return task;
	}
	
	public static int assignTaskAmount(Player player, int task, int difficulty) {
		int amount = difficulty == 0 ? 13 + Utility.random(5, 10) : difficulty == 1 ? 13 + Utility.random(5, 10) :
			difficulty == 2 ? 25 + Utility.random(5, 15) : difficulty == 3 ? 25 + Utility.random(15, 35) : 
				difficulty == 4 ? 25 + Utility.random(15, 35) : Utility.random(5, 35);
	for (Entry<Integer, Integer> entrys : player.getSlayerInterface().getExtensions().entrySet()) {
		if(entrys.getValue() == 270){
			if(task == 270 || task == 272 || task == 274){ //dragon extensions
				player.getActionSender().sendMessage("@red@You receive your task extension.");
				return (int) (amount * 1.5);
			}
		}
		if(entrys.getValue() == task) {
			player.getActionSender().sendMessage("@red@You receive your task extension.");
			return (int) (amount * 1.5);
			
		}
	}
		
		return amount;
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
		int currentCount = 1;
		int totalPercentage = 1;
		int total = Duradel.getTotal();
		ArrayList<Duradel> array = new ArrayList<Duradel>();
		for (Duradel t : Duradel.values()) {
			if(t.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)); {
				if (player.getSlayerInterface().getBlockedTasks().contains(t.getId())) {
					System.out.println("Skipping: "+t.getId());
					total = total - t.getWeight();
					}	else {
						
					array.add(t);
					}
		}
	}
		RandomGenerator r = new RandomGenerator();
		int random = r.exclusive(1, 100);
			
		for (int i =0; i < array.size(); i++) {
			array.get(i).setPercentage((int) Math.round(((double)array.get(i).getWeight()/(double)total) * 100));
			totalPercentage += array.get(i).getPercentage();
		}
		for (int i =0; i < array.size(); i++) {
			currentCount += array.get(i).getPercentage();
		if(currentCount >= random && (currentCount - array.get(i).getPercentage()) < random) {
				if (array.get(i).getId() == player.getLastSlayerTask()) {
					task = Duradel.values()[(int)(Math.random() * Duradel.values().length)];
				}
				player.setSlayerTask(array.get(i).getId());
				player.setSlayerTaskAmount(taskAmount);
				player.setSlayerTaskDifficulty(5);
				break;
			}
			
		}
		return task;
	}
	
	/**
	 * Decrease the slayer task, and reward the player when finished.
	 * @param player
	 *        The player killing the task
	 * @param npc
	 *        The slayer task
	 */
	public static void decreaseTask(Player player, NPC npc) {
		
		//Safety check
		if (player != null) {
			player = World.getWorld().getPlayers().get(npc.killedBy);
			
			//Decrease task
			if (player.getSlayerTask() == npc.getId() ||  NPC.getName(npc.getId()).toLowerCase().equalsIgnoreCase(NPC.getName(player.getSlayerTask()).toLowerCase())) {
				player.setSlayerTaskAmount(player.getSlayerTaskAmount() - 1);
				player.getSkills().addExperience(Skills.SLAYER, npc.getMaxHitpoints());
				//player.getActionSender().sendMessage("Slayertask: "+Npc.getName(npc.npcId)+ " left: "+player.getSlayerTaskAmount()));
				player.getActionSender().sendString("<img=28><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+NPC.getName(player.getSlayerTask()), 29511);
			}
			
			//Kills left messages
			if(player.getSlayerTaskAmount() == 25) {
				player.getActionSender().sendMessage("You still have to kill 25 more "+NPC.getName(npc.getId()));
			} else if(player.getSlayerTaskAmount() == 10) {
				player.getActionSender().sendMessage("You still have to kill 10 more "+NPC.getName(npc.getId()));
			}
			
			// The player has completed their task, we can go ahead and reward them.
			if (player.getSlayerTaskAmount() <= 0) {
				
				player.setSlayerTask(0);
				player.setSlayerTaskAmount(0);
		
				player.setSlayerTasksCompleted(player.getSlayerTasksCompleted() + 1);
				player.setSlayerStreak(player.getSlayerStreak() + 1);
				if(player.getSlayerStreak() > player.getSlayerStreakRecord()) {
					player.setSlayerStreakRecord(player.getSlayerStreak());
				}
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
				
				/**
				 * Beginner task (Turael).
				 */
				if (player.getSlayerTaskDifficulty() == 0) {
					player.getActionSender().sendMessage("You have completed your slayer assignment. You don't gain any slayer");
					player.getActionSender().sendMessage("points for completing a task with Turael. Go back and speak to Turael");
					player.getActionSender().sendMessage("to get a new assignment.");

					/**
					 * Easy task (Mazchna).
					 */
				} else if (player.getSlayerTaskDifficulty() == 1) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@"+(Mazchna.getStreak(player) > 0 ? Mazchna.getStreak(player) : 4)+"@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Mazchna.getStreak(player) > 0 ? Mazchna.getStreak(player) : 4));
						if(Mazchna.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Mazchna.getStreak(player)+" bonus slayer points.");
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@ "+(Mazchna.getStreak(player) > 0 ? Mazchna.getStreak(player) : 2)+"@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Mazchna.getStreak(player) > 0 ? Mazchna.getStreak(player) : 2));
						if(Mazchna.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Mazchna.getStreak(player)+" bonus slayer points.");
					}

					/**
					 * Medium task (Vannaka).
					 */
				} else if (player.getSlayerTaskDifficulty() == 2) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@"+(Vannaka.getStreak(player) > 0 ? Vannaka.getStreak(player) : 8)+"@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Vannaka.getStreak(player) > 0 ? Vannaka.getStreak(player) : 8));
						if(Vannaka.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Vannaka.getStreak(player)+" bonus slayer points.");
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@"+(Vannaka.getStreak(player) > 0 ? Vannaka.getStreak(player) : 4)+"@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Vannaka.getStreak(player) > 0 ? Vannaka.getStreak(player) : 4));
						if(Vannaka.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Vannaka.getStreak(player)+" bonus slayer points.");
					}
					/**
					 * Hard task (Chaeldar).
					 */
				} else if (player.getSlayerTaskDifficulty() == 3) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@"+(Chaeldar.getStreak(player) > 0 ? Chaeldar.getStreak(player) : 20)+"@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Chaeldar.getStreak(player) > 0 ? Chaeldar.getStreak(player) : 20));
						if(Chaeldar.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Chaeldar.getStreak(player)+" bonus slayer points.");
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@"+(Chaeldar.getStreak(player) > 0 ? Chaeldar.getStreak(player) : 10)+"@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Chaeldar.getStreak(player) > 0 ? Chaeldar.getStreak(player) : 10));
						if(Chaeldar.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Chaeldar.getStreak(player)+" bonus slayer points.");
					}

					/**
					 * Elite task (Nieve).
					 */
				} else if (player.getSlayerTaskDifficulty() == 4) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@"+(Nieve.getStreak(player) > 0 ? Nieve.getStreak(player) : 24)+"@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Nieve.getStreak(player) > 0 ? Nieve.getStreak(player) : 24));
						if(Nieve.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Nieve.getStreak(player)+" bonus slayer points.");
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@"+(Nieve.getStreak(player) > 0 ? Nieve.getStreak(player) : 12)+"@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Nieve.getStreak(player) > 0 ? Nieve.getStreak(player) : 12));
						if(Nieve.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Nieve.getStreak(player)+" bonus slayer points.");
					
					}

					/**
					 * Boss task (Duradel).
					 */
				} else if (player.getSlayerTaskDifficulty() == 5) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@"+(Duradel.getStreak(player) > 0 ? Duradel.getStreak(player) : 30)+"@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Duradel.getStreak(player) > 0 ? Duradel.getStreak(player) : 30));
						if(Duradel.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Duradel.getStreak(player)+" bonus slayer points.");
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@"+(Duradel.getStreak(player) > 0 ? Duradel.getStreak(player) :15)+"@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + (Duradel.getStreak(player) > 0 ? Duradel.getStreak(player) :15));
						if(Duradel.getStreak(player) > 0)
							player.getActionSender().sendMessage("You hit your streak of: "+player.getSlayerStreak()+" "
									+ "and received  "+Duradel.getStreak(player)+" bonus slayer points.");
					}
				}
				if(player.getSlayerStreak() % 1000 == 0) {
					PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + Utility.formatPlayerName(player.getName()) + " has just completed " + player.getSlayerStreak() + "x Slayer tasks in a row!");
				} else 	if(player.getSlayerStreak() % 250 == 0) {
					PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + Utility.formatPlayerName(player.getName()) + " has just completed " + player.getSlayerStreak() + "x Slayer tasks in a row!");
				}
				player.setSlayerTaskDifficulty(0);
			}
		}
	}

public enum Teleports {
		
		/**
		 * Turael
		 */
		BANSHEE(414, "Banshee", new Location(3441, 3565, 0)),
		BAT(2834, "Bat", new Location(2912, 9837, 0)),
		BIRDS(2692, "Chicken", new Location(3232, 3299, 0)),
		BEAR(3423, "Bear", new Location(2712, 3335, 0)),
		CAVE_BUGS(481, "Cave bug", new Location(3160, 9573, 0)),
		CAVE_CRAWLERS(406, "Cave crawler", new Location(3193, 9577, 0)),
		CAVE_SLIMES(480, "Cave slime", new Location(3158, 9592, 0)),
		COWS(2805, "Cow", new Location(3260, 3278, 0)),
		CRAWLING_HANDS(448, "Crawling hand", new Location(3411, 3537, 0)),
		DWARVES(291, "Dwarve", new Location(2918, 9758, 0)),
		GHOSTS(85, "Ghost", new Location(3277, 3658, 0)),
		GOBLINS(2245, "Goblin", new Location(2572, 3388, 0)),
		KALPHITES(955, "Kalphite", new Location(3760, 5854, 0)),
	
		/**
		 * Chaeldar
		 */
		ABYSSAL_DEMON(415, "Abyssal demon", new Location()),
		ABERRANT_SPECTRE(3, "Aberrant spectre", new Location(2393, 9780, 0)),
		BLACK_DEMON(1432, "Black demon", new Location(2701, 9490, 0)),
		HELLHOUND(135, "Hellhound", new Location(2407, 9785, 0)),
		CAVE_KRAKEN(492, "Cave kraken", new Location(2481, 9799, 0)),
		STEEL_DRAGON(274, "Steel dragon", new Location(2312, 9775, 0)),
		IRON_DRAGON(272, "Iron dragon", new Location(2326, 9736, 0)),
		ANKOU(2514, "Ankou", new Location(2376, 9758, 0)),
		BASILISK(417, "Basilisk", new Location(2742, 10002, 0)),
		BLUE_DRAGON(268, "Blue dragon", new Location(2894, 9794, 0)),
		BLOODVELD(484, "Bloodveld", new Location(2436, 9794, 0)),
		BRONZE_DRAGON(270, "Bronze dragon", new Location(2349, 9742, 0)),
		COCKATRICE(419, "Cockatrice", new Location(2799, 10030, 0)),
		FIRE_GIANT(2075, "Fire giant", new Location(2352, 9754, 0)),
		GARGOYLE(412, "Gargoyle", new Location(3434, 3539, 2)),
		GREEN_DRAGON(264, "Green dragon", new Location(2973, 3620, 0)),
		HILL_GIANT(2098, "Hill giant", new Location(3298, 3650, 0)),
		ICE_WARRIOR(2841, "Ice warrior", new Location(3036, 9541, 0)),
		INFERNAL_MAGE(443, "Infernal mage", new Location(3438, 3561, 0)),
		JELLY(437, "Jelly", new Location(2705, 10030, 0)),
		KURASK(410, "Kurask", new Location(2702, 9992, 0)),
		PYREFIEND(433, "Pyrefiend", new Location(2758, 10011, 0)),
		NECHRYAEL(11, "Nechryael", new Location(2427, 9752, 0)),
		TUROTH(432, "Turoth", new Location(2717, 10012, 0)),
		DUST_DEVIL(423, "Dust devil", new Location(3425, 3551, 1)),
		LESSER_DEMON(2005, "Lesser demon", new Location(2932, 9809, 0)),
		BLACK_DRAGON(252, "Black dragon", new Location(2331, 9747, 0)),
		
		/**
		 * Mazchna
		 */
		ROCK_SLUG(421, "Rock slug", new Location(3207, 9589, 0)),
		
		/**
		 * Nieve
		 */
		DARK_BEAST(4005, "Dark beast", new Location(3421, 3566, 2)),
		CAVE_HORROR(3209, "Cave horror", new Location(2802, 10015, 0)),
		GREATER_DEMON(2026, "Greater demon", new Location(2358, 9735, 0)),
		
		/**
		 * Duradel
		 */
		KING_BLACK_DRAGON(239, "King black dragon", new Location(2997, 3849, 0)),
		CHAOS_ELEMENTAL(2054, "Chaos elemental", new Location(3284, 3913, 0)),
		DAGANNOTH_SUPREME(2265, "Dagannoth supreme", new Location(1907, 4365, 0)),
		DAGGANOTH_REX(2267, "Dagannoth rex", new Location(1907, 4365, 0)),
		DAGGANOTH_PRIME(2266, "Dagannoth prime", new Location(1907, 4365, 0)),
		VENENATIS(6610, "Venenatis", new Location(3334, 3741, 0)),
		VETION(6611, "Vet'ion", new Location(3210, 3780, 0)),
		SCORPIA(6615, "Scorpia", new Location(3233, 3943, 0)),
		CALLISTO(6609, "Callisto", new Location(2271, 4681, 0)),
		KRIL_TSUTSAROTH(3129, "K'ril tsutsaroth", new Location(2924, 5340, 2)),
		KREE_ARRA(3162, "Kree'arra", new Location(2840, 5289, 2)),
		COMMANDER_ZILYANA(2205, "Commander zilyana", new Location(2912, 5266, 0)),
		GENERAL_GRAARDOR(2215, "General graardor", new Location(2857, 5354, 2)),
		KRAKEN(494, "Kraken", new Location(2481, 9799, 0)),
		BARRELCHEST(6342, "Barrelchest", new Location(3277, 3882, 0)),
		ZOMBIES_CHAMPION(3359, "Zombies champion", new Location(3020, 3631, 0));
		
		/**
		 * Represents the slayer task.
		 */
		private final int task;
		
		/**
		 * The name of our task.
		 */
		private final String name;
		
		/**
		 * The location of our task.
		 */
		private final Location location;
		
		/**
		 * Constructor of our enum.
		 * @param taskId
		 * @param taskName
		 * @param taskLocation
		 */
		private Teleports(int taskId, String taskName, Location taskLocation) {
			this.task = taskId;
			this.name = taskName;
			this.location = taskLocation;
		}
		
		public int getTask() {
			return task;
		}
		
		public String getName() {
			return name;
		}
		
		public Location getLocation() {
			return location;
		}
		
		/**
		 * Teleports us to our slayer task, when we have unlocked the ability.
		 * @param player
		 *         The player teleporting to the task
		 */
		public static void teleport(Player player) {
			if(!player.canTeleportToSlayerTask()) {
				player.getActionSender().sendMessage("You have yet to learn this ability.");
				return;
			}
			
			if(player != null) {
				for(Teleports teleTo : Teleports.values()) {
					if(player.getSlayerTask() == teleTo.getTask()) {
						TeleportExecutor.teleport(player, teleTo.getLocation());
						player.getActionSender().sendMessage("You've teleported to the "+teleTo.getName()+", you have yet to kill "+player.getSlayerTaskAmount()+ " more.");
					} else {
						player.getActionSender().sendMessage("You have no slayer task to teleport to.");
					}
					break;
				}
			}
		}
	}
	
}