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
		 * Ring of Wealth
		 */
		dialogues.put("RING_OF_WEALTH", RingofWealth.class);
		
		/**
		 * Magic Shortbow
		 */
		dialogues.put("MAGIC_SHORTBOW", MagicShortbow.class);
		
		/**
		 * White dark bow
		 */
		dialogues.put("WHITE_DARK_BOW", WhiteDarkBow.class);
		
		/**
		 * Yellow dark bow
		 */
		dialogues.put("YELLOW_DARK_BOW", YellowDarkBow.class);
		
		/**
		 * Green dark bow
		 */
		dialogues.put("GREEN_DARK_BOW", GreenDarkBow.class);
		
		/**
		 * Blue dark bow
		 */
		dialogues.put("BLUE_DARK_BOW", BlueDarkBow.class);
		
		/**
		 * Frozen whip
		 */
		dialogues.put("FROZEN_WHIP", FrozenWhip.class);
		
		/**
		 * Lava whip
		 */
		dialogues.put("LAVA_WHIP", LavaWhip.class);
		
		/**
		 * Abyssal tentacle
		 */
		dialogues.put("ABYSSAL_TENTACLE", AbyssalTentacle.class);
		
		/**
		 * Lava whip
		 */
		dialogues.put("BLESSED_SARADOMIN_SWORD", BlessedSaradominSword.class);
		
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
		dialogues.put("OLMLET_DIALOGUE", Olmlet.class);
		dialogues.put("ABYSSAL_ORPHAN_DIALOGUE", AbyssalOrphan.class);
		dialogues.put("BABY_MOLE_DIALOGUE", BabyMole.class);
		dialogues.put("CALLISTO_CUB_DIALOGUE", CallistoCub.class);
		dialogues.put("HELLPUPPY_DIALOGUE", HellPuppy.class);
		dialogues.put("CHAOS_ELEMENTAL_DIALOGUE", ChaosElementalJr.class);
		dialogues.put("DAGANNOTH_PRIME_DIALOGUE", DagannothPrime.class);
		dialogues.put("DAGANNOTH_REX_DIALOGUE", DagannothRex.class);
		dialogues.put("DAGANNOTH_SUPREME_DIALOGUE", DagannothSupreme.class);
		dialogues.put("DARK_CORE_DIALOGUE", PetDarkCore.class);
		dialogues.put("GENERAL_GRAARDOR_DIALOGUE", GeneralGraardorJr.class);
		dialogues.put("KRIL_TSUTSAROTH_DIALOGUE", KrilTsutsarothJr.class);
		dialogues.put("KREE_ARRA_DIALOGUE", KreeArraJr.class);
		dialogues.put("PENANCE_PET_DIALOGUE", PetPenanceQueen.class);
		dialogues.put("SMOKE_DEVIL_DIALOGUE", PetSmokeDevil.class);
		dialogues.put("ZILYANA_DIALOGUE", ZilyanaJr.class);
		dialogues.put("SNAKELING_DIALOGUE", Snakeling.class);
		dialogues.put("PRINCE_BLACK_DRAGON_DIALOGUE", PrinceBlackDragon.class);
		dialogues.put("SCORPIAS_OFFSPRING_DIALOGUE", ScorpiasOffspring.class);
		dialogues.put("TZREK_JAD_DIALOGUE", TzrekJad.class);
		dialogues.put("VENENATIS_SPIDERLING_DIALOGUE", VenenatisSpiderling.class);
		dialogues.put("VETION_DIALOGUE", VetionJr.class);
		dialogues.put("KALPHITE_PRINCESS_DIALOGUE", KalphitePrincess.class);
		dialogues.put("HERON_DIALOGUE", Heron.class);
		dialogues.put("ROCK_GOLEM_DIALOGUE", RockGolem.class);
		dialogues.put("BEAVER_DIALOGUE", Beaver.class);
		dialogues.put("GIANT_SQUIRREL_DIALOGUE", GiantSquirrel.class);
		dialogues.put("ROCKY_DIALOGUE", Rocky.class);
		dialogues.put("TANGLEROOT_DIALOGUE", Tangleroot.class);
		dialogues.put("RIFT_GUARDIAN_DIALOGUE", RiftGuardian.class);
		dialogues.put("BABY_CHINCHOMPA_DIALOGUE", BabyChinchompa.class);
		dialogues.put("CHOMPY_CHICK_DIALOGUE", ChompyChick.class);
		dialogues.put("BLOODHOUND_DIALOGUE", BloodHound.class);
		dialogues.put("PHOENIX_DIALOGUE", Phoenix.class);
		dialogues.put("SKOTOS_DIALOGUE", Skotos.class);
		dialogues.put("HERBI_DIALOGUE", Herbi.class);
		dialogues.put("JAL_NIB_REK_DIALOGUE", JalNibRek.class);
		dialogues.put("NOON_AND_MIDNIGHT_DIALOGUE", NoonAndMidnight.class);
		
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
		 * Barrows tunnel
		 */
		dialogues.put("BARROWS_TUNNEL", EmblemTrader.class);
		
		/**
		 * Mac
		 */
		dialogues.put("MAC", Mac.class);
		
		/**
		 * Bounty hunter
		 */
		dialogues.put("EMBLEM_TRADER", EmblemTrader.class);
		
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
		 * Ghommal
		 */
		dialogues.put("GHOMMAL", Ghommal.class);
		
		/**
		 * Probita
		 */
		dialogues.put("PET_INSURANCE", PetInsurance.class);
		
		/**
		 * Options
		 */
		dialogues.put("MAGIC_BOOK", MagicBook.class);
		dialogues.put("YES_OR_NO", YesOrNo.class);
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