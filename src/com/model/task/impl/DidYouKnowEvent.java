package com.model.task.impl;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.text.WordUtils;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class DidYouKnowEvent extends ScheduledTask {

	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Utility.toCyclesOrDefault(5, 5, TimeUnit.MINUTES);

	/**
	 * A {@link Collection} of messages that are to be displayed
	 */
	private final List<String> MESSAGES = Utility.jsonArrayToList(Paths.get("Data", "json", "did_you_know.json"), String[].class);

	/**
	 * The index or position in the list that we're currently at
	 */
	private int position;

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public DidYouKnowEvent() {
		super(INTERVAL);
	}

	@Override
	public void execute() {
		position++;
		if (position >= MESSAGES.size()) {
			position = 0;
		}
		List<String> messages = Arrays.asList(WordUtils.wrap(MESSAGES.get(position), 65).split("\\n"));
		messages.set(0, "[<col=255>Did you know?</col>] " + messages.get(0));
		
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.didYouKnow)
					messages.forEach(m -> player.getActionSender().sendMessage(m));
			}
		}
	}

}