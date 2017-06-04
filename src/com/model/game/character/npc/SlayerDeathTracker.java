package com.model.game.character.npc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import com.model.game.character.player.Player;
import com.model.game.definitions.NPCDefinitions;
import com.model.utility.Utility;

/**
 * 
 * @author Jason MacKeigan
 * @date Jul 25, 2014, 10:43:59 PM
 */
public class SlayerDeathTracker {

	private final Player player;

	private HashMap<SlayerNpcName, Integer> tracker = new HashMap<SlayerNpcName, Integer>();

	public HashMap<SlayerNpcName, Integer> getTracker() {
		return tracker;
	}

	public SlayerDeathTracker(Player player) {
		this.player = player;
	}

	public void add(NPC npc) {
		if (npc == null) {
			return;
		}

        NPCDefinitions definition = NPCDefinitions.get(npc.getId());
		if (definition == null) {
			return;
		}

		final String name = definition.getName().toLowerCase().replaceAll("_", " ").trim();

		Optional<SlayerNpcName> boss = SlayerNpcName.NAMES.stream().filter(n -> n.toString().equals(name)).findFirst();

		boss.ifPresent(b -> {
			int kills = 1;

			if (tracker.containsKey(b)) {
				kills = tracker.get(b) + 1;
			}

			tracker.put(b, kills);
			player.getActionSender().sendMessage("<col=255>You have killed</col> <col=FF0000>" + kills + "</col> <col=255>"
					+ Utility.capitalize(name) + ".</col>");
		} );
	}

	public enum SlayerNpcName {
		BATTLE_MAGE,
		YAK,
		SKELETON,
		PYREFIEND,
		BAT,
		GIANT_BAT,
		CAVE_CRAWLER,
		CRAWLING_HAND,
		DAGGANOTH,
		EARTH_WARRIOR,
		GHOST,
		ROCKSLUG,
		RED_DRAGON,
		COCKATRICE,
		BLACK_DEMON,
		DUST_DEVIL,
		ELF_WARRIOR,
		BANSHEE,
		BASILISK,
		BLOODVELD,
		BLUE_DRAGON,
		FIRE_GIANT,
		GREATER_DEMON,
		ICE_GIANT,
		GREEN_DRAGON,
		INFERNAL_MAGE,
		ICE_WARRIOR,
		JELLY,
		LESSER_DEMON,
		HILL_GIANT,
		MOSS_GIANT,
		STEEL_DRAGON,
		ABYSSAL_DEMON,
		BRONZE_DRAGON,
		BLACK_DRAGON,
		DARK_BEAST,
		GARGOYLE,
		HELLHOUND,
		IRON_DRAGON,
		KURASK,
		NECHRYAELS,
		CAVE_KRAKEN,
		SMOKE_DEVIL,
		ABERRANT_SPECTRE;

		@Override
		public String toString() {
			String name = name().toLowerCase().replaceAll("_", " ").trim();
			return name;
		}

		private static final Set<SlayerNpcName> NAMES = Collections.unmodifiableSet(EnumSet.allOf(SlayerNpcName.class));

		public static SlayerNpcName get(String name) {
			for (SlayerNpcName boss : NAMES) {
				if (boss.toString().equalsIgnoreCase(name))
					return boss;
			}
			return null;
		}

		// proper formatting of the name for display on interfaces
		public String format() {
			return Utility.capitalize(name().toLowerCase().replaceAll("_", " ").trim());
		}
	}

}