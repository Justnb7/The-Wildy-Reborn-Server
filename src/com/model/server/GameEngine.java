package com.model.server;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

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
	private final GameSequencer sequencer = new GameSequencer();
	
	public void start() {
		LOGGER.info("Starting game engine...");
		sequencer.getEngine().initialize();
	}

}
