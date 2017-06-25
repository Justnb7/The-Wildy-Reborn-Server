package com.venenatis.game.model.entity.player.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.entity.player.dialogue.impl.Imbue;
import com.venenatis.game.model.entity.player.dialogue.impl.RottenPotato;
import com.venenatis.game.model.entity.player.dialogue.impl.SigmundTheMerchant;
import com.venenatis.game.model.entity.player.dialogue.impl.chat.Mandrith;
import com.venenatis.game.model.entity.player.dialogue.impl.chat.RunescapeGuide;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.BarrowsTunnel;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.DiedInProcess;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.EnterCave;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.ExitCave;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.Tzhaar_Mej_Jal;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.WonFightCave;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild.DefenderInPosession;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild.NoDefenderInPossesion;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild.NoTokens;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.warriors_guild.NotEnoughTokens;
import com.venenatis.game.model.entity.player.dialogue.impl.pets.Olmlet;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.ChaeldarDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.DuradelDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.EnchantedGem;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.EnchantedGemTeleport;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.MazchnaDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.NieveDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.TuraelDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.VannakaDialogue;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.abilities.TeleportToTask;
import com.venenatis.game.model.entity.player.dialogue.impl.teleport.AgilityTeleports;
import com.venenatis.game.model.entity.player.dialogue.impl.teleport.MiningTeleports;
import com.venenatis.game.model.entity.player.dialogue.impl.teleport.TeleportCard;

/**
 * A repository to contain all of the dialogues
 *
 * @author Erik Eide
 */
public class DialogueRepository {

	/**
	 * A {@link HashMap} to store all of the dialogue classes in
	 */
	private static final Map<String, Class<? extends Dialogue>> dialogues = new HashMap<>();

	static {
		
		/**
		 * Fight caves
		 */
		dialogues.put("ENTER_FIGHT_CAVE", EnterCave.class);
		dialogues.put("LEAVE_FIGHT_CAVE", ExitCave.class);
		dialogues.put("DIED_DURING_FIGHT_CAVE", DiedInProcess.class);
		dialogues.put("WON_FIGHT_CAVE", WonFightCave.class);
		dialogues.put("FIGHT_CAVE", Tzhaar_Mej_Jal.class);
		
		/**
		 * Warriors guild
		 */
		dialogues.put("OUT_OF_TOKENS", NotEnoughTokens.class);
		dialogues.put("PLAYER_HAS_NO_TOKENS", NoTokens.class);
		dialogues.put("HAS_DEFENDER", DefenderInPosession.class);
		dialogues.put("NO_DEFENDER", NoDefenderInPossesion.class);
		
		/**
		 * Rotten potato
		 */
		dialogues.put("POTATO", RottenPotato.class);
		
		/**
		 * Slayer abilities
		 */
		dialogues.put("TELEPORT_TO_TASK", TeleportToTask.class);
		
		/**
		 * Pets
		 */
		dialogues.put("OLMLET", Olmlet.class);
		
		
		/**
		 * Shop dialogues
		 */
		dialogues.put("SIGMUND_THE_MERCHANT", SigmundTheMerchant.class);
		dialogues.put("MANDRITH", Mandrith.class);
		
		/**
		 * Imbue
		 */
		dialogues.put("IMBUE", Imbue.class);
		
		/**
		 * Barrows
		 */
		dialogues.put("BARROWS_TUNNEL", BarrowsTunnel.class);
		
		/**
		 * Teleports
		 */
		dialogues.put("AGILITY_TELEPORTS", AgilityTeleports.class);
		dialogues.put("MINING_TELEPORTS", MiningTeleports.class);
		dialogues.put("TELEPORT_CARD", TeleportCard.class);
		
		/**
		 * Starter dialogue
		 */
		dialogues.put("STARTER", RunescapeGuide.class);
		
		/**
		 * Slayer masters
		 */
		dialogues.put("TURAEL_DIALOGUE", TuraelDialogue.class);
		dialogues.put("MAZCHNA_DIALOGUE", MazchnaDialogue.class);
		dialogues.put("VANNAKA_DIALOGUE", VannakaDialogue.class);
		dialogues.put("CHAELDAR_DIALOGUE", ChaeldarDialogue.class);
		dialogues.put("NIEVE_DIALOGUE", NieveDialogue.class);
		dialogues.put("DURADEL_DIALOGUE", DuradelDialogue.class);
		
		/**
		 * Enchanted gem
		 */
		dialogues.put("ENCHANTED_GEM_TELEPORT", EnchantedGemTeleport.class);
		dialogues.put("ENCHANTED_GEM", EnchantedGem.class);
		
		/**
		 * Bounty hunter
		 */

	}

	/**
	 * Gets a dialogue based on the provided {@link String} key
	 * 
	 * @param name
	 *            The key to the dialogue
	 * @return The dialogue based on the provided {@link String} key
	 */
	protected static Dialogue getDialogue(final String name) {
		Class<? extends Dialogue> dialogue = dialogues.get(name);

		if (dialogue != null) {
			try {
				return dialogue.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

}