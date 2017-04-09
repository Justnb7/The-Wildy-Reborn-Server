package com.model.game.character.player.serialize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.account.Account;
import com.model.game.character.player.account.AccountType;
import com.model.game.item.Item;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.location.Position;

/**
 * Handles saving a player's container and details into a json file.
 * 
 * @author Patrick van Elderen
 * @date 3/09/2016
 *
 */
public class PlayerSave {
	
	/**
	 * GSON
	 */
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * The Types of saves
	 */
	public enum SaveTypes {
		DETAILS,
		CONTAINER;
	}

	/**
	 * Loads both the details and container of player.
	 * 
	 * @param player
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean load(Player player) throws Exception {
		if (!PlayerSaveDetail.loadDetails(player)) {
			return false;
		}
		if (!PlayerContainer.loadDetails(player)) {
			return false;
		}
		return true;
	}

	/**
	 * Saves both the details and container of player.
	 * 
	 * @param player
	 * @return
	 */
	public static synchronized final boolean save(Player player) {
		try {
			new PlayerSaveDetail(player).parseDetails();
			new PlayerContainer(player).parseDetails(player);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized final boolean save(Player player, SaveTypes type) {
		try {
			if (type == SaveTypes.DETAILS) {
				new PlayerSaveDetail(player).parseDetails();
			} else if (type == SaveTypes.CONTAINER) {
				new PlayerContainer(player).parseDetails(player);
			}
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Handles saving and loading player's details.
	 */
	public static final class PlayerSaveDetail {

		public static boolean loadDetails(Player player) throws Exception {
			BufferedReader reader = null;
			try {
				final File file = new File("./Data/characters/details/" + player.getName() + ".json");

				if (!file.exists()) {
					return false;
				}

				reader = new BufferedReader(new FileReader(file));

				final PlayerSaveDetail details = PlayerSave.GSON.fromJson(reader, PlayerSaveDetail.class);
				player.setUsername(details.username);
				player.setPassword(details.password);
				player.setRights(Rights.values()[details.rights]);
				player.setLocation(Position.create(details.x, details.y, details.z));
				player.setTutorial(details.inTutorial);
				AccountType type = Account.get(details.gameMode);
				if (type != null)
					player.getAccount().setType(type);
				player.appearance.setLook(details.look);
				player.appearance.setGender(details.gender);
				player.skills.setAllExp(details.skillXP);
				player.skills.setDynamicLevels(details.dynamicLevels);
				return true;

			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private final String username;
		private final String password;		
		private final int rights;
		private final int x, y, z;
		private final boolean inTutorial;
		private final String gameMode;
		private final int[] look;
		private final int gender;
		private final double[] skillXP;
		private final int[] dynamicLevels;
		
		public String user() {
			return this.username;
		}
		
		public String password() {
			return this.password;
		}

		public PlayerSaveDetail(Player player) {
			username = player.getName();
			password = player.getPassword();
			rights = player.getRights().toInteger();
			x = player.getX();
			y = player.getY();
			z = player.getZ();
			inTutorial = player.inTutorial();
			gameMode = player.getAccount().getType().alias();
			look = player.appearance.getLook();
			gender = player.appearance.gender;
			skillXP = player.skills.getAllXP();
			dynamicLevels = player.skills.getAllDynamicLevels();
		}

		public void parseDetails() throws Exception {
			File dir = new File("./Data/characters/details/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("./Data/characters/details/" + username + ".json", false));
				writer.write(PlayerSave.GSON.toJson(this));
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}
	
	/**
	 * Handles saving and loading player's container.
	 *
	 */
	public static final class PlayerContainer {

		public static boolean loadDetails(Player player) throws Exception {
			File dir = new File("./Data/characters/containers/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			final File file = new File("./Data/characters/containers/" + player.getName() + ".json");
			
			if (!file.exists()) {
				return false;
			}
			
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				final PlayerContainer details = PlayerSave.GSON.fromJson(reader, PlayerContainer.class);
				
				if (details.runePouch != null) {
					for(int i = 0; i < RunePouchContainer.SIZE; i++) {
						player.runePouchContainer.set(i, details.runePouch[i]);
					}
				}
				
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
			
			return true;
		}

		private final Item[] runePouch;

		public PlayerContainer(Player player) {
			runePouch = player.runePouchContainer.toArray();
		}

		public void parseDetails(Player player) throws IOException {
			File dir = new File("./Data/characters/containers/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			final BufferedWriter writer = new BufferedWriter(new FileWriter("./Data/characters/containers/" + player.getName() + ".json", false));
			try {
				writer.write(PlayerSave.GSON.toJson(this));
				writer.flush();
			} finally {
				writer.close();
			}
		}
	}
	
	public static boolean playerExists(String name) {
        File file = null;
        file = new File("./data/characters/" + name + ".txt");
        return file != null && file.exists();
    }
}