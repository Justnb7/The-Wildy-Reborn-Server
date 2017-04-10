package com.model.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Sets;
import com.model.Server;
import com.model.game.character.Entity;
import com.model.game.character.Entity.EntityType;
import com.model.game.character.MobileCharacterList;
import com.model.game.character.npc.NPC;
import com.model.game.character.npc.NpcUpdating;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.content.FriendAndIgnoreList;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.content.trivia.TriviaBot;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.character.player.packets.out.SendFriendPacket;
import com.model.game.character.player.serialize.PlayerSave;
import com.model.service.Service;
import com.model.task.ScheduledTask;
import com.model.task.impl.InstanceFloorReset;
import com.model.task.impl.NPCMovementTask;
import com.model.task.impl.RestoreSpecialStats;
import com.model.task.impl.RestoreStats;
import com.model.task.impl.SavePlayers;

/**
 * Represents a 'World' where we can update entities
 *
 * @author Tim
 * @author Mobster
 */
public class World implements Service {

	/**
	 * Our world singleton
	 */
	private static World WORLD = new World();

	/**
	 * The ExecutorService used for Entity synchronization
	 */
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	/**
	 * A list of all registered npcs in the world
	 */
	private final MobileCharacterList<NPC> NPCS = new MobileCharacterList<>(2000);

	/**
	 * A list of all players registered to the world
	 */
	public final MobileCharacterList<Player> PLAYERS = new MobileCharacterList<>(1200);
	

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
		schedule(new RestoreStats());
		schedule(new NPCMovementTask());
		schedule(new RestoreSpecialStats());
		schedule(new SavePlayers());
		schedule(new BountyHunter());
		schedule(new InstanceFloorReset());
		TriviaBot.declare();
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
		return PLAYERS.search(player -> player.getName().equalsIgnoreCase(realName));
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
		for (Player player : PLAYERS) {
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
			player.initialize();
			return true;
		} else if (entity.getEntityType() == EntityType.NPC) {
			NPC npc = (NPC) entity;
			if (getNPCs().spaceLeft() == 0)
				return false;
			getNPCs().add(npc);
			npc.setVisible(true);
			npc.setOnTile(npc.absX, npc.absY, npc.heightLevel);
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
			npc.setAbsX(-1);
			npc.setAbsY(-1);
			npc.setVisible(false);
			NPCS.remove(npc.getIndex());
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
		if (Trading.isTrading(player)) {
			Trading.decline(player);
		}
		
		/*
		 * Send our controller check
		 */
		player.getController().onDisconnect(player);

		/*
		 * Stop all player related tasks
		 */
		
		player.setOpenShop(null);
		
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
					target.write(new SendFriendPacket(player.usernameHash, 0));
			}
		}

		/*
		 * Finally allow ourself to be saved
		 */
		player.saveFile = true;
		player.saveCharacter = true;

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
		PlayerSave.save(player);

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
						//PlayerSerialization.saveGame(player);
						PlayerSave.save(player);
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

			for (NPC npc : NPCS) {
				if (npc != null && npc.isVisible()) {
					npc.process();
				}
			}

			for (Player player : PLAYERS) {
				if (player == null || !player.isActive()) {
					continue;
				}
				PlayerUpdating.updatePlayer(player, player.outStream);
				NpcUpdating.updateNPC(player, player.outStream);
			}

			for (Player player : PLAYERS) {
				if (player != null && player.isActive()) {
					handlePostUpdating(player);
				}
			}
			for (NPC npc : NPCS) {
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
	}

	/**
	 * Handles all pre-updating for the player
	 *
	 * @param player The player being pre-updated
	 */
	private void handlePreUpdating(Player player) {
		try {
			player.getSession().processQueuedPackets();
			player.process();
			player.getMovementHandler().process();
		} catch (Exception e) {
			e.printStackTrace();
			queueLogout(player);
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
		Set<Player> randomized = new HashSet<>(PLAYERS.size());
		PLAYERS.forEach(randomized::add); // Don't need to shuffle because we're
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
		return PLAYERS;
	}

	/**
	 * Gets the list of npcs registered in the game world
	 *
	 * @return A list of registered npcs to the game world
	 */
	public MobileCharacterList<NPC> getNPCs() {
		return NPCS;
	}

	/**
	 * Gets our world singleton
	 *
	 * @return
	 */
	public static World getWorld() {
		return WORLD;
	}

	public Optional<Player> getOptionalPlayer(String name) {
		return getWorld().getPlayers().stream().filter(Objects::nonNull).filter(client -> client.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	public String getOnlineStatus(String playerName) {
        for (Player p : getWorld().PLAYERS) {
                if (p == null || p.properLogout || !p.getName().equalsIgnoreCase(playerName))
                        continue;
                return "@gre@Online";
        }
        return "@red@Offline";
	}

	public static void sendMessage(String message, List<Player> players) {
		for (Player player : players) {
			if (Objects.isNull(player)) {
				continue;
			}
			player.getActionSender().sendMessage(message);
		}
	}
	
	public void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff() || (forStaff && p.getRights().getValue() == 0))
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
