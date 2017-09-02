package com.venenatis.game.model.entity.player.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.venenatis.game.content.KillTracker.KillEntry;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.bounty.BountyHunterConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler.PrayerData;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.account.AccountType;

/**
 * Handles saving a player's container and details into a json file.
 * 
 * @author Patrick van Elderen
 * @date 23-5-2017
 *
 */
public class PlayerSave {
	
	
	public static final Gson SERIALIZE = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * The save types
	 * - Player_information is used for "name, password, IP, MAC etc.."
	 * - Presets are used to store your saved presets.
	 * - "Containers are based on Inventory, bank equipment etc..."
	 * - Farming saves all patches data per player
	 */
	public enum Type {
		PLAYER_INFORMATION,
		PRESETS,
		CONTAINER,
		FARMING;
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
		if (!PlayerPresets.loadDetails(player)) {
			return false;
		}
		if (!PlayerFarming.loadDetails(player)) {
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
			new PlayerPresets(player).parseDetails(player);
			new PlayerFarming(player).parseDetails(player);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized final boolean save(Player player, Type type) {
		try {
			if (type == Type.PLAYER_INFORMATION) {
				new PlayerSaveDetail(player).parseDetails();
			} else if (type == Type.CONTAINER) {
				new PlayerContainer(player).parseDetails(player);
			} else if (type == Type.PRESETS) {
				new PlayerPresets(player).parseDetails(player);
			} else if (type == Type.FARMING) {
				new PlayerFarming(player).parseDetails(player);
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
				final File file = new File("./Data/characters/details/" + player.getUsername() + ".json");

				if (!file.exists()) {
					return false;
				}

				reader = new BufferedReader(new FileReader(file));

				final PlayerSaveDetail details = PlayerSave.SERIALIZE.fromJson(reader, PlayerSaveDetail.class);
				player.setUsername(details.username);
				player.setPassword(details.password);
				player.setRights(details.rights);
				player.setLocation(player.isNewPlayer() ? new Location(3087, 3495, 0) : details.location);
				player.setNewPlayer(details.newPlayer);
				player.setHostAddress(details.hostAddress);
				player.setIdentity(details.identity);
				player.setMacAddress(details.mac);
				player.setMuted(details.muted);
				player.setJailed(details.jailed);
				player.setPet(details.pet);
				player.setTutorial(details.inTutorial);
				player.setReceivedStarter(details.starterReceived);
				AccountType type = Account.get(details.gameMode);
				if (type != null)
					player.getAccount().setType(type);
				player.setDidYouKnow(details.didYouKnowActivated);
				player.setTrivia(details.triviaActivated);
				player.setSpecialAmount(details.specialAmount);
				player.setSpellBook(details.spellbook);
				player.getSkills().setExpCounter(details.expCounter);
				player.getCombatState().setRingOfRecoil(details.recoil);
				
				player.setVotePoints(details.votePoints);
				player.setTotalVotes(details.totalVotes);
				player.setPkPoints(details.pkPoints);
				player.setSlayerPoints(details.slayerPoints);
				player.setAmountDonated(details.amountDonated);
				player.setTotalAmountDonated(details.totalAmountDonated);
				player.getCombatState().setTeleblockUnlock(details.teleblockDuration);
				player.setSkullType(details.skullType);
				player.setSkullTimer(details.skullDuration);
				player.setInfection(details.infectionType);
				player.setStamina(details.staminaType);
				player.setGearPoints(details.gearPoints);
				player.setLastSlayerTask(details.lastSlayerTask);
				player.setSlayerTask(details.slayertask);
				player.setSlayerTaskAmount(details.slayerTaskAmount);
				player.setSlayerTaskDifficulty(details.taskDifficulity);
				player.setFirstSlayerTask(details.completedFirstTask);
				player.setFirstBossSlayerTask(details.completedFirstBossTask);
				player.setCanTeleportToTask(details.teleportToTaskUnlocked);
				player.setSlayerStreak(details.slayerStreak);
				player.setSlayerStreakRecord(details.slayerStreakRecord);
				player.setClanChat(details.clan);
				player.setPreserveUnlocked(details.unlockedPreserve);
				player.setRigourUnlocked(details.unlockedRigour);
				player.setAuguryUnlocked(details.unlockedAugury);
				player.setCurrentKillStreak(details.currentKillStreak);
				player.setHighestKillStreak(details.highestKillStreak);
				if (details.lastKilledPlayers != null) {
					player.setLastKilledPlayers(details.lastKilledPlayers);
				}
				player.setKillCount(details.killCount);
				player.setDeathCount(details.deathCount);
				player.setAttribute(BountyHunterConstants.ROGUE_CURRENT, details.rogueCurrent);
				player.setAttribute(BountyHunterConstants.ROGUE_RECORD, details.rogueRecord);
				player.setAttribute(BountyHunterConstants.HUNTER_CURRENT, details.hunterCurrent);
				player.setAttribute(BountyHunterConstants.HUNTER_RECORD, details.hunterRecord);
				player.setBountyPoints(details.bountyPoints);
				player.setAutoRetaliating(details.retaliating);
				player.setAttackStyle(details.attackStyle);
				player.setAttackStyleConfig(details.attackStyleConfig);
				player.setScreenBrightness(details.brightness);
				player.setSplitPrivateChat(details.splitPrivateChat);
				player.setRunEnergy(details.runEnergy);
				player.getWalkingQueue().setRunningToggled(details.runToggled);
				player.setEnableMusic(details.music);
				player.setAttribute("music_volume", details.musicVolume);
				player.setEnableSound(details.sounds);
				player.setAttribute("sound_volume", details.soundsVolume);
				player.setDataOrbs(details.dataOrbs);
				player.setRoofsToggled(details.roofs);
				player.setLeftClickAttack(details.leftClickAttack);
				player.setGameTimers(details.gameTimers);
				player.setTargetTracking(details.targetTracking);
				player.setGroundItems(details.groundItems);
				player.setShiftDrops(details.shiftDrop);
				if (details.killTracker != null) {
					player.setKillTracker(details.killTracker);
				}
				if (details.playerAchievements != null) {
					player.getPlayerAchievements().putAll(details.playerAchievements);
				}
				player.setAchievementPoints(details.achievementsPoints);
				if (details.friendList.size() > 0) {
					player.getFAI().setFriendsList(details.friendList);
				}
				if (details.ignoreList.size() > 0) {
					player.getFAI().setIgnoreList(details.ignoreList);
				}
				player.appearance.setLookArray(details.look);
				player.appearance.setColoursArray(details.colors);
				player.appearance.setGender(details.gender);
				player.skills.setAllExp(details.skillXP);
				player.skills.setDynamicLevels(details.dynamicLevels);
				if (details.blockedSlayerTasks != null) {
					player.getSlayerInterface().setBlockedTasks(details.blockedSlayerTasks);
				}
				if (details.slayerUnlocks != null) {
					player.getSlayerInterface().setUnlocks(details.slayerUnlocks);
				}
				if (details.slayerExtensions != null) {
					player.getSlayerInterface().setExtension(details.slayerExtensions);
				}
				if (details.quickPrayers != null) {
					player.getQuickPrayers().setPrayers(details.quickPrayers);
				}
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
		private final Rights rights;
		private final Location location;
		private final boolean newPlayer;
		private final String hostAddress;
		private final String identity;
		private final String mac;
		private final boolean muted;
		private final boolean jailed;
		private final int pet;
		private final boolean inTutorial;
		private final boolean starterReceived;
		private final String gameMode;
		private final boolean didYouKnowActivated;
		private final boolean triviaActivated;
		private final int specialAmount;
		private final SpellBook spellbook;
		private final int expCounter;
		private final int recoil;
		private final int votePoints;
		private final int totalVotes;
		private final int pkPoints;
		private final int slayerPoints;
		private final int amountDonated;
		private final int totalAmountDonated;
		private final int teleblockDuration;
		private final SkullType skullType;
		private final int skullDuration;
		private final int infectionType;
		private final int staminaType;
		private final int gearPoints;
		private final String lastSlayerTask;
		private final String slayertask;
		private final int slayerTaskAmount;
		private final int taskDifficulity;
		private final boolean completedFirstTask;
		private final boolean completedFirstBossTask;
		private final boolean teleportToTaskUnlocked;
		private final int slayerStreak;
		private final int slayerStreakRecord;
		private final String clan;
		private final boolean unlockedPreserve;
		private final boolean unlockedRigour;
		private final boolean unlockedAugury;
		
		private final int currentKillStreak;
		private final int highestKillStreak;
		private final Deque<String> lastKilledPlayers;
		private final int killCount;
		private final int deathCount;
		private final int rogueCurrent;
		private final int rogueRecord;
		private final int hunterCurrent;
		private final int hunterRecord;
		private final int bountyPoints;
		private final boolean retaliating;
		private final int attackStyle;
		private final int attackStyleConfig;
		private final byte brightness;
		private final boolean splitPrivateChat;
		private final int runEnergy;
		private boolean runToggled;
		private final boolean music;
		private final int musicVolume;
		private final boolean sounds;
		private final int soundsVolume;
		private final boolean dataOrbs;
		private final boolean roofs;
		private final boolean leftClickAttack;
		private final boolean gameTimers;
		private final boolean targetTracking;
		private final boolean groundItems;
		private final boolean shiftDrop;
		private CopyOnWriteArrayList<KillEntry> killTracker = new CopyOnWriteArrayList<KillEntry>();
		private final HashMap<AchievementList, Integer> playerAchievements;
		private final int achievementsPoints;
		private final List<Long> friendList;
		private final List<Long> ignoreList;
		private final int[] look;
		private final int[] colors;
		private final int gender;
		private final double[] skillXP;
		private final int[] dynamicLevels;
		private final ArrayList<String> blockedSlayerTasks;
		private final HashMap<Integer, String> slayerUnlocks;
		private final HashMap<Integer, Integer> slayerExtensions;
		private final PrayerData[] quickPrayers;

		
		public String user() {
			return this.username;
		}
		
		public String password() {
			return this.password;
		}

		public PlayerSaveDetail(Player player) {
			username = player.getUsername();
			password = player.getPassword();
			rights = player.getRights();
			location = player.getLocation();
			newPlayer = player.isNewPlayer();
			hostAddress = player.getHostAddress();
			identity = player.getIdentity();
			mac = player.getMacAddress();
			muted = player.isMuted();
			jailed = player.isJailed();
			pet = player.getPet();
			inTutorial = player.inTutorial();
			starterReceived = player.receivedStarter();
			gameMode = player.getAccount().getType().alias();
			didYouKnowActivated = player.is_did_you_know_activated();
			triviaActivated = player.is_trivia_activated();
			specialAmount = player.getSpecialAmount();
			spellbook = player.getSpellBook();
			expCounter = player.getSkills().getExpCounter();
			recoil = player.getCombatState().getRingOfRecoil();
			votePoints = player.getVotePoints();
			totalVotes = player.getTotalVotes();
			pkPoints = player.getPkPoints();
			slayerPoints = player.getSlayerPoints();
			amountDonated = player.getAmountDonated();
			totalAmountDonated = player.getTotalAmountDonated();
			teleblockDuration = player.getCombatState().getTeleblockUnlock();
			skullType = player.getSkullType();
			skullDuration = player.getSkullTimer();
			infectionType = player.getInfection();
			staminaType = player.getStaminaConfig();
			gearPoints = player.getGearPoints();
			lastSlayerTask = player.getLastSlayerTask();
			slayertask = player.getSlayerTask();
			slayerTaskAmount = player.getSlayerTaskAmount();
			taskDifficulity = player.getSlayerTaskDifficulty();
			completedFirstTask = player.getFirstSlayerTask();
			completedFirstBossTask = player.getFirstBossSlayerTask();
			teleportToTaskUnlocked = player.canTeleportToSlayerTask();
			slayerStreak = player.getSlayerStreak();
			slayerStreakRecord = player.getSlayerStreakRecord();
			clan = player.getClanChat();
			unlockedPreserve = player.isPreserveUnlocked();
			unlockedRigour = player.isRigourUnlocked();
			unlockedAugury = player.isAuguryUnlocked();
			currentKillStreak = player.getCurrentKillStreak();
			highestKillStreak = player.getHighestKillStreak();
			lastKilledPlayers = player.getLastKilledPlayers();
			killCount = player.getKillCount();
			deathCount = player.getDeathCount();
			rogueCurrent = player.getAttribute(BountyHunterConstants.ROGUE_CURRENT, 0);
			rogueRecord = player.getAttribute(BountyHunterConstants.ROGUE_RECORD, 0);
			hunterCurrent = player.getAttribute(BountyHunterConstants.HUNTER_CURRENT, 0);
			hunterRecord = player.getAttribute(BountyHunterConstants.HUNTER_RECORD, 0);
			bountyPoints = player.getBountyPoints();
			retaliating = player.isAutoRetaliating();
			attackStyle = player.getAttackStyle();
			attackStyleConfig = player.getAttackStyleConfig();
			brightness = player.getScreenBrightness();
			splitPrivateChat = player.getSplitPrivateChat();
			runEnergy = player.getRunEnergy();
			runToggled = player.getWalkingQueue().isRunningToggled();
			music = player.isEnableMusic();
			musicVolume = player.getAttribute("music_volume", 3);
			sounds = player.isEnableSound();
			soundsVolume = player.getAttribute("sound_volume", 3);
			dataOrbs = player.getDataOrbs();
			roofs = player.getRoofsToggled();
			leftClickAttack = player.getLeftClickAttack();
			gameTimers = player.getGameTimers();
			targetTracking = player.toggleTargetTracking();
			groundItems = player.toggleGroundItems();
			shiftDrop = player.toggleShiftClick();
			killTracker = player.getKillTracker();
			playerAchievements = player.getPlayerAchievements();
			achievementsPoints = player.getAchievementsPoints();
			friendList = player.getFAI().getFriendsList();
			ignoreList = player.getFAI().getIgnoreList();
			look = player.appearance.getLook();
			colors = player.appearance.getColors();
			gender = player.appearance.gender;
			skillXP = player.skills.getAllXP();
			dynamicLevels = player.skills.getAllDynamicLevels();
			blockedSlayerTasks = player.getSlayerInterface().getBlockedTasks();
			slayerUnlocks = player.getSlayerInterface().getUnlocks();
			slayerExtensions = player.getSlayerInterface().getExtensions();
			quickPrayers = player.getQuickPrayers().getPrayers();
		}

		public void parseDetails() throws Exception {
			File dir = new File("./data/characters/details/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("./data/characters/details/" + username + ".json", false));
				writer.write(PlayerSave.SERIALIZE.toJson(this));
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}
	
	/**
	 * Saves presets
	 * 
	 * @author Daniel
	 *
	 */
	public static final class PlayerPresets {

		public static boolean loadDetails(Player player) throws Exception {
			BufferedReader reader = null;
			try {
				final File file = new File("./data/characters/presets/" + player.getUsername() + ".json");

				if (!file.exists()) {
					return false;
				}

				reader = new BufferedReader(new FileReader(file));

				final PlayerPresets details = PlayerSave.SERIALIZE.fromJson(reader, PlayerPresets.class);

				player.getPresets().setDeathOpen(details.deathOpen);
				player.getPresets().setGearBank(details.gearBank);
				if (details.presetTitles != null) {
					player.getPresets().setPresetTitle(details.presetTitles);
				}
				if (details.presetSpellbook != null) {
					player.getPresets().setPresetSpellbook(details.presetSpellbook);
				}
				if (details.presetSkills != null) {
					player.getPresets().setPresetSkill(details.presetSkills);
				}
				if (details.presetInventory != null) {
					player.getPresets().setPresetInventory(details.presetInventory);
				}
				if (details.presetEquipment != null) {
					player.getPresets().setPresetEquipment(details.presetEquipment);
				}

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

		private final boolean deathOpen;
		private final boolean gearBank;
		private final String[] presetTitles;
		private final SpellBook[] presetSpellbook;
		private final int[][] presetSkills;
		private final Item[][] presetInventory;
		private final Item[][] presetEquipment;

		public PlayerPresets(Player player) {
			deathOpen = player.getPresets().isDeathOpen();
			gearBank = player.getPresets().isGearBank();
			presetTitles = player.getPresets().getPresetTitle();
			presetSpellbook = player.getPresets().getPresetSpellbook();
			presetSkills = player.getPresets().getPresetSkill();
			presetInventory = player.getPresets().getPresetInventory();
			presetEquipment = player.getPresets().getPresetEquipment();
		}

		public void parseDetails(Player player) throws Exception {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("./data/characters/presets/" + player.getUsername() + ".json", false));
				writer.write(PlayerSave.SERIALIZE.toJson(this));
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
			File dir = new File("./data/characters/containers/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			final File file = new File("./data/characters/containers/" + player.getUsername() + ".json");
			
			if (!file.exists()) {
				return false;
			}
			
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				final PlayerContainer details = PlayerSave.SERIALIZE.fromJson(reader, PlayerContainer.class);
				
				if (details.inventory != null) {
					for (int i = 0; i < details.inventory.length; i++) {
						player.getInventory().setSlot(i, details.inventory[i], false);
					}
				}
				
				if (details.equipment != null) {
					for (int i = 0; i < details.equipment.length; i++) {
						player.getEquipment().setSlot(i, details.equipment[i], false);
					}
				}
				
				if (details.tabAmounts != null) {
					for (int i = 0; i < details.tabAmounts.length; i++) {
						player.getBank().getTabAmounts()[i] = details.tabAmounts[i];
					}
				}
				
				if (details.bank != null) {
					for (int i = 0; i < details.bank.length; i++) {
						player.getBank().setSlot(i, details.bank[i], false);
					}
				}
				
				if (details.runePouch != null) {
					for(int i = 0; i < details.runePouch.length; i++) {
						
						player.runePouchContainer.setSlot(i, details.runePouch[i]);
					}
				}
				
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
			
			return true;
		}

		private final Item[] inventory;
		private final Item[] equipment;
		private final int[] tabAmounts;
		private final Item[] bank;
		private final Item[] runePouch;

		public PlayerContainer(Player player) {
			inventory = player.getInventory().toTrimmedArray();
			equipment = player.getEquipment().toTrimmedArray();
			tabAmounts = player.getBank().getTabAmounts();
			bank = player.getBank().toNonNullArray();
			runePouch = player.runePouchContainer.toTrimmedArray();
		}

		public void parseDetails(Player player) throws IOException {
			File dir = new File("./data/characters/containers/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			final BufferedWriter writer = new BufferedWriter(new FileWriter("./data/characters/containers/" + player.getUsername() + ".json", false));
			try {
				writer.write(PlayerSave.SERIALIZE.toJson(this));
				writer.flush();
			} finally {
				writer.close();
			}
		}
	}
	
	public static final class PlayerFarming {

		public static boolean loadDetails(Player player) throws Exception {
			File file = new File("./data/characters/farming/" + player.getUsername() + ".json");

			if (!file.exists()) {
				return false;
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				PlayerFarming details = PlayerSave.SERIALIZE.fromJson(reader, PlayerFarming.class);

				long millis = System.currentTimeMillis();

				if (details.lastFarmingTimer > 0) {
					player.getFarming().setFarmingTimer(details.farmingTimer + TimeUnit.MILLISECONDS.toMinutes(millis - details.lastFarmingTimer));
				} else {
					player.getFarming().setFarmingTimer(details.farmingTimer);
				}

				player.getFarming().getCompost().compostBins = details.compostBins;
				player.getFarming().getCompost().compostBinsTimer = details.compostBinsTimer;
				player.getFarming().getCompost().organicItemAdded = details.organicItemAdded;
				player.getFarming().getBushes().bushesStages = details.bushesStages;
				player.getFarming().getBushes().bushesSeeds = details.bushesSeeds;
				player.getFarming().getBushes().bushesState = details.bushesState;
				player.getFarming().getBushes().bushesTimer = details.bushesTimer;
				player.getFarming().getBushes().diseaseChance = details.bushesDiseaseChance;
				player.getFarming().getBushes().hasFullyGrown = details.bushesHasFullyGrown;
				player.getFarming().getBushes().bushesWatched = details.bushesWatched;
				player.getFarming().getAllotment().allotmentStages = details.allotmentStages;
				player.getFarming().getAllotment().allotmentSeeds = details.allotmentSeeds;
				player.getFarming().getAllotment().allotmentHarvest = details.allotmentHarvest;
				player.getFarming().getAllotment().allotmentState = details.allotmentState;
				player.getFarming().getAllotment().allotmentTimer = details.allotmentTimer;
				player.getFarming().getAllotment().diseaseChance = details.allotmentDiseaseChance;
				player.getFarming().getAllotment().allotmentWatched = details.allotmentWatched;
				player.getFarming().getAllotment().hasFullyGrown = details.allotmentHasFullyGrown;
				player.getFarming().getFlowers().flowerStages = details.flowerStages;
				player.getFarming().getFlowers().flowerSeeds = details.flowerSeeds;
				player.getFarming().getFlowers().flowerState = details.flowerState;
				player.getFarming().getFlowers().flowerTimer = details.flowerTimer;
				player.getFarming().getFlowers().diseaseChance = details.flowerDiseaseChance;
				player.getFarming().getFlowers().hasFullyGrown = details.flowerHasFullyGrown;
				player.getFarming().getFruitTrees().fruitTreeStages = details.fruitTreeStages;
				player.getFarming().getFruitTrees().fruitTreeSaplings = details.fruitTreeSaplings;
				player.getFarming().getFruitTrees().fruitTreeState = details.fruitTreeState;
				player.getFarming().getFruitTrees().fruitTreeTimer = details.fruitTreeTimer;
				player.getFarming().getFruitTrees().diseaseChance = details.fruitDiseaseChance;
				player.getFarming().getFruitTrees().hasFullyGrown = details.fruitHasFullyGrown;
				player.getFarming().getFruitTrees().fruitTreeWatched = details.fruitTreeWatched;
				player.getFarming().getHerbs().herbStages = details.herbStages;
				player.getFarming().getHerbs().herbSeeds = details.herbSeeds;
				player.getFarming().getHerbs().herbHarvest = details.herbHarvest;
				player.getFarming().getHerbs().herbState = details.herbState;
				player.getFarming().getHerbs().herbTimer = details.herbTimer;
				player.getFarming().getHerbs().diseaseChance = details.herbDiseaseChance;
				player.getFarming().getHops().hopsStages = details.hopsStages;
				player.getFarming().getHops().hopsSeeds = details.hopsSeeds;
				player.getFarming().getHops().hopsHarvest = details.hopsHarvest;
				player.getFarming().getHops().hopsState = details.hopsState;
				player.getFarming().getHops().hopsTimer = details.hopsTimer;
				player.getFarming().getHops().diseaseChance = details.hopDiseaseChance;
				player.getFarming().getHops().hasFullyGrown = details.hopHasFullyGrown;
				player.getFarming().getHops().hopsWatched = details.hopsWatched;
				player.getFarming().getSpecialPlantOne().specialPlantStages = details.specialPlantOneStages;
				player.getFarming().getSpecialPlantOne().specialPlantSaplings = details.specialPlantOneSeeds;
				player.getFarming().getSpecialPlantOne().specialPlantState = details.specialPlantOneState;
				player.getFarming().getSpecialPlantOne().specialPlantTimer = details.specialPlantOneTimer;
				player.getFarming().getSpecialPlantOne().diseaseChance = details.specialPlantOneDiseaseChance;
				player.getFarming().getSpecialPlantOne().hasFullyGrown = details.specialPlantOneHasFullyGrown;
				player.getFarming().getSpecialPlantTwo().specialPlantStages = details.specialPlantTwoStages;
				player.getFarming().getSpecialPlantTwo().specialPlantSeeds = details.specialPlantTwoSeeds;
				player.getFarming().getSpecialPlantTwo().specialPlantState = details.specialPlantTwoState;
				player.getFarming().getSpecialPlantTwo().specialPlantTimer = details.specialPlantTwoTimer;
				player.getFarming().getSpecialPlantTwo().diseaseChance = details.specialPlantTwoDiseaseChance;
				player.getFarming().getSpecialPlantTwo().hasFullyGrown = details.specialPlantTwoHasFullyGrown;
				player.getFarming().getTrees().treeStages = details.treeStages;
				player.getFarming().getTrees().treeSaplings = details.treeSaplings;
				player.getFarming().getTrees().treeHarvest = details.treeHarvest;
				player.getFarming().getTrees().treeState = details.treeState;
				player.getFarming().getTrees().treeTimer = details.treeTimer;
				player.getFarming().getTrees().diseaseChance = details.treeDiseaseChance;
				player.getFarming().getTrees().hasFullyGrown = details.treeHasFullyGrown;
				player.getFarming().getTrees().treeWatched = details.treeWatched;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			return true;
		}

		private final long farmingTimer;
		private final long lastFarmingTimer;

		private final int[] compostBins;
		private final long[] compostBinsTimer;
		private final int[] organicItemAdded;

		private final int[] bushesStages;
		private final int[] bushesSeeds;
		private final int[] bushesState;
		private final long[] bushesTimer;
		private final double[] bushesDiseaseChance;
		private final boolean[] bushesHasFullyGrown;
		private final boolean[] bushesWatched;

		private final int[] allotmentStages;
		private final int[] allotmentSeeds;
		private final int[] allotmentHarvest;
		private final int[] allotmentState;
		private final long[] allotmentTimer;
		private final double[] allotmentDiseaseChance;
		private final boolean[] allotmentWatched;
		private final boolean[] allotmentHasFullyGrown;

		private final int[] flowerStages;
		private final int[] flowerSeeds;
		private final int[] flowerState;
		private final long[] flowerTimer;
		private final double[] flowerDiseaseChance;
		private final boolean[] flowerHasFullyGrown;

		private final int[] fruitTreeStages;
		private final int[] fruitTreeSaplings;
		private final int[] fruitTreeState;
		private final long[] fruitTreeTimer;
		private final double[] fruitDiseaseChance;
		private final boolean[] fruitHasFullyGrown;
		private final boolean[] fruitTreeWatched;

		private final int[] herbStages;
		private final int[] herbSeeds;
		private final int[] herbHarvest;
		private final int[] herbState;
		private final long[] herbTimer;
		private final double[] herbDiseaseChance;

		private final int[] hopsStages;
		private final int[] hopsSeeds;
		private final int[] hopsHarvest;
		private final int[] hopsState;
		private final long[] hopsTimer;
		private final double[] hopDiseaseChance;
		private final boolean[] hopHasFullyGrown;
		private final boolean[] hopsWatched;

		private final int[] specialPlantOneStages;
		private final int[] specialPlantOneSeeds;
		private final int[] specialPlantOneState;
		private final long[] specialPlantOneTimer;
		private final double[] specialPlantOneDiseaseChance;
		private final boolean[] specialPlantOneHasFullyGrown;

		private final int[] specialPlantTwoStages;
		private final int[] specialPlantTwoSeeds;
		private final int[] specialPlantTwoState;
		private final long[] specialPlantTwoTimer;
		private final double[] specialPlantTwoDiseaseChance;
		private final boolean[] specialPlantTwoHasFullyGrown;

		private final int[] treeStages;
		private final int[] treeSaplings;
		private final int[] treeHarvest;
		private final int[] treeState;
		private final long[] treeTimer;
		private final double[] treeDiseaseChance;
		private final boolean[] treeHasFullyGrown;
		private final boolean[] treeWatched;

		public PlayerFarming(Player player) {
			this.farmingTimer = player.getFarming().getFarmingTimer();
			this.lastFarmingTimer = System.currentTimeMillis();
			this.compostBins = player.getFarming().getCompost().compostBins;
			this.compostBinsTimer = player.getFarming().getCompost().compostBinsTimer;
			this.organicItemAdded = player.getFarming().getCompost().organicItemAdded;
			this.bushesStages = player.getFarming().getBushes().bushesStages;
			this.bushesSeeds = player.getFarming().getBushes().bushesSeeds;
			this.bushesState = player.getFarming().getBushes().bushesState;
			this.bushesTimer = player.getFarming().getBushes().bushesTimer;
			this.bushesDiseaseChance = player.getFarming().getBushes().diseaseChance;
			this.bushesHasFullyGrown = player.getFarming().getBushes().hasFullyGrown;
			this.bushesWatched = player.getFarming().getBushes().bushesWatched;
			this.allotmentStages = player.getFarming().getAllotment().allotmentStages;
			this.allotmentSeeds = player.getFarming().getAllotment().allotmentSeeds;
			this.allotmentHarvest = player.getFarming().getAllotment().allotmentHarvest;
			this.allotmentState = player.getFarming().getAllotment().allotmentState;
			this.allotmentTimer = player.getFarming().getAllotment().allotmentTimer;
			this.allotmentDiseaseChance = player.getFarming().getAllotment().diseaseChance;
			this.allotmentWatched = player.getFarming().getAllotment().allotmentWatched;
			this.allotmentHasFullyGrown = player.getFarming().getAllotment().hasFullyGrown;
			this.flowerStages = player.getFarming().getFlowers().flowerStages;
			this.flowerSeeds = player.getFarming().getFlowers().flowerSeeds;
			this.flowerState = player.getFarming().getFlowers().flowerState;
			this.flowerTimer = player.getFarming().getFlowers().flowerTimer;
			this.flowerDiseaseChance = player.getFarming().getFlowers().diseaseChance;
			this.flowerHasFullyGrown = player.getFarming().getFlowers().hasFullyGrown;
			this.fruitTreeStages = player.getFarming().getFruitTrees().fruitTreeStages;
			this.fruitTreeSaplings = player.getFarming().getFruitTrees().fruitTreeSaplings;
			this.fruitTreeState = player.getFarming().getFruitTrees().fruitTreeState;
			this.fruitTreeTimer = player.getFarming().getFruitTrees().fruitTreeTimer;
			this.fruitDiseaseChance = player.getFarming().getFruitTrees().diseaseChance;
			this.fruitHasFullyGrown = player.getFarming().getFruitTrees().hasFullyGrown;
			this.fruitTreeWatched = player.getFarming().getFruitTrees().fruitTreeWatched;
			this.herbStages = player.getFarming().getHerbs().herbStages;
			this.herbSeeds = player.getFarming().getHerbs().herbSeeds;
			this.herbHarvest = player.getFarming().getHerbs().herbHarvest;
			this.herbState = player.getFarming().getHerbs().herbState;
			this.herbTimer = player.getFarming().getHerbs().herbTimer;
			this.herbDiseaseChance = player.getFarming().getHerbs().diseaseChance;
			this.hopsStages = player.getFarming().getHops().hopsStages;
			this.hopsSeeds = player.getFarming().getHops().hopsSeeds;
			this.hopsHarvest = player.getFarming().getHops().hopsHarvest;
			this.hopsState = player.getFarming().getHops().hopsState;
			this.hopsTimer = player.getFarming().getHops().hopsTimer;
			this.hopDiseaseChance = player.getFarming().getHops().diseaseChance;
			this.hopHasFullyGrown = player.getFarming().getHops().hasFullyGrown;
			this.hopsWatched = player.getFarming().getHops().hopsWatched;
			this.specialPlantOneStages = player.getFarming().getSpecialPlantOne().specialPlantStages;
			this.specialPlantOneSeeds = player.getFarming().getSpecialPlantOne().specialPlantSaplings;
			this.specialPlantOneState = player.getFarming().getSpecialPlantOne().specialPlantState;
			this.specialPlantOneTimer = player.getFarming().getSpecialPlantOne().specialPlantTimer;
			this.specialPlantOneDiseaseChance = player.getFarming().getSpecialPlantOne().diseaseChance;
			this.specialPlantOneHasFullyGrown = player.getFarming().getSpecialPlantOne().hasFullyGrown;
			this.specialPlantTwoStages = player.getFarming().getSpecialPlantTwo().specialPlantStages;
			this.specialPlantTwoSeeds = player.getFarming().getSpecialPlantTwo().specialPlantSeeds;
			this.specialPlantTwoState = player.getFarming().getSpecialPlantTwo().specialPlantState;
			this.specialPlantTwoTimer = player.getFarming().getSpecialPlantTwo().specialPlantTimer;
			this.specialPlantTwoDiseaseChance = player.getFarming().getSpecialPlantTwo().diseaseChance;
			this.specialPlantTwoHasFullyGrown = player.getFarming().getSpecialPlantTwo().hasFullyGrown;
			this.treeStages = player.getFarming().getTrees().treeStages;
			this.treeSaplings = player.getFarming().getTrees().treeSaplings;
			this.treeHarvest = player.getFarming().getTrees().treeHarvest;
			this.treeState = player.getFarming().getTrees().treeState;
			this.treeTimer = player.getFarming().getTrees().treeTimer;
			this.treeDiseaseChance = player.getFarming().getTrees().diseaseChance;
			this.treeHasFullyGrown = player.getFarming().getTrees().hasFullyGrown;
			this.treeWatched = player.getFarming().getTrees().treeWatched;
		}

		public void parseDetails(Player player) throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/characters/farming/" + player.getUsername() + ".json", false));
			try {
				writer.write(PlayerSave.SERIALIZE.toJson(this));
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