package com.venenatis.game.net.packet.in.commands;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;

/**
 * Abstract Command.
 * 
 * @author Lennard
 *
 */
public abstract class Command {

	/**
	 * Hook holding all the actions performed when executing the command.
	 * 
	 * @param player
	 *            The {@link Player} executing this command.
	 * @param command
	 *            The full command string  that's being sent.
	 */
	protected abstract void executeCommand(Player player, String command);

	/**
	 * Optional array of {@link Rights}s that determine the ranks that can execute
	 * this command. The command will not be executed if the player does not
	 * have one of the given ranks.
	 * 
	 * @return Optional array of Rights that determine the ranks that can execute
	 *         this command.
	 */
	protected abstract Optional<Rights[]> allowedRanks();

}