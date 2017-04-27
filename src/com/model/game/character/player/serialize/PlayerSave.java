package com.model.game.character.player.serialize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.game.character.combat.effect.SkullType;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.account.Account;
import com.model.game.character.player.account.AccountType;
import com.model.game.character.player.content.KillTracker.KillEntry;
import com.model.game.character.player.content.bounty_hunter.BountyHunterConstants;
import com.model.game.item.Item;
import com.model.game.item.container.impl.Bank;
import com.model.game.item.container.impl.Equipment;
import com.model.game.item.container.impl.Inventory;
import com.model.game.item.container.impl.RunePouchContainer;

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
				Rights right = Rights.get(details.rights);
				player.setRights(right);
				System.out.printf("Rights check: Name %s v Rights %d right %s%n", details.username, details.rights, right);
				player.teleportToX = player.inTutorial() ? 3087 : details.x;
				player.teleportToY = player.inTutorial() ? 3499 : details.y;
				player.teleHeight = details.z;
				player.setIdentity(details.identity);
				player.setMacAddress(details.mac);
				player.setPetSpawned(details.petSpawned);
				player.setPet(details.pet);
				player.setTutorial(details.inTutorial);
				player.setReceivedStarter(details.starterReceived);
				AccountType type = Account.get(details.gameMode);
				if (type != null)
					player.getAccount().setType(type);
				player.setSpecialAmount(details.specialAmount);
				player.setSpellBook(details.spellbook);
				player.getSkills().setExpCounter(details.expCounter);
				player.setRecoil(details.recoil);
				player.setVotePoints(details.votePoints);
				player.setTotalVotes(details.totalVotes);
				player.setSlayerPoints(details.slayerPoints);
				player.getAchievements().setPoints(details.achievementPoints);
				player.setAmountDonated(details.amountDonated);
				player.setTotalAmountDonated(details.totalAmountDonated);
				player.teleblock.reset();
				player.teleblockLength = details.teleblockDuration;
				player.isMuted = details.muted;
				player.setSkullType(details.skullType);
				player.setSkullTimer(details.skullDuration);
				player.infection = details.infectionType;
				player.setSlayerTask(details.slayertask);
				player.setSlayerTaskAmount(details.slayerTaskAmount);
				player.setSlayerTaskDifficulty(details.taskDifficulity);
				player.setFirstSlayerTask(details.completedFirstTask);
				player.setFirstBossSlayerTask(details.completedFirstBossTask);
				player.setCanTeleportToTask(details.teleportToTaskUnlocked);
				player.setTempKey(details.lastClanEntered);
				player.isClanMuted = details.clanPunishment;
				player.setPreserveUnlocked(details.unlockedPreserve);
				player.setRigourUnlocked(details.unlockedRigour);
				player.setAuguryUnlocked(details.unlockedAugury);
				player.setCurrentKillStreak(details.currentKillStreak);
				player.setHighestKillStreak(details.highestKillStreak);
				player.setWildernessKillStreak(details.wildernessKillStreak);
				player.lastKilledList.add(details.lastKilled);
				player.setKillCount(details.killCount);
				player.setDeathCount(details.deathCount);
				player.setAttribute(BountyHunterConstants.ROGUE_CURRENT, details.rogueCurrent);
				player.setAttribute(BountyHunterConstants.ROGUE_RECORD, details.rogueRecord);
				player.setAttribute(BountyHunterConstants.HUNTER_CURRENT, details.hunterCurrent);
				player.setAttribute(BountyHunterConstants.HUNTER_RECORD, details.hunterRecord);
				player.setBountyPoints(details.bountyPoints);
				player.setRunning(details.running);
				player.setAutoRetaliating(details.retaliating);
				player.setAttackStyle(details.attackStyle);
				player.setAttackStyleConfig(details.attackStyleConfig);
				player.setScreenBrightness(details.brightness);
				player.setSplitPrivateChat(details.splitPrivateChat);
				player.setEnableMusic(details.music);
				player.setAttribute("music_volume", details.musicVolume);
				player.setEnableSound(details.sounds);
				player.setAttribute("sound_volume", details.soundsVolume);
				if (details.killTracker != null) {
					player.setKillTracker(details.killTracker);
				}
				if (details.friendList.size() > 0) {
					player.getFAI().setFriendsList(details.friendList);
				}
				if (details.ignoreList.size() > 0) {
					player.getFAI().setIgnoreList(details.ignoreList);
				}
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
		private final String identity;
		private final String mac;
		private final boolean inTutorial;
		private final boolean starterReceived;
		private final String gameMode;
		private final boolean petSpawned;
		private final int pet;
		private final int specialAmount;
		private final SpellBook spellbook;
		private final int expCounter;
		private final int recoil;
		private final int votePoints;
		private final int totalVotes;
		private final int slayerPoints;
		private final int achievementPoints;
		private final int amountDonated;
		private final int totalAmountDonated;
		private final int teleblockDuration;
		private final boolean muted;
		private final SkullType skullType;
		private final int skullDuration;
		private final int infectionType;
		private final int slayertask;
		private final int slayerTaskAmount;
		private final int taskDifficulity;
		private final boolean completedFirstTask;
		private final boolean completedFirstBossTask;
		private final boolean teleportToTaskUnlocked;
		private final String lastClanEntered;
		private final boolean clanPunishment;
		private final boolean unlockedPreserve;
		private final boolean unlockedRigour;
		private final boolean unlockedAugury;
		
		private final int currentKillStreak;
		private final int highestKillStreak;
		private final int wildernessKillStreak;
		private String lastKilled;
		private final int killCount;
		private final int deathCount;
		private final int rogueCurrent;
		private final int rogueRecord;
		private final int hunterCurrent;
		private final int hunterRecord;
		private final int bountyPoints;
		private final boolean running;
		private final boolean retaliating;
		private final int attackStyle;
		private final int attackStyleConfig;
		private final byte brightness;
		private final boolean splitPrivateChat;
		private final boolean music;
		private final int musicVolume;
		private final boolean sounds;
		private final int soundsVolume;
		private CopyOnWriteArrayList<KillEntry> killTracker = new CopyOnWriteArrayList<KillEntry>();
		private final List<Long> friendList;
		private final List<Long> ignoreList;
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
			rights = player.getRights().getValue();
			x = player.getX();
			y = player.getY();
			z = player.getZ();
			identity = player.getIdentity();
			mac = player.getMacAddress();
			inTutorial = player.inTutorial();
			starterReceived = player.receivedStarter();
			gameMode = player.getAccount().getType().alias();
			petSpawned = player.isPetSpawned();
			pet = player.getPet();
			specialAmount = player.getSpecialAmount();
			spellbook = player.getSpellBook();
			expCounter = player.getSkills().getExpCounter();
			recoil = player.getRecoil();
			votePoints = player.getVotePoints();
			totalVotes = player.getTotalVotes();
			slayerPoints = player.getSlayerPoints();
			achievementPoints = player.getAchievements().getPoints();
			amountDonated = player.getAmountDonated();
			totalAmountDonated = player.getTotalAmountDonated();
			final int time = player.teleblock.isStopped() ? 0 : (int) (player.teleblockLength - player.teleblock.elapsedTime());
            final int tbTime = time > 300000 || time < 0 ? 0 : time;
			teleblockDuration = tbTime;
			muted = player.isMuted;
			skullType = player.getSkullType();
			skullDuration = player.getSkullTimer();
			infectionType = player.infection;
			slayertask = player.getSlayerTask();
			slayerTaskAmount = player.getSlayerTaskAmount();
			taskDifficulity = player.getSlayerTaskDifficulty();
			completedFirstTask = player.getFirstSlayerTask();
			completedFirstBossTask = player.getFirstBossSlayerTask();
			teleportToTaskUnlocked = player.canTeleportToSlayerTask();
			lastClanEntered = player.getClanMembership() == null ? "" : player.getClanMembership().getClanOwner();
			clanPunishment = player.getClanPunishment();
			unlockedPreserve = player.isPreserveUnlocked();
			unlockedRigour = player.isRigourUnlocked();
			unlockedAugury = player.isAuguryUnlocked();
			currentKillStreak = player.getCurrentKillStreak();
			highestKillStreak = player.getHighestKillStreak();
			wildernessKillStreak = player.getWildernessKillStreak();
			for (int i = 0; i < player.lastKilledList.size(); i++) {
				if (player.lastKilledList.get(i) != null && !player.lastKilledList.get(i).equalsIgnoreCase("null")) {
					lastKilled = player.lastKilledList.get(i);
				}
			}
			killCount = player.getKillCount();
			deathCount = player.getDeathCount();
			rogueCurrent = player.getAttribute(BountyHunterConstants.ROGUE_CURRENT, 0);
			rogueRecord = player.getAttribute(BountyHunterConstants.ROGUE_RECORD, 0);
			hunterCurrent = player.getAttribute(BountyHunterConstants.HUNTER_CURRENT, 0);
			hunterRecord = player.getAttribute(BountyHunterConstants.HUNTER_RECORD, 0);
			bountyPoints = player.getBountyPoints();
			running = player.isRunning();
			retaliating = player.isAutoRetaliating();
			attackStyle = player.getAttackStyle();
			attackStyleConfig = player.getAttackStyleConfig();
			brightness = player.getScreenBrightness();
			splitPrivateChat = player.getSplitPrivateChat();
			music = player.isEnableMusic();
			musicVolume = player.getAttribute("music_volume", 3);
			sounds = player.isEnableSound();
			soundsVolume = player.getAttribute("sound_volume", 3);
			killTracker = player.getKillTracker();	
			friendList = player.getFAI().getFriendsList();
			ignoreList = player.getFAI().getIgnoreList();
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
				
				if (details.equipment != null) {
					for (int i = 0; i < Equipment.SIZE; i++) {
						player.getEquipment().set(i, details.equipment[i]);
					}
				}
				
				if (details.inventory != null) {
					for (int i = 0; i < Inventory.SIZE; i++) {
						player.getInventory().set(i, details.inventory[i]);
					}
				}
				
				if (details.bank != null) {
					for (int i = 0; i < Bank.SIZE; i++) {
						player.bank.set(i, details.bank[i]);
					}
				}
				
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

		private final Item[] equipment;
		private final Item[] inventory;
		private final Item[] bank;
		private final Item[] runePouch;

		public PlayerContainer(Player player) {
			inventory = player.getInventory().toArray();
			equipment = player.getEquipment().toArray();
			bank = player.getBank().toArray();
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
        file = new File("./data/characters/details/" + name + ".json");
        return file != null && file.exists();
    }
}