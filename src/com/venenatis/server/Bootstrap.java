package com.venenatis.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.venenatis.ScriptManager;
import com.venenatis.game.cache.OpenRsUnpacker;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.skills.crafting.impl.Gem;
import com.venenatis.game.content.skills.crafting.impl.Hide;
import com.venenatis.game.content.skills.fishing.FishableData;
import com.venenatis.game.content.skills.fishing.FishingSpot;
import com.venenatis.game.content.skills.fishing.ToolData;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Arrow;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Bolt;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Carvable;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Crossbow;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Featherable;
import com.venenatis.game.content.skills.fletching.fletchable.impl.Stringable;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.content.sounds_and_music.sounds.PlayerSounds;
import com.venenatis.game.model.entity.player.Sanctions;
import com.venenatis.game.model.entity.player.clan.ClanRepository;
import com.venenatis.game.net.network.NettyChannelHandler;
import com.venenatis.game.net.network.codec.RS2Encoder;
import com.venenatis.game.net.network.handshake.HandshakeDecoder;
import com.venenatis.game.util.parser.impl.EquipmentDefinitionParser;
import com.venenatis.game.util.parser.impl.ItemDefinitionParser;
import com.venenatis.game.util.parser.impl.NPCDefinitionParser;
import com.venenatis.game.util.parser.impl.ShopParser;
import com.venenatis.game.util.parser.impl.WeaponDefinitionParser;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.clipmap.MapLoading;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;

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
			
			OpenRsUnpacker.unpack();

			MapLoading.load();

			LOGGER.info("Loading all of the game data...");
			new ItemDefinitionParser().run();
			new WeaponDefinitionParser().run();
			new EquipmentDefinitionParser().run();
			new NPCDefinitionParser().run();
			Server.npcHandler.declare();
	        Server.getDropManager().read();
			new ShopParser().run();
			MobAttackSounds.declare();
			PlayerSounds.declare();
			Sanctions.initialize();
		});

		LOGGER.info("Loading content...");
		serviceLoader.execute(() -> {
			ClanRepository.load();
			//FarmingVencillio.declare();
			Gem.declare();
			Hide.declare();
			Arrow.declare();
			Bolt.declare();
			Carvable.declare();
			Crossbow.declare();
			Featherable.declare();
			Stringable.declare();
			FishableData.Fishable.declare();
			FishingSpot.declare();
			ToolData.Tools.declare();
			//TriviaBot.declare();
			World.getWorld().init();
			World.getWorld().getEventManager().appendTimer();
			ScriptManager.getScriptManager().loadScripts(Constants.SCRIPTS_DIRECTORY);
		});
	}

}