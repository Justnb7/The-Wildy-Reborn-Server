package com.venenatis.game.content.minigames.multiplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.boudary.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * Minigame
 * Abstract system created for multi-player minigames.
 * 
 * @author Lennard
 */
public abstract class MultiplayerMinigame {

	/**
	 * The {@link MinigameType} of this minigame.
	 */
	private final MinigameType gameType;

	/**
	 * The in-game heightlevel of this minigame.
	 */
	private final int heightLevel;
	
	/**
	 * The random of this minigame
	 */
	private final Random random = new Random();

	/**
	 * The maximum amount of players that can participate in this minigame.
	 */
	private final int maximumPlayers;

	/**
	 * An {@link ArrayList} of all {@link Player}s participating in this
	 * minigame.
	 */
	private final ArrayList<Player> players;

	/**
	 * Determines if the game has been started.
	 */
	private boolean gameStarted;

	/**
	 * The amount of game ticks that have passed since the creation of this
	 * minigame or after a reset.
	 */
	private int gameTicksPassed;
	
	/**
	 * Flag that determines if the game needs to be destroyed
	 */
	private boolean destroyed = false;

	public MultiplayerMinigame(final MinigameType gameType, final int maximumPlayers, final int heightLevel) {
		this.gameType = gameType;
		this.maximumPlayers = maximumPlayers;
		this.heightLevel = heightLevel;
		players = new ArrayList<Player>();
	}
	
	public MultiplayerMinigame(final MinigameType gameType) {
		this.gameType = gameType;
		this.maximumPlayers = -1;
		this.heightLevel = 0;
		players = new ArrayList<Player>();
	}

	/**
	 * Action executed when this minigame starts.
	 */
	public abstract void start();
	
	/**
	 * Action executed when someone joins the minigame
	 */
	public abstract void join(Player player);
	
	/**
	 * Action executed when someone leaves the minigame
	 */
	public abstract void leave(Player player);

	/**
	 * Action executed each main server tick (600 milliseconds).
	 */
	public abstract void tick();

	/**
	 * Action executed when this minigame instance get's destructed.
	 */
	public abstract void destruct();

	/**
	 * Moves all players to the given Location.
	 * 
	 * @param location
	 *            {@link Location} where all players are moved to.
	 */
	public void movePlayers(final Location location) {
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.setTeleportTarget(location);
		}
	}

	/**
	 * Moves all the players in this minigame, to the given location if they are
	 * within the given boundary. This will also remove the players that are not
	 * within the given Boundary from the minigame.
	 * 
	 * @param location
	 *            The {@link Location} where the players are being moved to.
	 * @param boundary
	 *            The {@link Boundary} where the players needs to be in to get
	 *            moved.
	 */
	public void movePlayers(final Location location, final Boundary boundary) {
		checkPlayers(boundary);
		movePlayers(location);
	}

	/**
	 * Moves all the players in this minigame to a random location determined by
	 * the given destionation boundary, this will also remove all players that
	 * are not in the given boundary.
	 * 
	 * @param destination
	 *            The {@link Boundary} destination where the players will be
	 *            teleported to.
	 * @param boundary
	 *            The {@link Boundary} where the players need to be in to get
	 *            moved.
	 */
	public void movePlayers(final Boundary destination, final Boundary boundary) {
		checkPlayers(boundary);
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.setTeleportTarget(new Location(Utility.randomRange(destination.getBottomLeft().getX(), destination.getTopRight().getX()), Utility.randomRange(destination.getBottomLeft().getY(), destination.getTopRight().getY())));
		}
	}

	/**
	 * Checks if the players participating this minigame are in the given area,
	 * they will be removed if not.
	 * 
	 * @param boundary
	 *            The given {@link Boundary}.
	 */
	public void checkPlayers(final Boundary boundary) {
		final Collection<Player> toRemove = new ArrayList<Player>();
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			if (!boundary.isIn(player)) {
				toRemove.add(player);
			}
		}
		players.removeAll(toRemove);
	}

	/**
	 * Sends a message to all the players in this minigame.
	 * 
	 * @param message
	 *            The message.
	 */
	public void messagePlayers(final String message) {
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			player.getActionSender().sendMessage(message);
		}
	}

	/**
	 * Registers the player to this minigame and moves him to the given
	 * location.
	 * 
	 * @param player
	 *            The Player to enter this minigame.
	 * @param location
	 *            The {@link Location} where this player is being moved to.
	 * @param message
	 *            The message the player will receive.
	 */
	public void enterLobby(final Player player, final Location location, final String message) {
		if (!players.contains(player)) {
			players.add(player);
			player.setTeleportTarget(location);
			player.getActionSender().sendMessage(message);
		}
	}

	public MinigameType getGameType() {
		return gameType;
	}

	public int getHeightLevel() {
		return heightLevel;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void resetGameTicksPassed() {
		gameTicksPassed = 0;
	}

	public void addGameTicksPassed() {
		gameTicksPassed++;
	}

	public int getGameTicksPassed() {
		return gameTicksPassed;
	}

	public int getMaximumPlayers() {
		return maximumPlayers;
	}

	@Override
	public String toString() {
		return "Minigame[gameType: " + gameType.name() + ", heightLevel: " + heightLevel + ", playerCount: "
				+ getPlayers().size() + "]";
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public Random getRandom() {
		return random;
	}

}