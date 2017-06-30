package com.venenatis.game.content.activity.minigames;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.venenatis.game.content.activity.minigames.impl.SequencedMinigame;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

/**
 * The class that contains methods to handle the functionality of minigames.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class MinigameHandler extends Task {

	/**
	 * The hash collection of active minigames.
	 */
	public static final Set<Minigame> MINIGAMES = new HashSet<>();

	/**
	 * Creates a new {@link MinigameHandler}.
	 */
	public MinigameHandler() {
		super(1, false);
	}

	@Override
	public void execute() {
		for (Iterator<Minigame> it = MINIGAMES.iterator(); it.hasNext();) {
			Minigame m = it.next();
			if (m.getRunType() != Minigame.RunType.SEQUENCED)
				continue;
			SequencedMinigame sequencer = (SequencedMinigame) m;
			if (sequencer.getCounter().incrementAndGet() == sequencer.delay()) {
				sequencer.onSequence();
				sequencer.getCounter().set(0);
			}
		}
	}

	@Override
	public void onStop() {
		World.getWorld().schedule(new MinigameHandler());
	}

	/**
	 * The method that executes {@code action} for {@code player}.
	 *
	 * @param player
	 *            the player to execute the action for.
	 * @param action
	 *            the backed minigame action to execute.
	 */
	public static void execute(Player player, Consumer<Minigame> action) {
		Optional<Minigame> minigame = search(player);
		minigame.ifPresent(action::accept);
		return;
	}

	/**
	 * The method that executes {@code function} for {@code player} that returns
	 * a result.
	 *
	 * @param player
	 *            the player to execute the function for.
	 * @param defaultValue
	 *            the default value to return if the player isn't in a minigame.
	 * @param function
	 *            the function to execute that returns a result.
	 */
	public static <T> T execute(Player player, T defaultValue, Function<Minigame, T> function) {
		Optional<Minigame> minigame = search(player);
		if (!minigame.isPresent())
			return defaultValue;
		return function.apply(minigame.get());
	}

	/**
	 * Determines if {@code player} is in any minigame.
	 *
	 * @param player
	 *            the player to determine this for.
	 * @return {@code true} if the player is in a minigame, {@code false}
	 *         otherwise.
	 */
	public static boolean contains(Player player) {
		return search(player).isPresent();
	}

	/**
	 * Retrieves the minigame that {@code player} is currently in.
	 *
	 * @param player
	 *            the player to determine the minigame for.
	 * @return the minigame that the player is currently in.
	 */
	public static Optional<Minigame> search(Player player) {
		return MINIGAMES.stream().filter(m -> m.contains(player)).findFirst();
	}
}