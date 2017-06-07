package com.model.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.model.game.Constants;

/**
 * Represents the games core class for sequentially processes game logic.
 * 
 * @author Seven
 */
public class GameEngine {
	
	/**
	 * The single logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(GameEngine.class.getName());
	
	/**
	 * The {@link ScheduledExecutorService} that will run the game logic.
	 */
	private final ScheduledExecutorService sequencer = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("GameThread").build());
	
	public void start() {
		LOGGER.info("Starting game engine...");
		sequencer.scheduleAtFixedRate(new GameSequencer(), 600, Constants.CYLCE_RATE, TimeUnit.MILLISECONDS);
	}

}
