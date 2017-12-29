package com.venenatis.game.model.entity.npc.drops;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.gson.Gson;

/**
 * Represents a single NPCDropDefinition read from the npc_drops JSON file
 * 
 * @author Stan
 *
 */
public class NPCDropDefinitions {

	private static final Logger logger = Logger.getLogger(NPCDropDefinitions.class.getName());

	private static HashMap<Integer, NPCDropDefinitions> definitions = new HashMap<Integer, NPCDropDefinitions>();

	/**
	 * Fetches item definition data from {@code definitions}
	 * 
	 * @param id
	 * @return {@code definitions}
	 */
	public static NPCDropDefinitions get(int id) {
		return definitions.get(id);
	}

	/**
	 * Loads item definitions from JSON file
	 */
	public static void loadNPCDropDefs() {
		logger.info("Loading ");
		try {
			final NPCDropDefinitions[] read = new Gson().fromJson(new FileReader("./data/def/mob/npc_drops.json"),
					NPCDropDefinitions[].class);
			for (NPCDropDefinitions def : read)
				if (def != null) {
					for (NPCDrop drop : def.alwaysDrops)
						drop.setChance(1);
					for (NPCDrop drop : def.commonDrops)
						drop.setChance(DropChances.COMMON);
					for (NPCDrop drop : def.uncommonDrops)
						drop.setChance(DropChances.UNCOMMON);
					for (NPCDrop drop : def.rareDrops)
						drop.setChance(DropChances.RARE);
					for (NPCDrop drop : def.veryRareDrops)
						drop.setChance(DropChances.VERY_RARE);
					for (NPCDrop drop : def.specialDrops)
						drop.setChance(drop.getChance() / DropChances.SPECIAL < 20 ? 20 : drop.getChance() / DropChances.SPECIAL);
					
					def.allHigherDrops = Stream.concat(Arrays.stream(def.uncommonDrops), 
							Stream.concat(Arrays.stream(def.rareDrops), 
									Stream.concat(Arrays.stream(def.veryRareDrops), Arrays.stream(def.specialDrops))))
		                      .toArray(NPCDrop[]::new);
					
					for (int i : def.npcIds)
						definitions.put(i, def);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(definitions.size() + " npc drop definitions loaded...");
	}

	public boolean hasRdtAccess() {
		return rdtAccess;
	}

	public int getRdtChance() {
		return rdtChance;
	}
	
	public NPCDrop[] getAlwaysDrops() {
		return alwaysDrops;
	}

	public NPCDrop[] getCommonDrops() {
		return commonDrops;
	}

	public NPCDrop[] getUncommonDrops() {
		return uncommonDrops;
	}

	public NPCDrop[] getRareDrops() {
		return rareDrops;
	}

	public NPCDrop[] getVeryRareDrops() {
		return veryRareDrops;
	}

	public NPCDrop[] getSpecialDrops() {
		return specialDrops;
	}

	public NPCDrop[] getAllHigherDrops() {
		return allHigherDrops;
	}
	
	private int[] npcIds;
	
	private boolean rdtAccess;
	
	private int rdtChance;
	
	private NPCDrop[] alwaysDrops = new NPCDrop[] {};
	
	private NPCDrop[] commonDrops = new NPCDrop[] {};
	
	private NPCDrop[] uncommonDrops = new NPCDrop[] {};
	
	private NPCDrop[] rareDrops = new NPCDrop[] {};
	
	private NPCDrop[] veryRareDrops = new NPCDrop[] {};
	
	private NPCDrop[] specialDrops = new NPCDrop[] {};
	
	private NPCDrop[] allHigherDrops;
}