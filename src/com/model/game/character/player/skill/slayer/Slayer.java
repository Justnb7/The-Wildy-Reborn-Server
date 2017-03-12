package com.model.game.character.player.skill.slayer;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.slayer.tasks.Task;

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
	public static boolean canAttack(Player player, Npc npc) {
		if (isSlayerTask(player, npc.npcId)) {
			if (task.getSlayerReq() > player.getSkills().getLevel(Skills.SLAYER)) {
				return true;
			}
			if(npc.npcId == 5534 && player.getSlayerTask() != 494) {
				player.write(new SendMessagePacket("You must have Kraken's as a slayer-task to disturb these whirlpools."));
				return false;
			}
			if (npc.npcId == 493 && player.getSlayerTask() != 492 || npc.npcId == 496 && player.getSlayerTask() != 494) {
				player.write(new SendMessagePacket("You must have cave krakens as a slayer-task to attack"));
				return false;
			}
		}
		return false;
	}

}