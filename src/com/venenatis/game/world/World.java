package com.venenatis.game.world;

import com.google.common.collect.Sets;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.FriendAndIgnoreList;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.clan.ClanManager;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.MobileCharacterList;
import com.venenatis.game.model.entity.Entity.EntityType;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.updating.NpcUpdating;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.save.PlayerSerialization;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.task.Service;
import com.venenatis.game.task.impl.DidYouKnowEvent;
import com.venenatis.game.task.impl.InstanceFloorReset;
import com.venenatis.game.task.impl.NPCMovementTask;
import com.venenatis.game.task.impl.RestoreSpecialStats;
import com.venenatis.game.task.impl.RestoreStats;
import com.venenatis.game.task.impl.SavePlayers;
import com.venenatis.game.util.NameUtils;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Holds data global to the game world.
 * 
 * @author Graham Edgecombe
 * 
 */
public class World implements Service {
	
	/**
	 * Logging class.
	 */
	private static final Logger logger = Logger.getLogger(World.class.getName());

	/**
	 * World instance.
	 */
	private static final World world = new World();
	
	/**
	 * Gets the world instance.
	 * 
	 * @return The world instance.
	 */
	public static World getWorld() {
		return world;
	}

	/**
	 * The ExecutorService used for Entity synchronization
	 */
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	/**
	 * A list of active NPCs.
	 */
	private final MobileCharacterList<NPC> npcs = new MobileCharacterList<>(Constants.MAX_NPCS);

	/**
	 * A list of connected players.
	 */
	public final MobileCharacterList<Player> players = new MobileCharacterList<>(Constants.MAX_PLAYERS);

	public final RegionStoreManager regions = new RegionStoreManager();

	/**
	 * A queue of players waiting to be logged in
	 */
	private Set<Player> queuedLogouts = Sets.newConcurrentHashSet();

	/**
	 * The update has been announced
	 */
	public static boolean updateAnnounced;

	/**
	 * An update is currently running
	 */
	public static boolean updateRunning;

	/**
	 * The seconds before the update takes place
	 */
	public static int updateSeconds;

	/**
	 * The time the update started
	 */
	public static long updateStartTime;

	/**
	 * Should kick all players online off so we can finish the update
	 */
	private boolean kickAllPlayers = false;

	public static int gametick;

	/**
	 * Queues a player to be logged out
	 *
	 * @param player The {@link Player} to add to the queue to be logged out
	 */
	public void queueLogout(Player player) {
		if (getPlayers().get(player.getIndex()) == null) {
			// prevent a player who is already null from being added
			return;
		}
		queuedLogouts.add(player);
	}

	/**
	 * Initializes the game world
	 */
	public void init() {
		schedule(new DidYouKnowEvent());
		schedule(new RestoreStats());
		schedule(new NPCMovementTask());
		schedule(new RestoreSpecialStats());
		schedule(new SavePlayers());
		schedule(new BountyHunter());
		schedule(new InstanceFloorReset());
	}
	
	/**
	 * Shedules a task to be processed
	 *
	 * @param task
	 */
	public void schedule(ScheduledTask task) {
		Server.getTaskScheduler().schedule(task);
	}

	/**
	 * Gets the player by the name.
	 *
	 * @param playerName the player name.
	 * @return the player
	 */
	public Player getPlayerByName(String playerName) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getName().equalsIgnoreCase(playerName)) {
				return player;
			}
		}
		return null;
	}

	public Optional<Player> getPlayerByRealName(String realName) {
		return players.search(player -> player.getName().equalsIgnoreCase(realName));
	}

	/**
	 * Gets the player by the name hash.
	 *
	 * @param playerName the player name hash.
	 * @return the player
	 */
	public Player getPlayerByNameHash(long playerName) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.usernameHash == playerName) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Gets the player by the name hash.
	 *
	 * @param playerName the player name hash.
	 * @return the player
	 */

	public Optional<Player> getOptionalByNameHash(long playerName) {
		for (Player player : players) {
			if (player != null && player.usernameHash == playerName) {
				return Optional.of(player);
			}
		}
		return Optional.empty();
	}

	/**
	 * Registers an entity to the world
	 *
	 * @param entity The {@link Entity} to register to the world
	 * @return If the entity can be registered to the world
	 */
	public boolean register(Entity entity) {
		if (entity.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) entity;
			if (getPlayers().spaceLeft() == 0)
				return false;
			getPlayers().add(player);
			//set flags, 0 is flagged as bot i believe
			player.outStream.writeFrame(249);
			player.outStream.putByteA(0);
			//Sent the index to the client
			player.outStream.writeWordBigEndianA(player.getIndex());
			player.flushOutStream();
			player.getActionSender().sendLogin();
			logger.info("Registered player : " + player + " [online=" + players.size() + "]");
			return true;
		} else if (entity.getEntityType() == EntityType.NPC) {
			NPC npc = (NPC) entity;
			if (getNPCs().spaceLeft() == 0)
				return false;
			getNPCs().add(npc);
			npc.setVisible(true);
			npc.setOnTile(npc.getX(), npc.getY(), npc.getZ());
			return true; // GO
		}

		return false;
	}

	/**
	 * Unregisters an entity from the world
	 *
	 * @param entity The {@link Entity} to unregister from the world
	 */
	public void unregister(Entity entity) {
		if (entity.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) entity;

			final int index = player.getIndex();

			if (getPlayers().get(index) == null) {
				// dont unregister a null player
				return;
			}

			/*
			 * Disconnect our player
			 */
			disconnect(player);

			/*
			 * Since we've disconnected, we no longer need to be active
			 */
			player.setActive(false);

			/*
			 * Disconnect our channel
			 */
			player.getSession().getChannel().close();

			System.out.println("[Deregistered] " + player + ", Proper Logout: " + player.properLogout);
			/*
			 * Once the player is fully disconnected, we can go ahead and remove them from updating
			 */
			getPlayers().remove(index);
		} else if (entity.getEntityType() == EntityType.NPC) {
			NPC npc = (NPC) entity;
			npc.setVisible(false);
			npcs.remove(npc.getIndex());
			npc.removeFromTile();
		}
	}

	/**
	 * Handles disconnecting a player from the server and removing him from anything needing to be removed from
	 *
	 * @param player The {@link Player} to remove from the server
	 */
	public void disconnect(Player player) {
		
		/*
		 * Remove from trade
		 */
		if (player.getTradeSession().isTrading()) {
			player.getTradeSession().declineTrade(true);
		}
		
		/*
		 * Close price checker
		 */
		if (player.getInterfaceState().isInterfaceOpen(48500)) {
			player.getPriceChecker().close();
		}
		
		/*
		 * Send our controller check
		 */
		player.getController().onDisconnect(player);
		
		/*
		 * Remove from kraken instance
		 */
		
		if (player.getKraken() != null && player.getKraken().getInstance() != null)
			InstancedAreaManager.getSingleton().disposeOf(player.getKraken().getInstance());

		/*
		 * Let our friends know we've disconnected
		 */
		if (player.privateChat != FriendAndIgnoreList.OFFLINE) {
			for (Player target : World.getWorld().getPlayers()) {
				if (target == null || !target.isActive())
					continue;
				if (target.getFAI().hasFriend(player.usernameHash) && !player.getFAI().hasIgnored(target.usernameHash))
					target.getActionSender().sendFriend(player.usernameHash, 0);
			}
		}

		/*
		 * Stop all of the players tasks
		 */

		for (Iterator<ScheduledTask> t = player.getTasks().iterator(); t.hasNext();) {
			ScheduledTask task = t.next();
			if (task.isRunning()) {
				task.stop();
			}
		}

		/*
		 * Once we're done disconnecting our player, we'll go ahead and save him
		 */
		//PlayerSerialization.saveGame(player);
		PlayerSerialization.save(player);

		/*
		 * Remove from clan
		 */
		ClanManager.leaveClan(player, true);

	}

	/**
	 * The amount of unregisters to perform per cycle.
	 */
	private static final int UNREGISTERS_PER_CYCLE = 50;

	/**
	 * Finalizes unregistration for queued disconnections.
	 */
	public void handleLogouts() {

		int count = 0;

		Iterator<Player> it$ = queuedLogouts.iterator();
		while (it$.hasNext()) {
			Player player = it$.next();
			if (player.canUnregister()) {
				it$.remove();
				unregister(player);
				count++;
			}

			if (count >= UNREGISTERS_PER_CYCLE) {
				break;
			}
		}
	}

	/**
	 * Handles all of the updating for players
	 */
	@Override
	public void pulse() {
		try {
			if (kickAllPlayers) {
				for (Player player : World.getWorld().getPlayers()) {
					if (player != null) {
						player.getOutStream().writeFrame(109);
						player.flushOutStream();
						unregister(player);
						PlayerSerialization.save(player);
						System.exit(1);
					}
				}
			}
			
			// Use randomized iteration order for PID shuffling.
			for (Player player : World.getWorld().getUnorderedPlayers()) {
				if (player != null && player.isActive()) {
					handlePreUpdating(player);
				}
			}

			for (NPC npc : npcs) {
				if (npc != null && npc.isVisible()) {
					npc.process();
				}
			}

			for (Player player : players) {
				if (player == null || !player.isActive()) {
					continue;
				}
				PlayerUpdating.updatePlayer(player, player.outStream);
				NpcUpdating.updateNPC(player, player.outStream);
			}

			for (Player player : players) {
				if (player != null && player.isActive()) {
					handlePostUpdating(player);
				}
			}
			for (NPC npc : npcs) {
				if (npc != null) {
					npc.clearUpdateFlags();
				}
			}

			if (updateRunning && !updateAnnounced) {
				updateAnnounced = true;
			}
			if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000))) {
				kickAllPlayers = true;
			}

			/**
			 * We handle logouts after we've updated so we don't disrupt anything by logging the player out before
			 */
			handleLogouts();
		} catch (Exception e) {
			e.printStackTrace();
		}
		gametick++;
	}

	/**
	 * Handles all pre-updating for the player
	 *
	 * @param player The player being pre-updated
	 */
	private void handlePreUpdating(Player player) {
		try {
			player.getSession().processQueuedPackets();
		} catch (Exception e) {
			e.printStackTrace();
			queueLogout(player);
			// logout cos bad packets will fuck up entire networking
		}
		try {
			player.process();
			player.getWalkingQueue().processNextMovement();
			player.updateCoverage(player.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles post updating of the player, which handles resetting everything
	 *
	 * @param player The player being post updated
	 */
	private void handlePostUpdating(Player player) {
		try {
			player.clearUpdateFlags();
		} catch (Exception e) {
			e.printStackTrace();
			queueLogout(player);
		}
	}

	/**
	 * Retrieves a {@link HashSet} of unordered players.
	 *
	 * @return the unordered players.
	 */
	public Set<Player> getUnorderedPlayers() {
		Set<Player> randomized = new HashSet<>(players.size());
		players.forEach(randomized::add); // Don't need to shuffle because we're
											// using a HashSet which already has
											// randomized iteration order.
		return randomized;
	}
	
	/**
	 * The servers cycles?
	 */
	private static long cycles = 0L;
	
	/**
	 * Gets the cycles
	 * 
	 * @return
	 */
	public static long getCycles() {
		return cycles;
	}

	/**
	 * Gets the list of players registered in the game world
	 *
	 * @return A list of players registered to the game world
	 */
	public MobileCharacterList<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets the list of npcs registered in the game world
	 *
	 * @return A list of registered npcs to the game world
	 */
	public MobileCharacterList<NPC> getNPCs() {
		return npcs;
	}

	public Optional<Player> getOptionalPlayer(String name) {
		return getWorld().getPlayers().stream().filter(Objects::nonNull).filter(client -> client.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	/**
	 * Checks if a player is online.
	 * 
	 * @param name
	 *            The player's name.
	 * @return <code>true</code> if they are online, <code>false</code> if not.
	 */
	public boolean isPlayerOnline(String name) {
		name = NameUtils.formatName(name);
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public String getOnlineStatus(String playerName) {
        for (Player p : getWorld().players) {
                if (p == null || p.properLogout || !p.getName().equalsIgnoreCase(playerName))
                        continue;
                return "@gre@Online";
        }
        return "@red@Offline";
	}

	public void sendMessage(String message, List<Player> players) {
		for (Player player : players) {
			if (Objects.isNull(player)) {
				continue;
			}
			player.getActionSender().sendMessage(message);
		}
	}
	
	/**
	 * Sends a global message.
	 * 
	 * @param message
	 *            The message we're about to send globally.
	 * @param forStaff
	 *            Is the message for staff members only?
	 */
	public void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null || p.isYellOff() || (forStaff && p.getRights().getValue() == 0))
				continue;
			p.getActionSender().sendMessage(message);
		}
	}

	public Player getPlayer(String name) {
		for (int d = 0; d < World.getWorld().getPlayers().capacity(); d++) {
			if (World.getWorld().getPlayers().get(d) != null) {
				Player o = World.getWorld().getPlayers().get(d);
				if (o.getName().equalsIgnoreCase(name)) {
					return o;
				}
			}
		}
		return null;
	}

	public static ExecutorService getService() {
		return SERVICE;
	}

	/**
	 * Gets the active amount of players online
	 * 
	 * @return
	 */
	public int getActivePlayers() {
		int r = 0;

		for (Player players : World.getWorld().getPlayers()) {
			if (players != null) {
				r++;
			}
		}

		return r;
	}
	
	public int getStaff() {
		int amount = 0;
		for (Player players : World.getWorld().getPlayers()) {
			if (players != null) {
				if (players.getRights().isStaff()) {
					amount++;
				}
			}
		}
		return amount;
	}

}
