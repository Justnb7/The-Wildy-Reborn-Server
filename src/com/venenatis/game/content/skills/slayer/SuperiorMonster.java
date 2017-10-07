package com.venenatis.game.content.skills.slayer;

import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

/**
 * 
 * @author Patrick van Elderen
 *
 */
public class SuperiorMonster {
	
	private enum SuperiorSlayerMonster {
		
		CRUSHING_HAND("Crawling hand", 7388, 550),
		CHASM_CRAWLER("Cave crawler", 7389, 600),
		SCREAMING_BANSHEE("Banshee", 7390, 610),
		GIANT_ROCKSLUG("Rock slug", 7392, 770),
		COCKATHRICE("Cockatrice", 7393, 950),
		FLAMING_PYRELORD("Pyrefiend", 7394, 1250),
		MONSTROUS_BASILISK("Basilisk", 7395, 1700),
		MALEVOLENT_MAGE("Infernal mage", 7396, 1750),
		INSATIABLE_BLOODVELD("Bloodveld", 7397, 2900),
		INSATIABLE_MUTATED_BLOODVELD("Mutated bloodveld", 7398, 4100),
		VITREOUS_JELLY("Jelly", 7399, 2200),
		CAVE_ABGOMINATION("Cave horror", 7401, 1300),
		ABHORRENT_SPECTRE("Aberrant spectre", 7402, 2500),
		CHOKE_DEVIL("Dust devil", 7404, 3000),
		KING_KURASK("Kurask", 7405, 2726),
		MARBLE_GARGOYLE("Gargoyle", 7407, 3044),
		NECHRYARCH("Nechryael", 7411, 3068),
		GREATER_ABYSSAL_DEMON("Abyssal demon", 7410, 4200),
		NIGHT_BEAST("Dark beast", 7409, 6462),
		NUCLEAR_SMOKE_DEVIL("Smoke devil", 7406, 2400);
		
		/**
		 * Our current task
		 */
		private final String slayerTask;
		
		/**
		 * The superior npc
		 */
		private int superiorId;
		
		/**
		 * The experience reward for killing an superior
		 */
		private final int expReward;
		
		SuperiorSlayerMonster(String task, int spawn, int reward) {
			this.slayerTask = task;
			this.superiorId = spawn;
			this.expReward = reward;
		}
		
	}
	
	public static void spawnSuperior(final Player player, NPC npc) {
		if(!player.getSlayerInterface().getUnlocks().containsKey(91126)) {
			return;
		}
		for(SuperiorSlayerMonster ssm : SuperiorSlayerMonster.values()) {
			if(player.getSlayerTask().contains(ssm.slayerTask)) {
				int random_spawn = Utility.random(50);
				if(player.getUsername().equalsIgnoreCase("patrick"))
					random_spawn = 25;
				if(random_spawn == 25) {
					player.getActionSender().sendMessage("@red@A superior foe has appeared...");
					npc.spawn(player, ssm.superiorId, npc.getLocation(), 1, true);
				}
			}
		}
	}

}
