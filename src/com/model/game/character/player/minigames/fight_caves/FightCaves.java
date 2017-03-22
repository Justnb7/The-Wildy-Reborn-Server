package com.model.game.character.player.minigames.fight_caves;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
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
	public int[][] SPAWN_DATA = { { 2403, 5079 }, { 2396, 5074 },
			{ 2387, 5072 }, { 2388, 5085 }, { 2389, 5096 }, { 2403, 5097 },
			{ 2410, 5087 } };

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
				final int[][] wave = getWaves(player);
				player.dialogue().start("ENTER_FIGHT_CAVE");
				if (player == null) {
					this.stop();
					return;
				}
				if (!Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
					player.waveId = 0;
					this.stop();
					return;
				}
				if (player.waveId >= wave.length) {
					reward();
					this.stop();
					return;
				}
				if (player.waveId != 0 && player.waveId < wave.length)
					player.getActionSender().sendMessage("You are now on wave " + (player.waveId + 1) + " of " + wave.length + ".");
				setKillsRemaining(wave[player.waveId].length);
				for (int i = 0; i < getKillsRemaining(); i++) {
					int npcType = wave[player.waveId][i];
					int index = Utility.random(SPAWN_DATA.length - 1);
					int x = SPAWN_DATA[index][0];
					int y = SPAWN_DATA[index][1];
					NPCHandler.spawnNpc(player, npcType, x, y, player.getIndex() * 4, 1, true, false, false);
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
			player.getPA().movePlayer(OUTSIDE);
			player.dialogue().start("LEAVE_FIGHT_CAVE");
		} else if (type == 2) {
			// Teleport
		} else {
			player.getPA().movePlayer(OUTSIDE);
			int tokkul = getCurrentWave() * 8032 / Wave.WAVES.length;
			if (player.getItems().freeSlots() > 1) {
				player.getItems().addItem(TOKKUL.getId(), tokkul);
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(6529, tokkul), player.getX(), player.getY(), player.getHeight(), player));
			}
			player.dialogue().start("DIED_DURING_FIGHT_CAVE");
			player.getActionSender().sendMessage("You have been defeated!");
		}
		player.waveId = 0;
		killAllSpawns();
	}

	/**
	 * We're now entering the caves, we can start the waves
	 */
	public void enterFightCaves() {
		player.getActionSender().sendRemoveInterfacePacket();
		player.getPA().movePlayer(2413, 5117, player.getIndex() * 4);
		player.getActionSender().sendMessage("@red@Wave: 1");
		player.waveId = 0;
		startWave();
	}

	/**
	 * Stop the minigame
	 */
	public void stop() {
		reward();
		player.getPA().movePlayer(OUTSIDE);
		player.waveId = 0;
		killAllSpawns();
	}

	/**
	 * Kill all spawns alive
	 */
	private void killAllSpawns() {
		for (int i = 0; i < World.getWorld().getNpcs().capacity(); i++) {
			NPC npc = World.getWorld().getNpcs().get(i);
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
		switch (npc.npcId) {
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
					if (npc != null && npc.npcId == 3127) {
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
				player.getActionSender().sendMessage("@red@Wave: " + player.waveId);
			}
		}
	}

	/**
	 * During the Jad fight we can go ahead and spawn the healers
	 */
	public void spawnHealers() {
		for (int i = 0; i < 4; i++) {
			int index = Utility.getRandom(SPAWN_DATA.length - 1);
			int x = SPAWN_DATA[index][0];
			int y = SPAWN_DATA[index][1];
			NPCHandler.spawnNpc(player, Wave.YTHURKOT, x, y, player.getIndex() * 4, 1, true, false, false);
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