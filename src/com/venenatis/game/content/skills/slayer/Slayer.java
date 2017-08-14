package com.venenatis.game.content.skills.slayer;

import com.venenatis.game.content.skills.slayer.tasks.Task;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
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
		if (player.getCombatLevel() >= 3 && player.getCombatLevel() <= 19) {
			return SlayerMasters.TURAEL;
		} else if (player.getCombatLevel() >= 85) {
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

	/**
	 * 
	 * @param Player
	 * @param npcId
	 * @return If the player's slayer level is above the level required return
	 *         true else false.
	 */
	public static boolean canAttack(Player player, NPC npc) {
		int slayer_requirement = 0;
		
		if(npc.getName().equalsIgnoreCase("Crawling hand"))
			slayer_requirement = 5;
		
		if(npc.getName().equalsIgnoreCase("Cave bug"))
			slayer_requirement = 7;
		
		if(npc.getName().equalsIgnoreCase("Cave crawler"))
			slayer_requirement = 10;
		
		if(npc.getName().equalsIgnoreCase("Banshee"))
			slayer_requirement = 15;
		
		if(npc.getName().equalsIgnoreCase("Cave slime"))
			slayer_requirement = 17;
		
		if(npc.getName().equalsIgnoreCase("Rockslug"))
			slayer_requirement = 20;
		
		if(npc.getName().equalsIgnoreCase("Desert lizard"))
			slayer_requirement = 22;
		
		if(npc.getName().equalsIgnoreCase("Cockatrice"))
			slayer_requirement = 25;
		
		if(npc.getName().equalsIgnoreCase("Pyrefiend"))
			slayer_requirement = 30;
		
		if(npc.getName().equalsIgnoreCase("Mogre"))
			slayer_requirement = 32;
		
		if(npc.getName().equalsIgnoreCase("Harpie bug swarm"))
			slayer_requirement = 33;
		
		if(npc.getName().equalsIgnoreCase("Wall beast"))
			slayer_requirement = 35;
		
		if(npc.getName().equalsIgnoreCase("Killerwatt"))
			slayer_requirement = 37;
		
		if(npc.getName().equalsIgnoreCase("Monalisk"))
			slayer_requirement = 39;
		
		if(npc.getName().equalsIgnoreCase("Basilisk") || npc.getName().equalsIgnoreCase("Terror dog"))
			slayer_requirement = 40;
		
		if(npc.getName().equalsIgnoreCase("Fever spider"))
			slayer_requirement = 42;
		
		if(npc.getName().equalsIgnoreCase("Infernal mage"))
			slayer_requirement = 45;
		
		if(npc.getName().equalsIgnoreCase("Brine rat"))
			slayer_requirement = 47;
		
		if(npc.getName().equalsIgnoreCase("Bloodveld"))
			slayer_requirement = 50;
		
		if(npc.getName().equalsIgnoreCase("Jelly"))
			slayer_requirement = 52;
		
		if(npc.getName().equalsIgnoreCase("Turoth"))
			slayer_requirement = 55;
		
		if(npc.getName().equalsIgnoreCase("Mutated zygomite"))
			slayer_requirement = 57;
		
		if(npc.getName().equalsIgnoreCase("Cave horror"))
			slayer_requirement = 58;
		
		if(npc.getName().equalsIgnoreCase("Aberrant spectre"))
			slayer_requirement = 60;
		
		if(npc.getName().equalsIgnoreCase("Spiritual ranger"))
			slayer_requirement = 63;
		
		if(npc.getName().equalsIgnoreCase("Dust devil"))
			slayer_requirement = 65;
		
		if(npc.getName().equalsIgnoreCase("Spiritual warrior"))
			slayer_requirement = 68;
		
		if(npc.getName().equalsIgnoreCase("Kurask"))
			slayer_requirement = 70;
		
		if(npc.getName().equalsIgnoreCase("Skeletal wyvern"))
			slayer_requirement = 72;
		
		if(npc.getName().equalsIgnoreCase("Gargoyle"))
			slayer_requirement = 75;
		
		if(npc.getName().equalsIgnoreCase("Brutal black dragon"))
			slayer_requirement = 77;
		
		if(npc.getName().equalsIgnoreCase("Nechryael"))
			slayer_requirement = 80;
		
		if(npc.getName().equalsIgnoreCase("Spiritual mage"))
			slayer_requirement = 83;
		
		if(npc.getName().equalsIgnoreCase("Abyssal demon"))
			slayer_requirement = 85;
		
		if(npc.getName().equalsIgnoreCase("Cave kraken") || npc.getName().equalsIgnoreCase("Kraken"))
			slayer_requirement = 87;
		
		if(npc.getName().equalsIgnoreCase("Dark beast"))
			slayer_requirement = 90;
		
		if(npc.getName().equalsIgnoreCase("Cerberus"))
			slayer_requirement = 91;
		
		if(npc.getName().equalsIgnoreCase("Smoke devil") || npc.getName().equalsIgnoreCase("Thermonuclear smoke devil"))
			slayer_requirement = 93;
		
		if (player.getSkills().getLevelForExperience(Skills.SLAYER) < slayer_requirement) {
			player.getActionSender().sendMessage("You need a Slayer level of " + slayer_requirement + " to attack this npc.");
			Combat.resetCombat(player);
			return false;
		}
		
		if (npc.getId() == 5534 && player.getSlayerTask() != 494) {
			player.getActionSender().sendMessage("You must have Kraken's as a slayer-task to disturb these whirlpools.");
			Combat.resetCombat(player);
			return false;
		}
		
		if (npc.getId() == 493 && player.getSlayerTask() != 492 || npc.getId() == 496 && player.getSlayerTask() != 494) {
			player.getActionSender().sendMessage("You must have cave krakens as a slayer-task to attack");
			Combat.resetCombat(player);
			return false;
		}
		//player.debug(String.format("slayer level %s VS requirement %s%n", player.getSkills().getLevelForExperience(Skills.SLAYER), slayer_requirement));
		return true;
	}

}