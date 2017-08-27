package com.venenatis.game.model.entity.player.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.entity.player.dialogue.impl.*;
import com.venenatis.game.model.entity.player.dialogue.impl.chat.*;
import com.venenatis.game.model.entity.player.dialogue.impl.item_on_item.*;
import com.venenatis.game.model.entity.player.dialogue.impl.minigames.fight_caves.*;
import com.venenatis.game.model.entity.player.dialogue.impl.pets.*;
import com.venenatis.game.model.entity.player.dialogue.impl.slayer.*;
import com.venenatis.game.model.entity.player.dialogue.impl.teleport.*;


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
		 * Granite maul
		 */
		dialogues.put("GRANITE_MAUL_UPGRADE", GraniteMaulUpgrade.class);
		
		/**
		 * Fight caves
		 */
		dialogues.put("ENTER_FIGHT_CAVE", EnterCave.class);
		dialogues.put("LEAVE_FIGHT_CAVE", ExitCave.class);
		dialogues.put("DIED_DURING_FIGHT_CAVE", DiedInProcess.class);
		dialogues.put("WON_FIGHT_CAVE", WonFightCave.class);
		dialogues.put("FIGHT_CAVE", Tzhaar_Mej_Jal.class);
		
		/**
		 * Rotten potato
		 */
		dialogues.put("POTATO", RottenPotato.class);
		
		/**
		 * Presets
		 */
		dialogues.put("PRESETS", Presets.class);
		
		/**
		 * Pets
		 */
		dialogues.put("OLMLET", Olmlet.class);
		
		/**
		 * Teleports
		 */
		dialogues.put("AGILITY_TELEPORTS", AgilityTeleports.class);
		dialogues.put("MINING_TELEPORTS", MiningTeleports.class);
		dialogues.put("SLAYER_TELEPORTS", SlayerTeleports.class);
		dialogues.put("TRAINING_TELEPORTS", TrainingTeleports.class);
		dialogues.put("GODWARS_TELEPORTS", GodwarsTeleports.class);
		
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
		dialogues.put("EMBLEM_TRADER", EmblemTraderDialogue.class);
		
		/**
		 * Ironman
		 */
		dialogues.put("IRONMAN_PAUL", IronManPaul.class);
		dialogues.put("CLEAR_ACCOUNT", ClearAccount.class);
		
		/**
		 * Perdu
		 */
		dialogues.put("PERDU", Perdu.class);
		
		/**
		 * Options
		 */
		dialogues.put("MAGIC_BOOK", MagicBook.class);
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