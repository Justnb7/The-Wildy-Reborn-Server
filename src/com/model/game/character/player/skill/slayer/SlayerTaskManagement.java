package com.model.game.character.player.skill.slayer;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.skill.slayer.tasks.Nieve;
import com.model.game.character.player.skill.slayer.tasks.Task;
import com.model.game.character.player.skill.slayer.tasks.Turael;
import com.model.game.character.player.skill.slayer.tasks.Vannaka;
import com.model.game.location.Position;
import com.model.game.character.player.skill.slayer.tasks.Chaeldar;
import com.model.game.character.player.skill.slayer.tasks.Duradel;
import com.model.game.character.player.skill.slayer.tasks.Mazchna;
import com.model.utility.Utility;

/**
 * The class represents functionality for the slayer task.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class SlayerTaskManagement {
	
	public enum Teleports {
		
		/**
		 * Turael
		 */
		BANSHEE(414, "Banshee", new Position(3441, 3565, 0)),
		BAT(2834, "Bat", new Position(2912, 9837, 0)),
		BIRDS(2692, "Chicken", new Position(3232, 3299, 0)),
		BEAR(3423, "Bear", new Position(2712, 3335, 0)),
		CAVE_BUGS(481, "Cave bug", new Position(3160, 9573, 0)),
		CAVE_CRAWLERS(406, "Cave crawler", new Position(3193, 9577, 0)),
		CAVE_SLIMES(480, "Cave slime", new Position(3158, 9592, 0)),
		COWS(2805, "Cow", new Position(3260, 3278, 0)),
		CRAWLING_HANDS(448, "Crawling hand", new Position(3411, 3537, 0)),
		DWARVES(291, "Dwarve", new Position(2918, 9758, 0)),
		GHOSTS(85, "Ghost", new Position(3277, 3658, 0)),
		GOBLINS(2245, "Goblin", new Position(2572, 3388, 0)),
		KALPHITES(955, "Kalphite", new Position(3760, 5854, 0)),
	
		/**
		 * Chaeldar
		 */
		ABYSSAL_DEMON(415, "Abyssal demon", new Position()),
		ABERRANT_SPECTRE(3, "Aberrant spectre", new Position(2393, 9780, 0)),
		BLACK_DEMON(1432, "Black demon", new Position(2701, 9490, 0)),
		HELLHOUND(135, "Hellhound", new Position(2407, 9785, 0)),
		CAVE_KRAKEN(492, "Cave kraken", new Position(2481, 9799, 0)),
		STEEL_DRAGON(274, "Steel dragon", new Position(2312, 9775, 0)),
		IRON_DRAGON(272, "Iron dragon", new Position(2326, 9736, 0)),
		ANKOU(2514, "Ankou", new Position(2376, 9758, 0)),
		BASILISK(417, "Basilisk", new Position(2742, 10002, 0)),
		BLUE_DRAGON(268, "Blue dragon", new Position(2894, 9794, 0)),
		BLOODVELD(484, "Bloodveld", new Position(2436, 9794, 0)),
		BRONZE_DRAGON(270, "Bronze dragon", new Position(2349, 9742, 0)),
		COCKATRICE(419, "Cockatrice", new Position(2799, 10030, 0)),
		FIRE_GIANT(2075, "Fire giant", new Position(2352, 9754, 0)),
		GARGOYLE(412, "Gargoyle", new Position(3434, 3539, 2)),
		GREEN_DRAGON(264, "Green dragon", new Position(2973, 3620, 0)),
		HILL_GIANT(2098, "Hill giant", new Position(3298, 3650, 0)),
		ICE_WARRIOR(2841, "Ice warrior", new Position(3036, 9541, 0)),
		INFERNAL_MAGE(443, "Infernal mage", new Position(3438, 3561, 0)),
		JELLY(437, "Jelly", new Position(2705, 10030, 0)),
		KURASK(410, "Kurask", new Position(2702, 9992, 0)),
		PYREFIEND(433, "Pyrefiend", new Position(2758, 10011, 0)),
		NECHRYAEL(11, "Nechryael", new Position(2427, 9752, 0)),
		TUROTH(432, "Turoth", new Position(2717, 10012, 0)),
		DUST_DEVIL(423, "Dust devil", new Position(3425, 3551, 1)),
		LESSER_DEMON(2005, "Lesser demon", new Position(2932, 9809, 0)),
		BLACK_DRAGON(252, "Black dragon", new Position(2331, 9747, 0)),
		
		/**
		 * Mazchna
		 */
		ROCK_SLUG(421, "Rock slug", new Position(3207, 9589, 0)),
		
		/**
		 * Nieve
		 */
		DARK_BEAST(4005, "Dark beast", new Position(3421, 3566, 2)),
		CAVE_HORROR(3209, "Cave horror", new Position(2802, 10015, 0)),
		GREATER_DEMON(2026, "Greater demon", new Position(2358, 9735, 0)),
		
		/**
		 * Duradel
		 */
		KING_BLACK_DRAGON(239, "King black dragon", new Position(2997, 3849, 0)),
		CHAOS_ELEMENTAL(2054, "Chaos elemental", new Position(3284, 3913, 0)),
		DAGANNOTH_SUPREME(2265, "Dagannoth supreme", new Position(1907, 4365, 0)),
		DAGGANOTH_REX(2267, "Dagannoth rex", new Position(1907, 4365, 0)),
		DAGGANOTH_PRIME(2266, "Dagannoth prime", new Position(1907, 4365, 0)),
		VENENATIS(6610, "Venenatis", new Position(3334, 3741, 0)),
		VETION(6611, "Vet'ion", new Position(3210, 3780, 0)),
		SCORPIA(6615, "Scorpia", new Position(3233, 3943, 0)),
		CALLISTO(6609, "Callisto", new Position(2271, 4681, 0)),
		KRIL_TSUTSAROTH(3129, "K'ril tsutsaroth", new Position(2924, 5340, 2)),
		KREE_ARRA(3162, "Kree'arra", new Position(2840, 5289, 2)),
		COMMANDER_ZILYANA(2205, "Commander zilyana", new Position(2912, 5266, 0)),
		GENERAL_GRAARDOR(2215, "General graardor", new Position(2857, 5354, 2)),
		KRAKEN(494, "Kraken", new Position(2481, 9799, 0)),
		BARRELCHEST(6342, "Barrelchest", new Position(3277, 3882, 0)),
		ZOMBIES_CHAMPION(3359, "Zombies champion", new Position(3020, 3631, 0));
		
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
		private final Position location;
		
		/**
		 * Constructor of our enum.
		 * @param taskId
		 * @param taskName
		 * @param taskLocation
		 */
		private Teleports(int taskId, String taskName, Position taskLocation) {
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
		
		public Position getLocation() {
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
			player.getActionSender().sendRemoveInterfacePacket();
			player.getActionSender().sendMessage("Your slayer task has been reset, talk to any slayer master for a new one.");
			return true;
		} else {
			player.getActionSender().sendMessage("You do not have enough blood money in order to reset your slayer task.");
			player.getActionSender().sendRemoveInterfacePacket();
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
	public static void decreaseTask(Player player, NPC npc) {
		
		//Safety check
		if (player != null) {
			player = World.getWorld().getPlayers().get(npc.killedBy);
			
			//Decrease task
			if (player.getSlayerTask() == npc.npcId) {
				player.setSlayerTaskAmount(player.getSlayerTaskAmount() - 1);
				player.getSkills().addExperience(Skills.SLAYER, npc.maximumHealth);
				//player.getActionSender().sendMessage("Slayertask: "+Npc.getName(npc.npcId)+ " left: "+player.getSlayerTaskAmount()));
				player.getActionSender().sendString("<img=28><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+NPC.getName(player.getSlayerTask()), 29511);
			}
			
			//Kills left messages
			if(player.getSlayerTaskAmount() == 25) {
				player.getActionSender().sendMessage("You still have to kill 25 more "+NPC.getName(npc.npcId));
			} else if(player.getSlayerTaskAmount() == 10) {
				player.getActionSender().sendMessage("You still have to kill 10 more "+NPC.getName(npc.npcId));
			}
			
			// The player has completed their task, we can go ahead and reward them.
			if (player.getSlayerTaskAmount() <= 0) {
				
				player.setSlayerTask(0);
				player.setSlayerTaskAmount(0);
				player.setSlayerTaskDifficulty(0);
				player.setSlayerTasksCompleted(player.getSlayerTasksCompleted() + 1);
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
						player.getActionSender().sendMessage("meaining you get @blu@4@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 4);
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@2@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 2);
					}

					/**
					 * Medium task (Vannaka).
					 */
				} else if (player.getSlayerTaskDifficulty() == 2) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@8@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 8);
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@4@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 4);
					}
					/**
					 * Hard task (Chaeldar).
					 */
				} else if (player.getSlayerTaskDifficulty() == 3) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@12@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 12);
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@6@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 6);
					}

					/**
					 * Elite task (Nieve).
					 */
				} else if (player.getSlayerTaskDifficulty() == 4) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@16@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 16);
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@8@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 8);
					}

					/**
					 * Boss task (Duradel).
					 */
				} else if (player.getSlayerTaskDifficulty() == 5) {
					if (Constants.SLAYER_REWARDS) {
						player.getActionSender().sendMessage("You have completed your slayer assignment. Double Slayer points is active");
						player.getActionSender().sendMessage("meaining you get @blu@20@bla@ Slayer Points. Please speak to a Slayer Master");
						player.getActionSender().sendMessage("to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 20);
					} else {
						player.getActionSender().sendMessage("You have completed your slayer assignment. You gain @blu@10@bla@ Slayer Points!");
						player.getActionSender().sendMessage("Please speak to a Slayer Master to retrieve an another assignment.");
						player.setSlayerPoints(player.getSlayerPoints() + 10);
					}
				}
			}
		}
	}
}