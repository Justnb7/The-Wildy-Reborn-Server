package com.venenatis.game.content.skills.slayer;

import com.venenatis.game.content.skills.slayer.tasks.Task;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;

/**
 * The class represents functionality for the slayer skill.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class Slayer {

	private static Task task;

	public static Task getTask() {
		return task;
	}

	/**
	 * Checks which master is suitable for the player.
	 * 
	 * @param player
	 * @return master
	 */
	public static SlayerMasters suitableMaster(Player player) {
		if (player.combatLevel >= 3 && player.combatLevel <= 19) {
			return SlayerMasters.TURAEL;
		} else if (player.combatLevel >= 85) {
			return SlayerMasters.NIEVE;
		}
		return SlayerMasters.TURAEL;
	}

	/**
	 * Checks if the player already has a task set.
	 * 
	 * @param player
	 * @return taskAmount && slayerTask
	 */
	public static boolean hasTask(Player player) {
		return player.getSlayerTaskAmount() > 0 || player.getSlayerTask() > 0;
	}

	public static boolean isSlayerTask(Player player, int npcId) {
		if (player.getSlayerTask() == npcId) {
			player.debug("task");
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param Player
	 * @param npcId
	 * @return If the player's slayer level is above the level required return
	 *         true else false.
	 */
	public static boolean canAttack(Player player, NPC npc) {
		if (isSlayerTask(player, npc.getId())) {
			if (player.getSkills().getLevel(Skills.SLAYER) < task.getSlayerReq()) {
				return false;
			}
			if(npc.getId() == 5534 && player.getSlayerTask() != 494) {
				player.getActionSender().sendMessage("You must have Kraken's as a slayer-task to disturb these whirlpools.");
				return false;
			}
			if (npc.getId() == 493 && player.getSlayerTask() != 492 || npc.getId() == 496 && player.getSlayerTask() != 494) {
				player.getActionSender().sendMessage("You must have cave krakens as a slayer-task to attack");
				return false;
			}
		}
		return true;
	}

}