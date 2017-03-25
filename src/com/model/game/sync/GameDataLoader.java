package com.model.game.sync;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.omicron.jagex.runescape.CollisionMap;

import com.model.Server;
import com.model.ServerState;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.drops.NpcDropSystem;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.item.equipment.Equipment;
import com.model.net.ConnectionHandler;
import com.model.utility.cache.ObjectDefinition;
import com.model.utility.cache.map.MapLoading;
import com.model.utility.json.EquipmentRequirementLoader;
import com.model.utility.json.ItemDefinitionLoader;
import com.model.utility.json.ShopLoader;

/**
 * A class for loading all of the gamedata on server startup
 * 
 * @author Mobster
 *
 */

public class GameDataLoader implements Runnable {

	/**
	 * A logger for the {@link GameDataLoader} class
	 */
	private static final Logger logger = Logger.getLogger(GameDataLoader.class.getName());
	
	public static NPCHandler npcHandler = new NPCHandler();
	
	@Override
	public void run() {

		logger.info("Loading all of the game data.");
		try {
			// Most important first - clipping/cache related. How can we access cache info (clipping, npcdef)
			// when it's not loaded?
			ObjectDefinition.loadConfig();
			MapLoading.load();
			CollisionMap.load("Data/data/collisiondata.dat");
			// Everything else..
			NPCHandler.declare();
			NpcDropSystem.get().loadDrops();
			NpcDropSystem.get().loadRareDrops();
			new ShopLoader().load();
			new ItemDefinitionLoader().load();
			new EquipmentRequirementLoader().load();
			MobAttackSounds.declare();
			PlayerSounds.declare();
			Equipment.setMetalEquipment();
			ClanManager.init();
			ConnectionHandler.initialize();
			Server.state = ServerState.LOADED;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load the game data.", e);
		}
	}

}
