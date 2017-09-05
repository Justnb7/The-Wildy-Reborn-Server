package com.venenatis.game.world;

import com.google.common.collect.Sets;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.FriendAndIgnoreList;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Entity.EntityType;
import com.venenatis.game.model.entity.MobileCharacterList;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.updating.NpcUpdating;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights.Order;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.task.Service;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.*;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.GameEngine;
import com.venenatis.server.Server;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
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
		schedule(new SecondTask());
		schedule(new DidYouKnowEvent());
		schedule(new RestoreStats());
		schedule(new NPCMovementTask());
		schedule(new RestoreSpecialStats());
		schedule(new SavePlayers());
		schedule(new BountyHunter());
		schedule(new InstanceFloorReset());
		schedule(new GearPointsTask());
	}
	
	/**
	 * Shedules a task to be processed
	 *
	 * @param task
	 */
	public void schedule(Task task) {
		Server.getTaskScheduler().schedule(task);
	}
	
	public Player lookupPlayerByName(String playerName) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(playerName)) {
				return player;
			}
		}
		return null;
	}
	
	/**
	 * Gets the player by the name.
	 *
	 * @param name the player name.
	 * @return the player
	 */
	public Optional<Player> getPlayerByName(String name) {
		return players.stream().filter(Objects::nonNull).filter($it -> $it.getUsername().equalsIgnoreCase(name)).findFirst();
	}

	public Optional<Player> getPlayerByRealName(String realName) {
		return players.search(player -> player.getUsername().equalsIgnoreCase(realName));
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
			if (getNPCs().spaceLeft() == 0) {
				System.err.println("Max NPCS reached, cannot spawn more!");
				return false;
			}
			getNPCs().add(npc);
			npc.setVisible(true);
			npc.setOnTile(npc.getX(), npc.getY(), npc.getZ());
			return true;
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

			if (player.getVenomDrainTick() != null) {
				player.getVenomDrainTick().stop();
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

			/*
			 * Once we're done disconnecting our player, we'll go ahead and save him
			 */
			GameEngine.loginMgr.requestSave(player);

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
		 * Remove from duel
		 */
		if (player.getDuelArena().isInSession()) {
			player.getDuelArena().declineDuel(true);
		}

		/*
		 * Reward our opponent when we kick our session
		 */
		if (player.getDuelArena().isDueling()) {
			player.getDuelArena().getOther().get().getDuelArena().setWon(true);
			player.getDuelArena().setWon(false);
			player.getDuelArena().onEnd();
		}
		
		/*
		 * Close price checker
		 */
		if (player.getInterfaceState().isInterfaceOpen(48500)) {
			player.getPriceChecker().close();
		}
		
		/*
		 * Remove from zulrah instance
		 */
		if (player.getZulrahEvent().getInstancedZulrah() != null) {
			InstancedAreaManager.getSingleton().disposeOf(player.getZulrahEvent().getInstancedZulrah());
		}
		
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

		for (Iterator<Task> t = player.getTasks().iterator(); t.hasNext();) {
			Task task = t.next();
			if (task.isRunning()) {
				task.stop();
			}
		}

		/*
		 * Remove from clan
		 */
		ClanManager.leave(player, true);

		MinigameHandler.execute(player, $it -> $it.onLogout(player));

		//Reset poison and venom
		player.setInfection(0);
		player.getController().onLogout(player);
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
		long startTime = System.currentTimeMillis();
		try {
			if (kickAllPlayers) {
				for (Player player : World.getWorld().getPlayers()) {
					if (player != null) {
						player.getOutStream().writeFrame(109);
						player.flushOutStream();
						unregister(player);
						// Force save, don't don't put it in the queue otherwise we'll just kill the server below without saving.
						PlayerSave.save(player);
						System.exit(1);
					}
				}
			}
			
			final long player_pre = System.currentTimeMillis();
			// Use randomized iteration order for PID shuffling.
			for (Player player : World.getWorld().getUnorderedPlayers()) {
				if (player != null && player.isActive()) {
					handlePreUpdating(player);
				}
			}
			GameEngine.profile.wp.player_pre = System.currentTimeMillis() - player_pre;

			final long npc_pre = System.currentTimeMillis();
			for (NPC npc : npcs) {
				if (npc != null && npc.isVisible()) {
					npc.process();
				}
			}
			GameEngine.profile.wp.npc_pre = System.currentTimeMillis() - npc_pre;

			final long upd = System.currentTimeMillis();
			for (Player player : players) {
				if (player == null || !player.isActive()) {
					continue;
				}
				PlayerUpdating.updatePlayer(player, player.outStream);
				NpcUpdating.updateNPC(player, player.outStream);
			}
			GameEngine.profile.wp.update = System.currentTimeMillis() - upd;

			final long player_post = System.currentTimeMillis();
			for (Player player : players) {
				if (player != null && player.isActive()) {
					handlePostUpdating(player);
				}
			}
			GameEngine.profile.wp.player_post = System.currentTimeMillis() - player_post;

			final long npc_post = System.currentTimeMillis();
			for (NPC npc : npcs) {
				if (npc != null) {
					npc.clearUpdateFlags();
				}
			}
			GameEngine.profile.wp.npc_post = System.currentTimeMillis() - npc_post;

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
		
		long endTime = System.currentTimeMillis() - startTime;
		GameEngine.profile.world = endTime;
		//System.out.println("[World pulse] end time: "+endTime + " : players online: " + World.getWorld().getPlayers().size());
	}

	/**
	 * Handles all pre-updating for the player
	 *
	 * @param player The player being pre-updated
	 */
	private void handlePreUpdating(Player player) {
		//long startTime = System.currentTimeMillis();
		try {
			 // Use randomized iteration order for PID shuffling.
	        for (Player p : World.getWorld().getUnorderedPlayers()) {
	            if (p != null && p.getSession() != null && p.isActive()) {
					p.getSession().processQueuedPackets();
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
			queueLogout(player);
			// logout cos bad packets will fuck up entire networking
		}
		try {
			long p1 = System.currentTimeMillis();
			player.process();
			GameEngine.profile.pp.process += System.currentTimeMillis()-p1;
			
			p1 = System.currentTimeMillis();
			player.getWalkingQueue().processNextMovement();
			GameEngine.profile.pp.walk += System.currentTimeMillis()-p1;

			p1 = System.currentTimeMillis();
			player.updateCoverage(player.getLocation());
			GameEngine.profile.pp.coverage += System.currentTimeMillis()-p1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//long endTime = System.currentTimeMillis() - startTime; System.out.println("[handlePreUpdating] end time: "+endTime + " : players online: " + World.getWorld().getPlayers().size());
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
		return getWorld().getPlayers().stream().filter(Objects::nonNull).filter(client -> client.getUsername().equalsIgnoreCase(name)).findFirst();
	}
	
	public int getPlayerCount() {
		return Math.toIntExact(getPlayers().stream().filter(Objects::nonNull).count());
	}

	public int getPvPCount() {
		return Math.toIntExact(getPlayers().stream().filter(Objects::nonNull).filter($it -> Area.inWilderness($it)).count());
	}

	public int getStaffCount() {
		return Math.toIntExact(getPlayers().stream().filter(Objects::nonNull).filter($it -> $it.getRights().isStaffMember($it)).count());
	}

	public void yell(String message) {
		getPlayers().stream().filter(Objects::nonNull).forEach($it -> $it.getActionSender().sendMessage(message));
	}
	
	public void kickPlayer(Predicate<Player> condition) {
		getPlayers().stream().filter(Objects::nonNull).filter($it -> condition.test($it)).forEach($it -> {
			$it.logout();
		});
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
			if (p == null || p.isYellOff() || (forStaff && p.getRights().getOrder() == Order.PLAYER))
				continue;
			p.getActionSender().sendMessage(message);
		}
	}
	
	/**
	 * The {@likn Set} of banned ips.
	 */
	public static final Set<String> IP_BANS = new HashSet<>();
	
	public Set<String> getIpBans() {
		return IP_BANS;
	}
	
	/**
	 * The {@link Set} of banned mac addresses.
	 */
	public static final Set<String> MAC_BANS = new HashSet<>();

	public Set<String> getMacBans() {
		return MAC_BANS;
	}

	public static ExecutorService getService() {
		return SERVICE;
	}

	public void sendMessageToStaff(String message) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.getRights().getCrown() > 0 && player.getRights().getCrown() < 3) {
					Player client = player;
					client.getActionSender().sendMessage("<col=255>[STAFF MESSAGE] " + message + "</col>");
				}
			}
		}
	}

	public String getOnlineStatus(String playerName) {
		for (Player p : getWorld().getPlayers()) {
			if (p == null || p.properLogout || !p.getUsername().equalsIgnoreCase(playerName))
				continue;
			return "@gre@Online";
		}
		return "@red@Offline";
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

}
