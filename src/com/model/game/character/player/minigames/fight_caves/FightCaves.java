package com.model.game.character.player.minigames.fight_caves;

import java.util.Random;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.Utility;

/**
 * The fight caves miningame
 * Started editing on June 11th
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick/">Patrick van Elderen</a> Credits to
 *         <a href="http://www.rune-server.org/members/jason/">Jason</a> for the data being used
 */
public class FightCaves {
	
	private static Random r = new Random();

	/**
	 * The player instance
	 */
	private Player player;
	
	public FightCaves(Player player) {
		this.player = player;
	}
	
	/**
	 * The kills remaining
	 */
	private int killsRemaining;

	/**
	 * Gets the remaining kills
	 */
	public int getKillsRemaining() {
		return killsRemaining;
	}

	/**
	 * Set remaining kills
	 */
	public void setKillsRemaining(int remaining) {
		this.killsRemaining = remaining;
	}
	
	/**
	 * We're outside the fightcaves
	 */
	private final Position OUTSIDE = new Position(2438, 5168, 0);
	
	/**
	 * The firecape item reward
	 */
	public static final Item FIRE_CAPE = new Item(6570);

	/**
	 * The tokkul item reward
	 */
	public static final Item TOKKUL = new Item(6529);

	/**
	 * Random set of spawn coordinates
	 */
	 public static Position getRandomLocation(Player player) {
	        final int[] locationSet = LOCATIONS[r.nextInt(LOCATIONS.length)];
	        final int x = r.nextInt(locationSet[2] - locationSet[0]) + locationSet[0];
	        final int y = r.nextInt(locationSet[3] - locationSet[1]) + locationSet[1];
	        final int z = player.getPosition().getZ();
	        return Position.create(x, y, z);
	    }

	private static int[][] LOCATIONS = { { 2376, 5065, 2387, 5076 }, { 2376, 5099, 2387, 5112 },
			{ 2408, 5103, 2419, 5113 }, { 2412, 5078, 2422, 5086 }, };

	/**
	 * A set of all waves, fight caves has 63 waves
	 */
	public int[][] getWaves(Player player) {
		return Wave.WAVES;
	}
	

	/**
	 * The wave we're currently on
	 */
	public int getCurrentWave() {
		return player.waveId;
	}

	/**
	 * Starts the minigame
	 */
	public void startWave() {
		Server.getTaskScheduler().schedule(new ScheduledTask(player, 3, Walkable.WALKABLE, Stackable.STACKABLE) {
			@Override
			public void execute() {
			
				if (player == null) {
					this.stop();
					return;
				}
				if (!Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
					player.waveId = 0;
					this.stop();
					return;
				}
				
				final int[][] wave = getWaves(player);
				
				if (player.waveId >= wave.length) {
					reward();
					this.stop();
					return;
				}
				if (player.waveId != 0 && player.waveId < wave.length)
					player.getActionSender().sendMessage("@red@Wave: " + (player.waveId + 1));
				setKillsRemaining(wave[player.waveId].length);
				player.debug("remaining: "+getKillsRemaining());
				for (int spawn = 0; spawn < getKillsRemaining(); spawn++) {
					Position spawnLoc = getRandomLocation(player);
					NPCHandler.spawnNpc(player, wave[player.waveId][spawn], spawnLoc, 1, true, false, false);
				}
				this.stop();
			}

			@Override
			public void onStop() {

			}
		});
	}

	/**
	 * A method called when you leave the cave
	 * @param type
	 *        1 - normal leave 2 - teleport 3 - death
	 */
	public void exitCave(int type) {
		if (type == 1) {
			player.move(OUTSIDE);
			player.dialogue().start("LEAVE_FIGHT_CAVE");
		} else if (type == 2) {
			// Teleport
		} else {
			player.move(OUTSIDE);
			int tokkul = getCurrentWave() * 8032 / Wave.WAVES.length;
			if (player.getItems().freeSlots() > 1) {
				player.getItems().addItem(TOKKUL.getId(), tokkul);
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(6529, tokkul), player.getX(), player.getY(), player.getHeight(), player));
			}
			player.dialogue().start("DIED_DURING_FIGHT_CAVE");
			player.getActionSender().sendMessage("You have been defeated!");
		}
		InstancedAreaManager.getSingleton().disposeOf(player.getFCI().getInstance());
		player.waveId = 0;
		killAllSpawns();
	}

	/**
	 * Stop the minigame
	 */
	public void stop() {
		player.move(OUTSIDE);
		player.waveId = 0;
		killAllSpawns();
	}

	/**
	 * Kill all spawns alive
	 */
	private void killAllSpawns() {
		for (int i = 0; i < World.getWorld().getNPCs().capacity(); i++) {
			NPC npc = World.getWorld().getNPCs().get(i);
			if (npc != null) {
				if (isFightCaveNpc(npc)) {
					if (NPCHandler.isSpawnedBy(player, npc)) {
						npc = null;
					}
				}
			}
		}
	}

	/**
	 * Checks if the entity is a fight cave enemy
	 * @param npc
	 *        The fightcave npc
	 */
	public static boolean isFightCaveNpc(NPC npc) {
		if (npc == null)
			return false;
		switch (npc.getId()) {
		case Wave.TZ_KEK_SPAWN:
		case Wave.TZ_KIH:
		case Wave.TZ_KEK:
		case Wave.TOK_XIL:
		case Wave.YT_MEJKOT:
		case Wave.KET_ZEK:
		case Wave.TZTOK_JAD:
			return true;
		}
		return false;
	}

	/**
	 * When our enemy is death send the next wave, 
	 * when we kill jad reward the player
	 * @param player
	 *        The player playing the minigame
	 * @param npc
	 *        The fightcave enemy
	 */
	public static void sendDeath(Player player, NPC npc) {
		if (npc != null) {
			if (player != null) {
				if (player.getFightCave() != null) {
					if (isFightCaveNpc(npc))
						nextWave(player, npc);
					if (npc != null && npc.getId() == 3127) {
						player.getFightCave().reward();
					}
				}
			}
		}
	}

	/**
	 * Send the next wave, weve finished our current wave
	 * @param player
	 *        The attendee
	 * @param npc
	 *        The fightcave enemy
	 */
	public static void nextWave(Player player, NPC npc) {
		if (player.getFightCave() != null) {
			player.getFightCave().setKillsRemaining(player.getFightCave().getKillsRemaining() - 1);
			if (player.getFightCave().getKillsRemaining() == 0) {
				player.waveId++;
				player.getFightCave().startWave();
			}
		}
	}

	/**
	 * During the Jad fight we can go ahead and spawn the healers
	 */
	public void spawnHealers() {
		for (int i = 0; i < 4; i++) {
			Position spawnLoc = getRandomLocation(player);
			NPC npc = new NPC(Wave.YTHURKOT, spawnLoc, 0);
			World.getWorld().getNPCs().add(npc);
		}
	}

	/**
	 * When we've killed Jad we can go ahead and reward the attendee
	 */
	public void reward() {
		player.dialogue().start("WON_FIGHT_CAVE");
		player.getItems().addItem(FIRE_CAPE.getId(), 1);
		player.getItems().addItem(TOKKUL.getId(), 10000 + Utility.random(5000));
		player.setCompletedFightCaves();
		player.getFightCave().stop();
		player.waveId = 0;
		player.getActionSender().sendMessage("You were victorious!!");
	}

}