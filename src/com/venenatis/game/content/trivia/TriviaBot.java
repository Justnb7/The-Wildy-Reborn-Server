package com.venenatis.game.content.trivia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class TriviaBot {
	
	/**
	 * The logger for the class.
	 */
	private static Logger logger = Logger.getLogger(TriviaBot.class.getSimpleName());

	/**
	 * Holds all the bot data.
	 */
	public final static Set<TriviaBotData> BOT_DATA = new HashSet<>();

	/**
	 * The current question/answer set.
	 */
	private static TriviaBotData current = null;

	/**
	 * Holds all the TriviaBot attempted answers.
	 */
	public final static ArrayList<String> attempts = new ArrayList<String>();
	
	/**
	 * Declares the TriviaBot data.
	 */
	public static void declare() {
		for (TriviaBotData data : TriviaBotData.values()) {
			BOT_DATA.add(data);
		}
		logger.info("Loaded " + BOT_DATA.size() + " TriviaBot questions.");
		initialize();
	}

	/**
	 * Initializes the TriviaBot task
	 */
	public static void initialize() {
		Server.getTaskScheduler().schedule(new ScheduledTask(600) {
			@Override
			public void execute() {
				assign();
			}
			@Override
			public void onStop() {
			}
		});
	}

	/**
	 * Assigns a new question
	 */
	private static void assign() {
		current = Utility.randomElement(BOT_DATA);
		World.getWorld().sendWorldMessage("<img=8><col=0066FF><shad=222222>[Trivia]: " + current.getQuestion(), false);
	}

	/**
	 * Handles player answering the question
	 * 
	 * @param player
	 * @param answer
	 */
	public static void answer(Player player, String answer) {
		if (current == null) {
			player.getActionSender().sendMessage("That round has already been won, wait for the next round.");
			return;
		}
		
		for (String answers : current.getAnswers()) {
			if (answers.equalsIgnoreCase(answer)) {
				reward(player, answer);
				return;
			}
		}
		
		player.getActionSender().sendMessage("That answer wasn't correct, please try it again.");
		attempts.add(answer);
	}

	/**
	 * Resets the Trivia Bot
	 */
	private static final void reset() {
		current = null;
		attempts.clear();
	}
	
	private static Item[] REWARDS = {new Item(995, 100_000)};
	
	/**
	 * Reward the player with a random reward from the rewards array
	 * @param player
	 *        The player being rewarded
	 */
	public static void reward(Player player, String answer) {
		Item reward = Utility.randomElement(REWARDS);
		World.getWorld().sendWorldMessage("<img=12><col=0066FF><shad=222222>[Trivia]: Congratulations, "+Utility.formatPlayerName(player.getName())+" has won "+reward.getName()+".", false);
		player.getInventory().addOrCreateGroundItem(reward);
		reset();
	}

}
