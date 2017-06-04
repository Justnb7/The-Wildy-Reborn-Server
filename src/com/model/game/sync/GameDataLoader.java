package com.model.game.sync;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omicron.jagex.runescape.CollisionMap;

import com.model.Server;
import com.model.ServerState;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.skill.fletching.fletchable.impl.Arrow;
import com.model.game.character.player.skill.fletching.fletchable.impl.Bolt;
import com.model.game.character.player.skill.fletching.fletchable.impl.Carvable;
import com.model.game.character.player.skill.fletching.fletchable.impl.Crossbow;
import com.model.game.character.player.skill.fletching.fletchable.impl.Featherable;
import com.model.game.character.player.skill.fletching.fletchable.impl.Stringable;
import com.model.game.definitions.NPCDefinitions;
import com.model.net.ConnectionHandler;
import com.model.utility.cache.ObjectDefinition;
import com.model.utility.cache.map.MapLoading;
import com.model.utility.parser.impl.EquipmentDefinitionParser;
import com.model.utility.parser.impl.ItemDefinitionParser;
import com.model.utility.parser.impl.NPCDefinitionParser;
import com.model.utility.parser.impl.WeaponDefinitionParser;

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
			Arrow.declare();
			Bolt.declare();
			Carvable.declare();
			Crossbow.declare();
			Featherable.declare();
			Stringable.declare();
			Server.npcHandler.declare();
			Arrays.fill(NPCDefinitions.getDefinitions(), null);
	        new NPCDefinitionParser().run();
			new ItemDefinitionParser().run();
			new WeaponDefinitionParser().run();
			new EquipmentDefinitionParser().run();
			MobAttackSounds.declare();
			PlayerSounds.declare();
			ClanManager.init();
			ConnectionHandler.initialize();
	        Server.getDropManager().read();
			Server.state = ServerState.LOADED;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load the game data.", e);
		}
	}

}
