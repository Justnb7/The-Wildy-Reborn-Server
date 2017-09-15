package com.venenatis.game.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * The class which represents functionality for the Ring of Wealth interface <br>and for tracking down kills and or activity.
 * @author Battle OS
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class KillTracker {
	
	/**
	 * The player this is relative to
	 */
	private Player player;
	
	/**
	 * Creates a new {@link KillTracker} object for a singular player
	 * 
	 * @param player the player
	 */
	public KillTracker(Player player) {
		this.player = player;
	}
	
	private CopyOnWriteArrayList<KillEntry> killTracker = new CopyOnWriteArrayList<KillEntry>();
	
	/**
	 * The getter for the KillEntry
	 */
	public CopyOnWriteArrayList<KillEntry> getKillTracker() {
		return killTracker;
	}

	/**
	 * The setter for the KillEntry
	 */
	public void setKillTracker(CopyOnWriteArrayList<KillEntry> entry) {
		this.killTracker = entry;
	}

	/**
	 * A sub class
	 *
	 */
	public static class KillEntry {

		/**
		 * The boss we want to track
		 */
		private String name;
		
		/**
		 * The amount of kills
		 */
		private int amount;

		/**
		 * The constructor for the sub class {@link KillEntry}
		 * @param name
		 *            The boss
		 * @param amount
		 *            The amount of kills
		 */
		public KillEntry(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		/**
		 * Gets the name off the boss we've killed
		 */
		public String getName() {
			return name;
		}

		/**
		 * Checks the amount of times we've killed this boss
		 */
		public int getAmount() {
			return amount;
		}
	}
	
	/**
	 * A method used to check if the enty exists
	 * 
	 * @param entry
	 *            The entry being checked
	 */
	private final boolean entryExist(KillEntry entry) {
		return killTracker.contains(entry);
	}

	/**
	 * Sent a new submission for the killed npc
	 * 
	 * @param entry
	 *            The killed npc
	 * @param message
	 *            Checks if the player wants to receive a kill message
	 * 
	 */
	public void submit(KillEntry entry, boolean message) {
		//Safety check
		if (entry == null) {
			return;
		}
		
		//Grab the index of the entry
		int index = getIndex(entry);
		
		//If the index is above or equals 0 add ontop of the excisting entry
		if (index >= 0) {
			killTracker.get(index).amount += entry.amount;
			entry = killTracker.get(index);
		} else {
			//Otherwise create a new one, for safety, but by default we already track the array BOSSES
			killTracker.add(entry);
		}
		
		if (message) {
			player.getActionSender().sendMessage("Your " + entry.getName() + " kill count is: <col=ff0000>" + Utility.formatDigits(entry.getAmount()) + "</col>.");			
		}
	}

	/**
	 * A getter method to grab the entry index.
	 * 
	 * @param entry
	 *            The entry
	 */
	private final int getIndex(KillEntry entry) {
		for (int index = 0; index < killTracker.size(); index++) {
			if (killTracker.get(index).name.equals(entry.name)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Sents the interface i.e Ring of Wealth, can open the interface
	 */
	public void open() {

		int line = 45011;

		for (KillEntry entry : getKillTracker()) {
			
			if (entry == null) {
				continue;
			}
			
			player.getActionSender().sendString(entry.getName(), line);
			line++;
			player.getActionSender().sendString(Utility.formatDigits(entry.getAmount()), line);
			line++;
		}
		
		player.getActionSender().sendScrollBar(45010, 200);

		player.getActionSender().sendInterface(45000);
	}
	
	/**
	 * Array of all bosses.
	 */
	public final int[] BOSSES = { 
		6609, //Callisto
		2054, //Chaos Elemental
		6619, //Chaos Fanatic
		2205, //Commander Zilyana
		319, //Corporeal Beast
		239, //King Black Dragon
		3129, //K'ril Tsutsaroth
		2215, //General Graardor
		5779, //Giant mole
		6361, //Dagannoth mother
		6362, //Dagannoth mother
		6365, //Dagannoth mother
		3359, //Zombies champion
		2267, //Dagannoth rex
		2266, //Dagannoth prime
		2265, //Dagannoth supreme
	};
	
	/**
	 * By default bosses have an entry of 0 kills
	 */
	public void loadDefault() {
		for (int id : BOSSES) {
			KillEntry entry = new KillEntry(new NPC(id).getName(), 0);
			if (!entryExist(entry)) {
				submit(entry, false);
			}
		}
	}
}