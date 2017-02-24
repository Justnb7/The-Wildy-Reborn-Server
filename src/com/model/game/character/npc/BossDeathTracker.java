package com.model.game.character.npc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;
import com.model.utility.json.definitions.NpcDefinition;

/**
 * 
 * @author Jason MacKeigan
 * @date Jul 25, 2014, 10:43:59 PM
 */
public class BossDeathTracker {

	private final Player player;
	
	private HashMap<BossName, Integer> tracker = new HashMap<BossName, Integer>();

	public HashMap<BossName, Integer> getTracker() {
		return tracker;
	}

	public BossDeathTracker(Player player) {
		this.player = player;
	}

	public void add(Npc npc) {
		if (npc == null) {
			return;
		}
		
        NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcId];
		
		if (definition == null) {
			return;
		}
		
		final String name = definition.getName().toLowerCase().replaceAll("_", " ").trim();
		
		Optional<BossName> boss = BossName.NAMES.stream().filter(n -> n.toString().equals(name)).findFirst();
		
		boss.ifPresent(b -> {
			int kills = 1;
			
			if (tracker.containsKey(b)) {
				kills = tracker.get(b) + 1;
			}
			
			tracker.put(b, kills);
			player.write(new SendMessagePacket("<col=255>You have killed</col> <col=FF0000>" + kills + "</col> <col=255>" + Utility.capitalize(name) + ".</col>"));
		});
	}
	
	/**
	 * Determines the total amount of kills
	 * @return	the total kill count
	 */
	public long getTotal() {
		return tracker.values().stream().mapToLong(Integer::intValue).sum();
	}

	public enum BossName {
		KING_BLACK_DRAGON, GENERAL_GRAARDOR, COMMANDER_ZILYANA, KREE_ARRA, KRIL_TSUTSAROTH, DAGANNOTH_REX, DAGANNOTH_SUPREME, DAGANNOTH_PRIME, BARRELCHEST, CHAOS_ELEMENTAL, KALPHITE_QUEEN, KRAKEN, GIANT_MOLE, SCORPIA, VENENATIS, CALLISTO, VETION,
		ZULRAH, CORPOREAL_BEAST, ZOMBIES_CHAMPION;

		@Override
		public String toString() {
			String name = name().toLowerCase().replaceAll("_", " ").trim();
			return name;
		}

		private static final Set<BossName> NAMES = Collections.unmodifiableSet(EnumSet.allOf(BossName.class));

		public static BossName get(String name) {
			for (BossName boss : NAMES) {
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