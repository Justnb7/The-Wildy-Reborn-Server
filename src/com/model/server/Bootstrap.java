package com.model.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.omicron.jagex.runescape.CollisionMap;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.model.game.Constants;
import com.model.game.ScriptManager;
import com.model.game.World;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.content.trivia.TriviaBot;
import com.model.game.character.player.skill.fletching.fletchable.impl.*;
import com.model.game.definitions.NPCDefinitions;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.net.ConnectionHandler;
import com.model.net.network.NettyChannelHandler;
import com.model.net.network.codec.RS2Encoder;
import com.model.net.network.handshake.HandshakeDecoder;
import com.model.utility.cache.ObjectDefinition;
import com.model.utility.cache.map.MapLoading;
import com.model.utility.parser.impl.*;

/**
 * The bootstrap that will prepare the game and network.
 * 
 * @author Seven
 * @author Patrick van Elderen
 */
public class Bootstrap {
	
	/**
	 * The single logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());
	
	/**
	 * The {@link ExecutorService} that will run the startup services.
	 */
	private final ExecutorService serviceLoader = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ServiceLoaderThread").build());
	
	/**
	 * Builds the game by executing any startup services, and starting the game
	 * loop.
	 * 
	 * @return The instance of this bootstrap.
	 */
	public Bootstrap build() throws Exception {
		LOGGER.info("Loading game settings...");
		LOGGER.info("Executing startup services...");
		// Loads and caches data we will use throughout execution.
		executeServiceLoad();
		//From this point we can start the engine
		//GameEngine.initialize();
		//Now we can load our content
		serviceLoader.shutdown();
		LOGGER.info("Game Engine has been built");
		if (!serviceLoader.awaitTermination(15, TimeUnit.MINUTES)) {
			throw new IllegalStateException("The background service load took too long!");
		}
		return this;
	}

	/**
	 * Builds the network by creating the netty server bootstrap and binding to
	 * a specified port.
	 * 
	 * @return The instance of this bootstrap.
	 */
	public Bootstrap bind() throws Exception {
		LOGGER.info("Building network");
		ResourceLeakDetector.setLevel(io.netty.util.ResourceLeakDetector.Level.PARANOID);
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("encoder", new RS2Encoder());
				ch.pipeline().addLast("decoder", new HandshakeDecoder());
				ch.pipeline().addLast("handler", new NettyChannelHandler());
			}
		});
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.bind(Constants.SERVER_PORT).syncUninterruptibly();
		Server.SERVER_STARTED = true;
		LOGGER.info("Network has been bound");
		return this;
	}

	/**
	 * Executes external files to be used in game.
	 */
	private void executeServiceLoad() {

		LOGGER.info("Loading startup files..");
		serviceLoader.execute(() -> {
			ObjectDefinition.loadConfig();
			MapLoading.load();
			//Region.sort();
			try {
				CollisionMap.load("Data/data/collisiondata.dat");
			} catch (Exception e) {
				e.printStackTrace();
			}
			LOGGER.info("Loading all of the game data...");
			MobAttackSounds.declare();
			PlayerSounds.declare();
			ClanManager.init();
			ConnectionHandler.initialize();
			new ItemDefinitionParser().run();
			new WeaponDefinitionParser().run();
			new EquipmentDefinitionParser().run();
			new NPCDefinitionParser().run();
			new ShopParser().run();
			Server.npcHandler.declare();
	        Server.getDropManager().read();
		});

		LOGGER.info("Loading content...");
		serviceLoader.execute(() -> {
			Arrow.declare();
			Bolt.declare();
			Carvable.declare();
			Crossbow.declare();
			Featherable.declare();
			Stringable.declare();
			TriviaBot.declare();
			EquipmentConstants.declare();
			World.getWorld().init();
			ScriptManager.getScriptManager().loadScripts(Constants.SCRIPTS_DIRECTORY);
		});
	}

}