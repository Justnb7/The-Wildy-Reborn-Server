package com.model.game.character.player;

import io.netty.buffer.Unpooled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.CombatAssistant;
import com.model.game.character.combat.CombatDamage;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.magic.LunarSpells;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.range.Projectile;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.combat.weapon.WeaponInterface;
import com.model.game.character.npc.BossDeathTracker;
import com.model.game.character.npc.NPCAggression;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.SlayerDeathTracker;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.PetCombat;
import com.model.game.character.player.content.FriendAndIgnoreList;
import com.model.game.character.player.content.achievements.AchievementHandler;
import com.model.game.character.player.content.clan.ClanMember;
import com.model.game.character.player.content.consumable.Consumable;
import com.model.game.character.player.content.consumable.food.FoodConsumable;
import com.model.game.character.player.content.consumable.potion.PotionData;
import com.model.game.character.player.content.consumable.potion.Potions;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.Duel;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.content.teleport.TeleportHandler.TeleportationTypes;
import com.model.game.character.player.content.trade.TradeState;
import com.model.game.character.player.controller.Controller;
import com.model.game.character.player.controller.ControllerManager;
import com.model.game.character.player.dialogue.DialogueHandler;
import com.model.game.character.player.dialogue.DialogueManager;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.character.player.instances.impl.KrakenInstance;
import com.model.game.character.player.packets.encode.PacketEncoder;
import com.model.game.character.player.packets.encode.impl.SendConfig;
import com.model.game.character.player.packets.encode.impl.SendInteractionOption;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendMultiWay;
import com.model.game.character.player.packets.encode.impl.SendSidebarInterface;
import com.model.game.character.player.packets.encode.impl.SendSkillPacket;
import com.model.game.character.player.packets.encode.impl.SendSoundPacket;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.skill.SkillInterfaces;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.character.player.skill.Skilling;
import com.model.game.character.player.skill.herblore.Herblore;
import com.model.game.character.player.skill.impl.Thieving;
import com.model.game.character.player.skill.mining.Mining;
import com.model.game.character.walking.MovementHandler;
import com.model.game.item.Item;
import com.model.game.item.ItemAssistant;
import com.model.game.item.UseItem;
import com.model.game.item.bank.Bank;
import com.model.game.item.bank.BankPin;
import com.model.game.item.container.impl.LootingBagContainer;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.item.container.impl.TradeContainer;
import com.model.game.item.equipment.Equipment;
import com.model.game.item.equipment.EquipmentSet;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Area;
import com.model.game.location.Location;
import com.model.game.shop.Currency;
import com.model.game.shop.Shop;
import com.model.net.network.Packet;
import com.model.net.network.rsa.GameBuffer;
import com.model.net.network.rsa.ISAACRandomGen;
import com.model.net.network.session.GameSession;
import com.model.task.ScheduledTask;
import com.model.task.impl.DistancedActionTask;
import com.model.utility.Stopwatch;
import com.model.utility.Utility;

public class Player extends Entity {
	
	private Duel duelSession = new Duel(this);
	
	public Duel getDuel() {
		return duelSession;
	}
	
	/**
	 * The players game mode, can either be PKER or TRAINED.
	 */
    private String gameMode = "PKER";
	
    /**
     * Gets the players game mode.
     * 
     * @return
     */
	public String getGameMode() {
		return gameMode;
	}
	
	/**
	 * Sets the players game mode.
	 * 
	 * @param gameMode
	 */
	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}
	
	/**
	 * The player is still in the tutorial
	 */
    private boolean tutorial = true;
	
    /**
	 * Gets if the player is in the tutorial
	 * 
	 * @return
	 */
	public boolean inTutorial() {
		return tutorial;
	}
	
	/**
	 * Sets if the player is in the tutorial
	 * 
	 * @param tutorial
	 */
	public void setTutorial(boolean tutorial) { 
		this.tutorial = tutorial;
	}
	
	/**
	 * The player has received a starter pack
	 */
	private boolean receivedStarter;
	
	/**
	 * Gets if the player has received a starter kit
	 * 
	 * @return
	 */
	public boolean receivedStarter() {
		return receivedStarter;
	}

	/**
	 * Sets if the player has received a starter kit
	 * 
	 * @param received
	 */
	public void setReceivedStarter(boolean received) {
		this.receivedStarter = received;
	}
	
	private boolean maxCape;
	
	public boolean hasClaimedMax() {
		return maxCape;
	}
	
	public void setClaimedMaxCape(boolean maxCape) {
		this.maxCape = maxCape;
	}
	
	public boolean usingBow, usingRangeWeapon, usingMagic, castingMagic, magicFailed;
	
	private boolean usingMelee;
	
	public boolean isUsingMelee() {
		return usingMelee;
	}
	
	public void usingMelee(boolean usingMelee) {
		this.usingMelee = usingMelee;
	}
	
	/**
	 * Are we using special attack
	 */
	private boolean usingSpecial;
	
	public boolean isUsingSpecial() {
		return usingSpecial;
	}
	
	public void setUsingSpecial(boolean usingSpecial) {
		this.usingSpecial = usingSpecial;
	}
	
	/**
	 * Represents the amount donated
	 */
	private int amountDonated;
	
	public int getAmountDonated() {
		return amountDonated;
	}
	
	public void setAmountDonated(int amountDonated) {
		this.amountDonated = amountDonated;
	}
	
	/**
	 * Represents the total amount donated
	 */
	private int totalAmountDonated;
	
	public int getTotalAmountDonated() {
		return totalAmountDonated;
	}
	
	public void setTotalAmountDonated(int totalAmountDonated) {
		this.totalAmountDonated = totalAmountDonated;
	}
	
	/**
	 * Represents the players total death amount, (inside the wilderness)
	 */
	private int deathCount;
	
	public int getDeathCount() {
		return deathCount;
	}
	
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}
	
	/**
	 * Represents the players total kill amount, (inside the wilderness)
	 */
	private int killCount;
	
	public int getKillCount() {
		return killCount;
	}
	
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}
	
	/**
	 * Represents the players current killstreak, (inside the wilderness)
	 */
	private int currentKillStreak;
	
	public int getCurrentKillStreak() {
		return currentKillStreak;
	}
	
	public void setCurrentKillStreak(int currentKillStreak) {
		this.currentKillStreak = currentKillStreak;
	}
	
	/**
	 * Represents the players highest killstreak, (inside the wilderness)
	 */
	private int highestKillStreak;
	
	public int getHighestKillStreak() {
		return highestKillStreak;
	}
	
	public void setHighestKillStreak(int highestKillStreak) {
		this.highestKillStreak = highestKillStreak;
	}
	
	/**
	 * Represents the players current wilderness killstreak
	 */
	private int wildernessKillStreak;
	
	public int getWildernessKillStreak() {
		return wildernessKillStreak;
	}
	
	public void setWildernessKillStreak(int wildernessKillStreak) {
		this.wildernessKillStreak = wildernessKillStreak;
	}
	
	
	
	/**
	 * Representing the amount of completed slayer tasks
	 */
	private int slayerTasksCompleted;
	
	public int getSlayerTasksCompleted() {
		return slayerTasksCompleted;
	}
	
	public void setSlayerTasksCompleted(int slayerTasksCompleted) {
		this.slayerTasksCompleted = slayerTasksCompleted;
	}
	
	/**
	 * A reward for doing slayer tasks
	 */
	private int slayerPoints;
	
	public int getSlayerPoints() {
		return slayerPoints;
	}
	
	public void setSlayerPoints(int slayerPoints) {
		this.slayerPoints = slayerPoints;
	}
	
	/**
	 * A reward for killing players
	 */
	private int pkPoints;
	
	public int getPkPoints() {
		return pkPoints;
	}
	
	public void setPkPoints(int pkPoints) {
		this.pkPoints = pkPoints;
	}
	
	/**
	 * Vote reward currency
	 */
	private int votePoints;
	
	public int getVotePoints() {
		return votePoints;
	}
	
	public void setVotePoints(int votePoints) {
		this.votePoints = votePoints;
	}
	
	/**
	 * Total times voted
	 */
	private int totalVotes;
	
	public int getTotalVotes() {
		return totalVotes;
	}
	
	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}
	
	/**
	 * Gear points are given every 5 minutes.
	 */
	private int gearPoints;
	
	public int getGearPoints() {
		return gearPoints;
	}
	
	public void setGearPoints(int gearPoints) {
		this.gearPoints = gearPoints;
	}
	
	/**
	 * The players current prayer points
	 */
	private double prayerPoint = 1.0;

	/**
	 * The players current active prayers
	 */
	private boolean[] activePrayer = new boolean[26];

	/**
	 * The players current prayer icon
	 */
	private int prayerIcon = -1;

	/**
	 * The prayer drain rate
	 */
	private double prayerDrainRate;
	
	/**
	 * Gets the active prayer
	 * 
	 * @param prayer
	 * @return
	 */
	public boolean isActivePrayer(Prayer prayer) {
		int index = prayer.getPrayerIndex(prayer);
		return activePrayer[index];
	}

	/**
	 * Returns the players active prayers
	 * 
	 * @return
	 */
	public boolean[] getPrayers() {
		return activePrayer;
	}

	/**
	 * Sets the active prayer
	 * 
	 * @param prayer
	 * @param active
	 * @return
	 */
	public Player setActivePrayer(Prayer prayer, boolean active) {
		int index = prayer.getPrayerIndex(prayer);
		this.activePrayer[index] = active;
		return this;
	}

	/**
	 * Gets the player icon
	 * 
	 * @return
	 */
	public int getPrayerIcon() {
		return prayerIcon;
	}

	/**
	 * Sets the prayer icon
	 * 
	 * @param icon
	 * @return
	 */
	public Player setPrayerIcon(int icon) {
		this.prayerIcon = icon;
		return this;
	}

	/**
	 * Gets the player drain rate
	 * 
	 * @return
	 */
	public double getPrayerDrainRate() {
		return prayerDrainRate;
	}

	/**
	 * Adds to the player drain rate
	 * 
	 * @param rate
	 * @return
	 */
	public Player addPrayerDrainRate(double rate) {
		this.prayerDrainRate += rate;
		return this;
	}

	/**
	 * Gets the players prayer points
	 * 
	 * @return
	 */
	public double getPrayerPoint() {
		return prayerPoint;
	}

	/**
	 * Sets the players prayer points
	 * 
	 * @param prayerPoint
	 */
	public void setPrayerPoint(double prayerPoint) {
		this.prayerPoint = prayerPoint;
	}
	
	/**
	 * Gets the attack style config
	 * 
	 * @return
	 */
	public int getAttackStyleConfig() {
		return attackStyleConfig;
	}

	/**
	 * Sets the attack style config
	 * 
	 * @param config
	 */
	public void setAttackStyleConfig(int config) {
		this.attackStyleConfig = config;
	}
	
	/**
	 * The attack style config
	 */
	private int attackStyleConfig;
	
	/**
	 * The attack style id.
	 */
	public int attackStyle;
	
	public int getAttackStyle() {
		return attackStyle;
	}

	public void setAttackStyle(int attackStyle) {
		this.attackStyle = attackStyle;
	}
	
	/**
	 * The playeers total special amount
	 */
	private int specialAmount = 100;
	
	/**
	 * Returns the players amount of special
	 * 
	 * @return
	 */
	public int getSpecialAmount() {
		return specialAmount;
	}

	/**
	 * Sets the players amount of special
	 * 
	 * @param amount
	 */
	public void setSpecialAmount(int amount) {
		this.specialAmount = amount;
	}
	
	/**
	 * Sets the players spell id
	 * 
	 * @param id
	 *            The id of the spell
	 */
	public void setSpellId(int id) {
		this.spellId = id;
	}

	/**
	 * Returns the players spell id
	 * 
	 * @return
	 */
	public int getSpellId() {
		return spellId;
	}
	
	/**
	 * The players spell Id
	 */
	public int spellId = -1;

	/**
	 * The players autocast Id
	 */
	public int autocastId = -1;
	
	/**
	 * The player is auto casting
	 */
	public boolean autoCast = false;
	public boolean onAuto = false;

	/**
	 * Constructs a new {@link LootingBag}.
	 */
	private final LootingBagContainer lootingbagContainer = new LootingBagContainer(this);
	
	/**
	 * @see {@link #lootingbagContainer}.
	 * <b>no point in documentating a getter</b>
	 */
	public LootingBagContainer getLootingBagContainer() {
		return lootingbagContainer;
	}
	
	/**
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public final RunePouchContainer runePouchContainer = new RunePouchContainer(this);
	
	/**
	 * @see {@link #runePouchContainer}.
	 * <b>no point in documentating a getter</b>
	 */
	public RunePouchContainer getRunePouchContainer() {
		return runePouchContainer;
	}
	
	private PetCombat petCombat = new PetCombat(this);
	
	public PetCombat getPetCombat() {
		return petCombat;
	}
	
	/**
	 * The player's spell book.
	 */
	public SpellBook spell = SpellBook.MODERN;
	
	public SpellBook getSpellBook() {
		return spell;
	}

	public void setSpellBook(SpellBook spell) {
		this.spell = spell;
	}
	
	private String yellColor = "ff0000";
	
	public String getYellColor() {
		return yellColor;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}
	
	public boolean in_debug_mode() {
		return debugMode;
	}
	
	public void set_debug_mode(boolean a) {
		this.debugMode = a;
	}
	
	/**
	 * The player is in debug mode
	 */
	private boolean debugMode;

	/**
	 * The shop that you currently have open.
	 */
	private String openShop = "";
	
	/**
	 * The players death store
	 */
	public Shop deathShop = new Shop("Death Store", new Item[0], false, false, Currency.COINS);
	
	/**
	 * Does the player have the death store enabled
	 */
	public boolean deathShopEnabled = true;
	
	/**
	 * Using the death shop chat
	 */
	public boolean deathShopChat;

	
	/**
	 * Reload ground items.
	 * @param player
	 */
	public void reloadItems(Player player) {
		Server.getTaskScheduler().schedule(new ScheduledTask(4) {
			@Override
			public void execute() {
				GroundItemHandler.reloadGroundItems(player);
				this.stop();
			}
		});
	}

	/**
	 * Writes an encoded packet to the client
	 *
	 * @param encoder
	 *            the {@link PacketEncoder} to write to the client
	 */
	public void write(PacketEncoder encoder) {
		if (getOutStream() != null) {
			encoder.encode(this);
		}
	}
	
	public boolean attackable;
	public boolean inTask = false;

	/**
	 * The player is unattackable
	 */
	private boolean unattackable = false;

	/**
	 * The player is invincible
	 */
	private boolean invincible;

	private long xlogDelay;
	
	public int getId() {
		return getIndex();
	}

	@Override
	public Location getLocation() {
		return new Location(absX, absY, heightLevel);
	}

	public long lastBankDeposit;

	public GameBuffer inStream = null, outStream = null;
	private GameSession session;

	public GameSession getSession() {
		return session;
	}

	public void setBountyPoints(int points) {
		this.bountyPoints = points;
	}

	public int getBountyPoints() {
		return bountyPoints;
	}
	
	private int recoil = 40;
	
	public int getRecoil() {
		return recoil;
	}

	public void setRecoil(int recoil) {
		this.recoil = recoil;
	}
	
    private int suffering = 0;
	
	public int getROSuffering() {
		return suffering;
	}

	public void setROSuffering(int suffering) {
		this.suffering = suffering;
	}

	public long teleblockLength;

	public boolean WithinDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public int[][] MAGIC_SPELLS = { { 1152, 1, 711, 90, 91, 92, 2, 5, 556, 1, 558, 1, 0, 0, 0, 0, 993 }, // wind
																												// strike
			{ 1154, 5, 711, 93, 94, 95, 4, 7, 555, 1, 556, 1, 558, 1, 0, 0, 211 }, // water
																					// strike
			{ 1156, 9, 711, 96, 97, 98, 6, 9, 557, 2, 556, 1, 558, 1, 0, 0, 0 }, // earth
																					// strike
			{ 1158, 13, 711, 99, 100, 101, 8, 11, 554, 3, 556, 2, 558, 1, 0, 0, 0 }, // fire
																						// strike
			{ 1160, 17, 711, 117, 118, 119, 9, 13, 556, 2, 562, 1, 0, 0, 0, 0, 0 }, // wind
																					// bolt
			{ 1163, 23, 711, 120, 121, 122, 10, 16, 556, 2, 555, 2, 562, 1, 0, 0, 0 }, // water
																						// bolt
			{ 1166, 29, 711, 123, 124, 125, 11, 20, 556, 2, 557, 3, 562, 1, 0, 0, 0 }, // earth
																						// bolt
			{ 1169, 35, 711, 126, 127, 128, 12, 22, 556, 3, 554, 4, 562, 1, 0, 0, 0 }, // fire
																						// bolt
			{ 1172, 41, 711, 132, 133, 134, 13, 25, 556, 3, 560, 1, 0, 0, 0, 0, 0 }, // wind
																						// blast
			{ 1175, 47, 711, 135, 136, 137, 14, 28, 556, 3, 555, 3, 560, 1, 0, 0, 0 }, // water
																						// blast
			{ 1177, 53, 711, 138, 139, 140, 15, 31, 556, 3, 557, 4, 560, 1, 0, 0, 0 }, // earth
																						// blast
			{ 1181, 59, 711, 129, 130, 131, 16, 35, 556, 4, 554, 5, 560, 1, 0, 0, 0 }, // fire
																						// blast
			{ 1183, 62, 727, 158, 159, 160, 17, 36, 556, 5, 565, 1, 0, 0, 0, 0, 0 }, // wind
																						// wave
			{ 1185, 65, 727, 161, 162, 163, 18, 37, 556, 5, 555, 7, 565, 1, 0, 0, 0 }, // water
																						// wave
			{ 1188, 70, 727, 164, 165, 166, 19, 40, 556, 5, 557, 7, 565, 1, 0, 0, 0 }, // earth
																						// wave
			{ 1189, 75, 727, 155, 156, 157, 20, 42, 556, 5, 554, 7, 565, 1, 0, 0, 0 }, // fire
																						// wave

			{ 1153, 3, 716, 102, 103, 104, 0, 13, 555, 3, 557, 2, 559, 1, 0, 0, 0 }, // confuse
			{ 1157, 11, 716, 105, 106, 107, 0, 20, 555, 3, 557, 2, 559, 1, 0, 0, 0 }, // weaken
			{ 1161, 19, 716, 108, 109, 110, 0, 29, 555, 2, 557, 3, 559, 1, 0, 0, 0 }, // curse
			{ 1542, 66, 729, 167, 168, 169, 0, 76, 557, 5, 555, 5, 566, 1, 0, 0, 0 }, // vulnerability
			{ 1543, 73, 729, 170, 171, 172, 0, 83, 557, 8, 555, 8, 566, 1, 0, 0, 0 }, // enfeeble
			{ 1562, 80, 729, 173, 174, 107, 0, 90, 557, 12, 555, 12, 556, 1, 0, 0, 0 }, // stun

			{ 1572, 20, 710, 177, 178, 181, 0, 30, 557, 3, 555, 3, 561, 2, 0, 0, 0 }, // bind
			{ 1582, 50, 710, 177, 178, 180, 2, 60, 557, 4, 555, 4, 561, 3, 0, 0, 0 }, // snare
			{ 1592, 79, 710, 177, 178, 179, 4, 90, 557, 5, 555, 5, 561, 4, 0, 0, 0 }, // entangle

			{ 1171, 39, 724, 145, 146, 147, 15, 25, 556, 2, 557, 2, 562, 1, 0, 0, 0 }, // crumble
																						// undead
			{ 1539, 50, 708, 87, 88, 89, 25, 42, 554, 5, 560, 1, 0, 0, 0, 0, 0 }, // iban
																					// blast
			{ 12037, 50, 1576, 327, 328, 329, 19, 30, 560, 1, 558, 4, 0, 0, 0, 0, 0 }, // magic
																						// dart

			{ 1190, 60, 811, 0, 0, 76, 20, 60, 554, 2, 565, 2, 556, 4, 0, 0, 0 }, // sara
																					// strike
			{ 1191, 60, 811, 0, 0, 77, 20, 60, 554, 1, 565, 2, 556, 4, 0, 0, 0 }, // cause
																					// of
																					// guthix
			{ 1192, 60, 811, 0, 0, 78, 20, 60, 554, 4, 565, 2, 556, 1, 0, 0, 0 }, // flames
																					// of
																					// zammy

			{ 12445, 85, 1819, 0, 344, 345, 0, 65, 563, 1, 562, 1, 560, 1, 0, 0, 0 }, // teleblock

			// Ancient Spells
			{ 12939, 50, 1978, 0, 384, 385, 13, 30, 560, 2, 562, 2, 554, 1, 556, 1, 0 }, // smoke
																							// rush
			{ 12987, 52, 1978, 0, 378, 379, 14, 31, 560, 2, 562, 2, 566, 1, 556, 1, 0 }, // shadow
																							// rush
			{ 12901, 56, 1978, 0, 0, 373, 15, 33, 560, 2, 562, 2, 565, 1, 0, 0, 0 }, // blood
																						// rush
			{ 12861, 58, 1978, 0, 360, 361, 16, 34, 560, 2, 562, 2, 555, 2, 0, 0, 0 }, // ice
																						// rush
			{ 12963, 62, 1979, 0, 0, 389, 19, 36, 560, 2, 562, 4, 556, 2, 554, 2, 0 }, // smoke
																						// burst
			{ 13011, 64, 1979, 0, 0, 382, 20, 37, 560, 2, 562, 4, 556, 2, 566, 2, 0 }, // shadow
																						// burst
			{ 12919, 68, 1979, 0, 0, 376, 21, 39, 560, 2, 562, 4, 565, 2, 0, 0, 0 }, // blood
																						// burst
			{ 12265, 70, 1979, 0, 0, 363, 22, 40, 560, 2, 562, 4, 555, 4, 0, 0, 0 }, // ice
																						// burst
			{ 12951, 74, 1978, 0, 386, 387, 23, 42, 560, 2, 554, 2, 565, 2, 556, 2, 0 }, // smoke
																							// blitz
			{ 12999, 76, 1978, 0, 380, 381, 24, 43, 560, 2, 565, 2, 556, 2, 566, 2, 0 }, // shadow
																							// blitz
			{ 12911, 80, 1978, 0, 374, 375, 25, 45, 560, 2, 565, 4, 0, 0, 0, 0, 0 }, // blood
																						// blitz
			{ 12871, 82, 1978, 366, 0, 367, 26, 46, 560, 2, 565, 2, 555, 3, 0, 0, 0 }, // ice
																						// blitz
			{ 12975, 86, 1979, 0, 0, 391, 27, 48, 560, 4, 565, 2, 556, 4, 554, 4, 0 }, // smoke
																						// barrage
			{ 13023, 88, 1979, 0, 0, 383, 28, 49, 560, 4, 565, 2, 556, 4, 566, 3, 0 }, // shadow
																						// barrage
			{ 12929, 92, 1979, 0, 0, 377, 29, 51, 560, 4, 565, 4, 566, 1, 0, 0, 0 }, // blood
																						// barrage
			{ 12891, 94, 1979, 0, 0, 369, 30, 52, 560, 4, 565, 2, 555, 6, 0, 0, 0 }, // ice
																						// barrage

			{ -1, 80, 811, 301, 0, 0, 0, 0, 554, 3, 565, 3, 556, 3, 0, 0, 0 }, // charge
			{ -1, 21, 712, 112, 0, 0, 0, 10, 554, 3, 561, 1, 0, 0, 0, 0, 0 }, // low
																				// alch
			{ -1, 55, 713, 113, 0, 0, 0, 20, 554, 5, 561, 1, 0, 0, 0, 0, 0 }, // high
																				// alch
			{ -1, 33, 728, 142, 143, 144, 0, 35, 556, 1, 563, 1, 0, 0, 0, 0, 0 }, // telegrab
			{ -1, 75, 1167, 1251, 1252, 1253, 29, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, // trident
																				// of
																				// the
																				// seas
			{ -1, 75, 1167, 665, 1040, 1042, 32, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, // trident
																				// of
																				// the
																				// swamp
			{1337, 80, 2078, 145, 146, 147, 40, 1, 0, 0, 0, 0, 0, 0, 0, 0} //polypore 54
			// example {magicId, level req, animation, startGFX, projectile Id, endGFX, maxhit, exp gained, rune 1, rune 1 amount, rune 2, rune 2 amount, rune 3, rune 3 amount, rune 4, rune 4 amount}
			

			// blast
			// water
			// blast

	};
	
	public boolean isAutoButton(int button) {
		for (int j = 0; j < autocastIds.length; j += 2) {
			if (autocastIds[j] == button) {
				return true;
			}
		}
		return false;
	}

	public int[] autocastIds = { 51133, 32, 51185, 33, 51091, 34, 24018, 35, 51159, 36, 51211, 37, 51111, 38, 51069, 39,
			51146, 40, 51198, 41, 51102, 42, 51058, 43, 51172, 44, 51224, 45, 51122, 46, 51080, 47, 7038, 0, 7039, 1,
			7040, 2, 7041, 3, 7042, 4, 7043, 5, 7044, 6, 7045, 7, 7046, 8, 7047, 9, 7048, 10, 7049, 11, 7050, 12, 7051,
			13, 7052, 14, 7053, 15, 47019, 27, 47020, 25, 47021, 12, 47022, 13, 47023, 14, 47024, 15 };

	public void assignAutocast(int button) {
		for (int j = 0; j < autocastIds.length; j++) {
			if (autocastIds[j] == button) {
				Player c = World.getWorld().getPlayers().get(this.getIndex());
				autoCast = true;
				autocastId = autocastIds[j + 1];
				c.write(new SendConfig(108, 1));
				c.write(new SendSidebarInterface(0, 328));
				break;
			}
		}
	}
	
	public int[] playerEquipment() {
		return playerEquipment;
	}
	
	private Equipment equipment = new Equipment();
	
	/**
	 * Gets the player's equipment.
	 * 
	 * @return The player's equipment.
	 */
	public Equipment getEquipment() {
		return equipment;
	}
	
	public void resetWalkingQueue() {
		getMovementHandler().reset();
	}

	public Set<Player> localPlayers = new LinkedHashSet<>(255);
	public Set<Npc> localNpcs = new LinkedHashSet<>(255);

	public boolean withinDistance(Player otherPlr) {
		if (heightLevel != otherPlr.heightLevel) {
			return false;
		}
		int deltaX = otherPlr.getX() - getX(), deltaY = otherPlr.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(Npc npc) {
		if (heightLevel != npc.heightLevel) {
			return false;
		}
		if (!npc.isVisible()) {
			return false;
		}
		int deltaX = npc.getX() - getX(), deltaY = npc.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean destinationReached() {
		Location player = new Location(absX, absY, heightLevel);
		Location object = new Location(objectX, objectY, heightLevel);
		if (player.equals(object) || player.withinDistance(object, objectDistance)) {
			return true;
		} else {
			return false;
		}
	}

	public void faceUpdate(int index) {
		face = index;
		setFaceUpdateRequired(true);
		updateRequired = true;
	}

	public void turnPlayerTo(int pointX, int pointY) {
		FocusPointX = 2 * pointX + 1;
		FocusPointY = 2 * pointY + 1;
		updateRequired = true;
	}

	private void hasDied() {
		Server.getTaskScheduler().schedule(new ScheduledTask(1, true) {

			@Override
			public void execute() {
				if (!isRegistered() || !isDead()) {
					stop();
					return;
				}
				switch (countdown) {
				case 0:
					setDead(true);
					resetWalkingQueue();
					break;
				case 1:
					playAnimation(Animation.create(0x900));
					poisonDamage = -1;
					infection = 0;
					infected = false;
					break;
				case 4:
					getPlayerDeath().characterDeath();
					break;
				case 5:
					getPlayerDeath().giveLife();
					setDead(false);
					stop();
					break;
				}
				countdown++;
			}

			@Override
			public void onStop() {
				setDead(false);
				countdown = 0;
			}
		}.attach(this));
	}

	private GameBuffer updateBlock = null;
	
	public void setForceMovement(int xx1, int yy1, int xx2, int yy2, int speedd1, int speedd2, int directionn) {
        this.x1 = xx1;
        this.y1 = yy1;
        this.x2 = xx2;
        this.y2 = yy2;
        this.speed1 = speedd1;
        this.speed2 = speedd2;
        this.direction = directionn;
        this.forceMovementUpdateRequired = true;
        this.updateRequired = true;
    }

	public void appendMask400Update(GameBuffer str) {
		str.writeByteS(x1);
		str.writeByteS(y1);
		str.writeByteS(x2);
		str.writeByteS(y2);
		str.writeWordBigEndianA(speed1);
		str.writeWordA(speed2);
		str.writeByteS(direction);
	}

	public void clearUpdateFlags() {
		forceMovementUpdateRequired = false;
		updateRequired = false;
		chatTextUpdateRequired = false;
		appearanceUpdateRequired = false;
		setHitUpdateRequired(false);
		hitUpdateRequired2 = false;
		forcedChatUpdateRequired = false;
		this.gfxUpdateRequired = false;
		this.animUpdateRequired = false;
		FocusPointX = -1;
		FocusPointY = -1;
		setFaceUpdateRequired(false);
		face = 65535;
		setUpdateBlock(null);
		super.clear();
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}
	

	public void stopMovement() {
		getMovementHandler().reset();
	}

	public MovementHandler getMovementHandler() {
		return movementHandler;
	}

	public int getMapRegionX() {
		return mapRegionX;
	}

	public int getMapRegionY() {
		return mapRegionY;
	}

	public int getZ() {
		return heightLevel;
	}

	public void setHitUpdateRequired(boolean hitUpdateRequired) {
		this.hitUpdateRequired = hitUpdateRequired;
	}

	public void setHitUpdateRequired2(boolean hitUpdateRequired2) {
		this.hitUpdateRequired2 = hitUpdateRequired2;
	}

	public boolean isHitUpdateRequired() {
		return hitUpdateRequired;
	}

	public boolean getHitUpdateRequired() {
		return hitUpdateRequired;
	}

	public boolean getHitUpdateRequired2() {
		return hitUpdateRequired2;
	}

	public void setChatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
	}

	public int getChatTextEffects() {
		return chatTextEffects;
	}

	public void setChatTextSize(byte chatTextSize) {
		this.chatTextSize = chatTextSize;
	}

	public byte getChatTextSize() {
		return chatTextSize;
	}

	public boolean isChatTextUpdateRequired() {
		return chatTextUpdateRequired;
	}

	public void setChatText(byte chatText[]) {
		this.chatText = chatText;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public void setChatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
	}

	public int getChatTextColor() {
		return chatTextColor;
	}

	public void setInStreamDecryption(ISAACRandomGen inStreamDecryption) {
	}

	public void setOutStreamDecryption(ISAACRandomGen outStreamDecryption) {
	}

	public void putInCombat(int attacker) {
		underAttackBy = attacker;
		logoutDelay.reset();
		singleCombatDelay.reset();
		updateLastCombatAction();
		setInCombat(true);
	}

	private int lastRegionHeight;

	public int setLastRegionHeight(int height) {
		return this.lastRegionHeight = height;
	}

	public int getLastRegionHeight() {
		return this.lastRegionHeight;
	}

	public String getIdentity() {
		return identity;
	}

	public String setIdentity(String identity) {
		return this.identity = identity;
	}

	public void mapData(boolean s) {
		outStream.writeFrame(99);
		outStream.writeByte(s ? 2 : 0);
	}

	public Player(String username) {
		super(EntityType.PLAYER);
		this.username = username;
		usernameHash = Utility.playerNameToInt64(username);
		for (int i = 0; i < playerItems.length; i++) {
			playerItems[i] = 0;
			playerItemsN[i] = 0;
		}
		for (int i = 0; i < BANK_SIZE; i++) {
			bankItems[i] = 0;
			bankItemsN[i] = 0;
		}
		playerAppearance[0] = 0; // gender
		playerAppearance[1] = 0; // head
		playerAppearance[2] = 18;// Torso
		playerAppearance[3] = 26; // arms
		playerAppearance[4] = 33; // hands
		playerAppearance[5] = 36; // legs
		playerAppearance[6] = 42; // feet
		playerAppearance[7] = 10; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 0; // torso colour
		playerAppearance[10] = 0; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0; // skin colour
		dialogue = new DialogueManager(this);
		playerEquipment[getEquipment().getHelmetId()] = -1;
		playerEquipment[getEquipment().getCapeId()] = -1;
		playerEquipment[getEquipment().getAmuletId()] = -1;
		playerEquipment[getEquipment().getChestId()] = -1;
		playerEquipment[getEquipment().getShieldId()] = -1;
		playerEquipment[getEquipment().getLegsId()] = -1;
		playerEquipment[getEquipment().getGlovesId()] = -1;
		playerEquipment[getEquipment().getBootsId()] = -1;
		playerEquipment[getEquipment().getRingId()] = -1;
		playerEquipment[getEquipment().getQuiverId()] = -1;
		playerEquipment[getEquipment().getWeaponId()] = -1;
		heightLevel = 0;
		teleportToX = 3087;
		teleportToY = 3499;
		absX = absY = -1;
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		resetWalkingQueue();
		outStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		outStream.offset = 0;
		inStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		inStream.offset = 0;
	}

	@Override
	public Hit decrementHP(Hit hit) {
		int damage = hit.getDamage();
		if (damage > this.getSkills().getLevel(3))
			damage = this.getSkills().getLevel(3);

		if (!isInvincible()) {
			this.getSkills().setLevel(3, getSkills().getLevel(3) - damage);
			//System.out.println("Health "+getSkills().getLevel(Skills.HITPOINTS)+ " damage taken: "+damage);
		}
		int difference = getSkills().getLevel(Skills.HITPOINTS) - damage;
		if (difference <= this.getSkills().getLevelForExperience(Skills.HITPOINTS) / 10 && difference > 0) {
			getPrayerHandler().appendRedemption(this);
		}

		/*
		 * Check if our player has died, if so start the death task
		 * 
		 */
		if (getSkills().getLevel(3) < 1 && !isDead()) {
			setDead(true);
			hasDied();
		}
		return new Hit(damage, hit.getType());
	}

	public void flushOutStream() {
		if (outStream == null || getSession() == null || !getSession().getChannel().isOpen()
				|| (outStream.offset == 0)) {
			return;
		}
		byte[] temp = new byte[outStream.offset];
		System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
		getSession().getChannel().writeAndFlush(new Packet(-1, Unpooled.wrappedBuffer(temp)));
		outStream.offset = 0;
	}

	public void sendClan(String name, String message, String clan) {
		outStream.putFrameVarShort(217);
		int offset = getOutStream().offset;
		outStream.putRS2String(name);
		outStream.putRS2String(message);
		outStream.putRS2String(clan);
		outStream.writeShort(0);
		outStream.putFrameSizeShort(offset);
	}

	public void sendClan(String name, String message, String clan, int rights) {
		outStream.putFrameVarShort(217);
		int offset = getOutStream().offset;
		outStream.putRS2String(name);
		outStream.putRS2String(message);
		outStream.putRS2String(clan);
		outStream.writeShort(rights);
		outStream.putFrameSizeShort(offset);
	}

	public static final int PACKET_SIZES[] = { 
		    0, 0, 0, 1, -1, 0, 4, 0, 4, 0, // 9
			0, 0, 0, 0, 4, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 16, 0, -1, -1, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			6, 10, -1, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, /* 0 */4, 7, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};
	
	public static void main(String[] s){
		System.out.println("pkt 140 is "+PACKET_SIZES[140]+ " pkt 141 is "+PACKET_SIZES[141]+ " pkt 142 is "+PACKET_SIZES[142]);
	}

	public boolean teleportRequired = false;

	public void teleportRequired(Player player) {
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {
			@Override
			public void execute() {
				if (teleportRequired) {
					player.getPA().movePlayer(3087, 3500, 0);
					this.stop();
				}
				if (!teleportRequired)
					this.stop();
			}

			@Override
			public void onStop() {
				teleportRequired = false;

			}
		}.attach(this));
	}

	/**
	 * Checks if a player is in a specific location
	 *
	 * @param x
	 *            The x location
	 * @param y
	 *            The y location
	 * @param z
	 *            The z location
	 * @return If the player is standing on this spot
	 */
	public boolean onSpot(int x, int y, int z) {
		return this.absX == x && this.absY == y && this.heightLevel == z;
	}

	private void loadInterfaces() {
		int[] interfaces = { 2423, 3917, 29400, 3213, 1644, 5608, -1, 18128, 5065, 5715, 2449, 36000, 147, -1, -1 };//15
		for (int i = 0; i < 15; i++) {
			this.write(new SendSidebarInterface(i, interfaces[i]));
		}
		
		if (this.getSpellBook() == SpellBook.ANCIENT) {
			this.write(new SendSidebarInterface(6, 12855));
		} else if (this.getSpellBook() == SpellBook.MODERN) {
			this.write(new SendSidebarInterface(6, 1151));
		} else if (this.getSpellBook() == SpellBook.LUNAR) {
			this.write(new SendSidebarInterface(6, 29999));
		}
		
	}
	
	public boolean ecoReset = true;
	
	public void initialize() {
		outStream.writeFrame(249);
		outStream.putByteA(0);
		outStream.writeWordBigEndianA(getIndex());
		flushOutStream();

		combatLevel = getSkills().getCombatLevel();
		totalLevel = getSkills().getTotalLevel();
		this.getPA().serverReset();
		infection = 0;
		poisonDamage = 0;
		infected = false;
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			this.write(new SendSkillPacket(i));
		}
		PrayerHandler.resetAllPrayers(this);

		if (teleportRequired) {
			teleportRequired(this);
		}
		if (!receivedStarter() && inTutorial()) {
			this.dialogue().start("STARTER");
			PlayerUpdating.executeGlobalMessage("<col=255>" + Utility.capitalize(getName()) + "</col> Has joined Luzoxpk for the first time.");
		}
		getFAI().handleLogin();
		loadInterfaces();
		this.setLastRegionHeight(this.getZ());
		write(new SendString("100%", 149));
		write(new SendInteractionOption("Follow", 4, true));
		write(new SendInteractionOption("Trade With", 5, true));
		getItems().resetItems(3214);
		getWeaponInterface().sendWeapon(playerEquipment[this.getEquipment().getWeaponId()], this.getItems().getItemName(playerEquipment[this.getEquipment().getWeaponId()]));
		getItems().setEquipment(playerEquipment[getEquipment().getWeaponId()],playerEquipmentN[getEquipment().getWeaponId()], getEquipment().getWeaponId());
		CombatAnimation.itemAnimations(this);
		getItems().resetBonus();
		getItems().getBonus();
		getItems().writeBonus();

		for (int equip = 0; equip < playerEquipment.length; equip++) {
			getItems().setEquipment(playerEquipment[equip], playerEquipmentN[equip], equip);
		}

		getWeaponInterface().sendSpecialBar(playerEquipment[this.getEquipment().getWeaponId()]);
		canChangeAppearance = true;

		submitAfterLogin();
		correctPlayerCoordinatesOnLogin();
		setActive(true);
		refreshSettings(this);
		Utility.println("[REGISTERED]: " + this + "");
		setAttribute("login_delay", System.currentTimeMillis());
		QuestTabPageHandler.write(this, QuestTabPages.HOME_PAGE);
	}

	public void refreshSettings(Player player) {
		AttackStyle.adjustAttackStyleOnLogin(player);
		this.setScreenBrightness((byte) 4);
		player.write(new SendString("100%", 149));
		player.write(new SendConfig(166, getScreenBrightness()));
		player.write(new SendConfig(207, isEnableMusic() ? 1 : 0));
		player.write(new SendConfig(206, isEnableSound() ? 1 : 0));
		player.write(new SendConfig(287, getSplitPrivateChat() ? 1 : 0));
		player.write(new SendConfig(205, getSplitPrivateChat() ? 1 : 0));
		player.write(new SendConfig(200, getAcceptAid() ? 1 : 0));
		player.write(new SendConfig(172, isAutoRetaliating() ? 1 : 0));
		player.write(new SendConfig(152, isRunning() ? 1 : 0));
	}

	private void submitAfterLogin() {
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {

			@Override
			public void execute() {
				Player player = (Player) getAttachment();
				if (player == null || !player.isActive()) {
					stop();
					return;
				}
				
				player.write(new SendMessagePacket("Welcome back to " + Constants.SERVER_NAME + "."));
				
				if (isMuted()) {
					player.write(new SendMessagePacket("You are currently muted. Other players will not see your chat messages."));
				}
				
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
				if (player.getBankPin().isLocked() && player.getBankPin().getPin().trim().length() > 0) {
					player.getBankPin().open(2);
					player.playerStun = true;
					player.aggressionTolerance.stop();
					player.isBanking = false;
				}
				if (player.petId > 0)
					player.getPets().spawnPet(player, 0, true);
				if (player.teleportRequired) {
					player.teleportRequired(player);
				}
				if (player.getName().equalsIgnoreCase("Patrick")) {
					player.set_debug_mode(true);
				}
				if (tempKey == null || tempKey.equals("") || tempKey.isEmpty()) {
					player.write(new SendMessagePacket("<col=ff0033>We noticed you aren't in a clanchat, so we added you to the community clanchat!"));
					tempKey = "patrick";
				}
				if (tempKey != null) {
					com.model.game.character.player.content.clan.ClanManager.joinClan(player, player.tempKey);
				}
				this.stop();
			}
		}.attach(this));

	}
	
	private boolean isMuted() {
		return this.isMuted;
	}

	private void correctPlayerCoordinatesOnLogin() {
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {

			@Override
			public void execute() {
				Player player = (Player) getAttachment();
				if (player == null || !player.isActive()) {
					stop();
					return;
				}
				/*if(player.getArea().inDuelArena()) {
					player.getPA().movePlayer(Constants.DUELING_RESPAWN_X, Constants.DUELING_RESPAWN_Y, 0);
				}*/
				ControllerManager.setControllerOnWalk(player);
				controller.onControllerInit(player);
				stop();
			}

		}.attach(this));
	}

	public void logout() {
		if (!controller.canLogOut(this)) {
			return;
		}
		this.redSkull = 0;
		this.infection = 0;
		this.infected = false;
		this.poisonDamage = 0;
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(this, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			duelSession.getOther(this).write(new SendMessagePacket("The challenger has left the duel."));
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (this.petId > 0)
			getPets().pickupPet(this, false, World.getWorld().getNpcs().get(this.petNpcIndex));
		if (logoutDelay.elapsed(10000) && getLastCombatAction().elapsed(600)) {
			if (this.attackable || this.inTask) {
				this.write(new SendMessagePacket("You're currently under an attack-timer..(Someone attempted to attack you)"));
				this.write(new SendMessagePacket("Please enter your pin to remove this block."));
				return;
			}
			outStream.writeFrame(109);
			flushOutStream();
			properLogout = true;
			World.getWorld().unregister(this);
		} else {
			write(new SendMessagePacket("You must wait 10 seconds before logging out."));
		}
	}

	@Override
	public void process() {
		try {
			refresh_inventory();
			PrayerHandler.handlePrayerDraining(this);
			//Server.getGlobalObjects().pulse();
			if (clickObjectType > 0 && destinationReached())
				handleObjectAction();
			process_following();
			combatProcessing();
			controller.tick(this);
			NPCAggression.process(this);
			if (!getArea().inWild()) {
				if (wildernessKillStreak >= 2) {
					write(new SendMessagePacket("[@red@Streak@bla@] Your wilderness ends at @red@" + wildernessKillStreak + "@bla@ as you exit the wilderness."));
					write(new SendMessagePacket("[@red@Streak@bla@] Wilderness killstreak bonus: @blu@+" + (wildernessKillStreak * 5) + "@bla@ PK Points."));
					pkPoints += (wildernessKillStreak * 5);
				}
				wildernessKillStreak = 0;
			}
			
			if (getPoisonDamage() > 0 && System.currentTimeMillis() - getLastPoisonHit() > TimeUnit.MINUTES.toMillis(1)) {
				appendPoisonDamage();
				setLastPoisonHit(System.currentTimeMillis());
				infection = 1;
				write(new SendMessagePacket("You have been poisoned!"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public GameBuffer getInStream() {
		return inStream;
	}

	public GameBuffer getOutStream() {
		return outStream;
	}

	public ItemAssistant getItems() {
		return itemAssistant;
	}

	public PlayerAssistant getPA() {
		return playerAssistant;
	}
	
	public DialogueHandler getDialogueHandler() {
		return dialogueHandler;
	}
	
	public DialogueHandler getPetChat() {
		return dialogueHandler;
	}

	public CombatAssistant getCombat() {
		return combatAssistant;
	}

	public ActionHandler getActions() {
		return actionHandler;
	}
	
	public void process_following() {
		if (followId > 0) {
			getPA().followPlayer(/* !getLastCombatAction().elapsed(5000) || inCombat() || */playerIndex > 0);
		} else if (followId2 > 0) {
			getPA().followNpc();
		}
	}

	public void combatProcessing() {
		try {
			if (singleCombatDelay.elapsed(6000)) {
				underAttackBy = 0;
				setInCombat(false);
			}
			if (singleCombatDelay2.elapsed(6000)) {
				underAttackBy2 = 0;
				resetDamageReceived();
			}
			if (skullTimer > 0) {
				skullTimer--;
				if (skullTimer == 1) {
					isSkulled = false;
					attackedPlayers.clear();
					skullIcon = -1;
					skullTimer = -1;
					redSkull = -1;
					getPA().requestUpdates();
				}
			}

			// freezing is a timer that belongs in the main process, we can however
			// add an entity process which can share between both npc/players
			//Lets start here btw, idk why PI has this in the processing
			//Shouldnt be rite?/
			//Alrite well the freezeTimer shit is pi i've added
			//isnt thi already npc and player process? it'll be in both but it can be in
			// entity only just to reduce duplicate code like same code in both classes plr/npc
			//then you mgith need to show me i don't fully understand that entity processing kk
			super.frozen_process();
			if (hitDelay > 0) {
				hitDelay--;
			}
			if (hitDelay == 1) {
				if (oldNpcIndex > 0) {
					getCombat().delayedHit(this, oldNpcIndex,
							new Item(this.playerEquipment[3], this.playerEquipmentN[3]));
				}
				if (oldPlayerIndex > 0) {
					getCombat().playerDelayedHit(this, oldPlayerIndex,
							new Item(this.playerEquipment[3], this.playerEquipmentN[3]));
				}
			}
			if (attackDelay > 0) {
				attackDelay--;
			}

			if (attackDelay == 1) {
				if (npcIndex > 0 && clickNpcType == 0) {
					getCombat().attackNpc(npcIndex);
					//System.out.println("Can attack?");
				}
				if (playerIndex > 0) {
					getCombat().attackPlayer(playerIndex);
				}
			} else if (attackDelay <= 0 && (npcIndex > 0 || playerIndex > 0)) {
				if (npcIndex > 0) {
					attackDelay = 0;
					getCombat().attackNpc(npcIndex);
					//System.out.println("Can attack.");
				} else if (playerIndex > 0) {
					attackDelay = 0;
					getCombat().attackPlayer(playerIndex);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateWalkEntities() {
		ControllerManager.setControllerOnWalk(this);
		ControllerManager.updateControllerOnWalk(this);
		
		if (hasMultiSign && !getArea().inMulti()) {
			hasMultiSign = false;
			this.write(new SendMultiWay(-1));
		} else if (!hasMultiSign && getArea().inMulti()) {
			hasMultiSign = true;
			this.write(new SendMultiWay(1));
		}
	}

	public void handleObjectAction() {
		walkingToObject = false;
		turnPlayerTo(objectX, objectY);
		if (clickObjectType == 1)
			getActions().firstClickObject(objectId, objectX, objectY);
		else if (clickObjectType == 2)
			getActions().secondClickObject(objectId, objectX, objectY);
		else if (clickObjectType == 3)
			getActions().thirdClickObject(objectId, objectX, objectY);
		else if (clickObjectType == 4)
			UseItem.ItemonObject(this, objectId, objectX, objectY, itemUsedOn);
	}

	private Map<Integer, TinterfaceText> interfaceText = new HashMap<Integer, TinterfaceText>();

	public class TinterfaceText {
		public int id;
		public String currentState;

		public TinterfaceText(String s, int id) {
			this.currentState = s;
			this.id = id;
		}

	}
	
	public boolean checkPacket126Update(String text, int id) {
		if (interfaceText.containsKey(id)) {
			TinterfaceText t = interfaceText.get(id);
			if (text.equals(t.currentState)) {
				return false;
			}
		}
		interfaceText.put(id, new TinterfaceText(text, id));
		return true;
	}

	public int getChunckX() {
		return (absX >> 6);
	}

	public int getChunckY() {
		return (absY >> 6);
	}

	public int getRegionId() {
		return ((getChunckX() << 8) + getChunckY());
	}

	private ItemAssistant itemAssistant = new ItemAssistant(this);
	private PlayerAssistant playerAssistant = new PlayerAssistant(this);
	private CombatAssistant combatAssistant = new CombatAssistant(this);
	private ActionHandler actionHandler = new ActionHandler(this);
	private DialogueHandler dialogueHandler = new DialogueHandler(this);
	private TradeContainer tradeContainer = new TradeContainer(this);
	
	private TradeState tradeState = TradeState.NONE;

	@Override
	public EntityType getEntityType() {
		return EntityType.PLAYER;
	}

	public GameBuffer getUpdateBlock() {
		return updateBlock;
	}

	public void setUpdateBlock(GameBuffer updateBlock) {
		this.updateBlock = updateBlock;
	}

	public boolean isFaceUpdateRequired() {
		return faceUpdateRequired;
	}

	public void setFaceUpdateRequired(boolean faceUpdateRequired) {
		this.faceUpdateRequired = faceUpdateRequired;
	}

	public void setSession(GameSession session) {
		this.session = session;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * The players active status
	 */
	private boolean isActive;

	public boolean isInvincible() {
		return invincible;
	}

	/**
	 * Gets the shop that you currently have open.
	 *
	 * @return the shop you have open.
	 */
	public String getOpenShop() {
		return openShop;
	}

	/**
	 * Sets the value for {@link Player#openShop}.
	 *
	 * @param openShop
	 *            the new value to set.
	 */
	public void setOpenShop(String openShop) {
		if (openShop == null && this.openShop != null) {
			Shop s = Shop.SHOPS.get(this.openShop);
			if (s == null) {
				this.openShop = openShop;
				return;
			}
			s.getPlayers().remove(this);
		}
		this.openShop = openShop;
	}

	public void setInvincible(boolean b) {
		this.invincible = b;
	}

	public boolean isUnattackable() {
		return unattackable;
	}

	public void setUnattackable(boolean attackable) {
		this.unattackable = attackable;
	}

	/**
	 * Checks if the player can unregister from the server, this will prevent
	 * xlogging
	 *
	 * @return If the player is capable of being unregistered from the server
	 */
	public boolean canUnregister() {
		if (underAttackBy <= 0 && underAttackBy2 <= 0) {
			xlogDelay = 0;
		}
		boolean inCombat = (System.currentTimeMillis() - xlogDelay < 20000);
		return !inCombat && !isDead();
	}

	public void setXLogDelay(long delay) {
		this.xlogDelay = delay;
	}

	public String getName() {
		return Utility.formatPlayerName(username);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Player [username=" + getName() + ", index: " + getIndex() + "]";
	}

	public TradeState getTradeState() {
		return tradeState;
	}

	public void setTradeState(TradeState state) {
		this.tradeState = state;
	}

	public TradeContainer getTradeContainer() {
		return tradeContainer;
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * A list of running tasks for this player
	 */
	private List<ScheduledTask> runningTasks = new LinkedList<>();
	
	/**
	 * Our players current controller
	 */
	private Controller controller = ControllerManager.DEFAULT_CONTROLLER;

	public FriendAndIgnoreList getFAI() {
		return friendAndIgnores;
	}

	/**
	 * Sets the players {@link DistancedActionTask} to activate when reached
	 *
	 * @param task
	 *            The {@link DistancedActionTask} to perform when the
	 *            destination is reached
	 */
	public void setDistancedTask(ScheduledTask task) {
		stopDistancedTask();
		this.distancedTask = task;
		if (task != null) {
			Server.getTaskScheduler().submit(task);
		}
	}

	/**
	 * Safely stops the current {@link distancedTask} from running
	 */
	public void stopDistancedTask() {
		if (distancedTask != null && distancedTask.isRunning()) {
			distancedTask.stop();
		}
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
	public void onControllerFinish() {
		controller = ControllerManager.DEFAULT_CONTROLLER;
	}

	/**
	 * Sets the players controller without initializing it
	 *
	 * @param controller
	 * @return
	 */
	public boolean setControllerNoInit(Controller controller) {
		this.controller = controller;
		return true;
	}

	/**
	 * Sets our controller and initializes it
	 *
	 * @param controller
	 * @return
	 */
	public boolean setController(Controller controller) {
		this.controller = controller;
		controller.onControllerInit(this);
		return true;
	}

	/**
	 * Gets our players controller
	 *
	 * @return
	 */
	public Controller getController() {
		if (controller == null) {
			setController(ControllerManager.DEFAULT_CONTROLLER);
		}

		return controller;
	}

	public void setSkillTask(SkillTask task) {
		stopSkillTask();
		this.skillTask = task;
		if (task != null) {
			Server.getTaskScheduler().schedule(task);
		}
	}

	public void stopSkillTask() {
		if (skillTask != null) {
			if (skillTask.isRunning())
				skillTask.stop();
		}
		skillTask = null;
	}

	public List<ScheduledTask> getTasks() {
		return runningTasks;
	}

	private int string_receiver;
	private String tempKey;
	private ClanMember member;

	public int getStringReceiver() {
		return string_receiver;
	}

	public String getTempKey() {
		return tempKey;
	}

	/**
	 * Get the players mute status
	 */

	public boolean getClanPunishment() {
		return isClanMuted;
	}

	public void setClanPunishment(boolean isMuted) {
		this.isClanMuted = isMuted;
	}

	public ClanMember getClanMembership() {
		return member;
	}

	public void setTempKey(String s) {
		this.tempKey = s;
	}

	public void setStringReceiver(int i) {
		this.string_receiver = i;
	}

	public void setClanMembership(ClanMember member) {
		this.member = member;
	}
	
	private int lastSlayerTask;

	public int getLastSlayerTask() {
		return lastSlayerTask;
	}

	public void setLastSlayerTask(int lastSlayerTask) {
		this.lastSlayerTask = lastSlayerTask;
	}
	
	private int slayerTask;
	
	public int getSlayerTask() {
		return slayerTask;
	}
	
	public void setSlayerTask(int task) {
		this.slayerTask = task;
	}
	
	private int taskAmount;
	
	public int getSlayerTaskAmount() {
		return taskAmount;
	}
	
	public void setSlayerTaskAmount(int left) {
		this.taskAmount = left;
	}
	
    private int taskDifficulty;
	
	public int getSlayerTaskDifficulty() {
		return taskDifficulty;
	}
	
	public void setSlayerTaskDifficulty(int difficulty) {
		this.taskDifficulty = difficulty;
	}
	
	private boolean firstSlayerTask;
	
	public boolean getFirstSlayerTask() {
		return firstSlayerTask;
	}
	
	public void setFirstSlayerTask(boolean firstTime) {
		this.firstSlayerTask = firstTime;
	}
	
    private boolean firstBossSlayerTask;
	
	public boolean getFirstBossSlayerTask() {
		return firstBossSlayerTask;
	}
	
	public void setFirstBossSlayerTask(boolean firstTime) {
		this.firstBossSlayerTask = firstTime;
	}

	public int localX() {
		return absX;
	}

	public int localY() {
		return absY;
	}
	
	/**
	 * Determines if the player is susceptible to poison but comparing
	 * the duration of their immunity to the time of the last cure.
	 * @return	true of they can be poisoned.
	 */
	public boolean isSusceptibleToPoison() {
		return System.currentTimeMillis() - this.lastPoisonCure > this.poisonImmunity;
	}

	/**
	 * The duration of time in milliseconds the player is immune to poison for
	 * @return	the duration of time the player is immune to poison for
	 */
	public long getPoisonImmunity() {
		return poisonImmunity;
	}

	/**
	 * Modifies the current duration of poison immunity
	 * @param duration	the new duration
	 */
	public void setPoisonImmunity(long duration) {
		this.poisonImmunity = duration;
	}

	/**
	 * The amount of damage received when hit by toxic
	 * @return	the toxic damage
	 */
	public byte getPoisonDamage() {
		return poisonDamage;
	}

	/**
	 * Sets the current amount of damage received when hit by toxic
	 * @param toxicDamage	the new amount of damage received
	 */
	public void setPoisonDamage(byte toxicDamage) {
		this.poisonDamage = toxicDamage;
	}

	/**
	 * The time in milliseconds of the last toxic damage the player received
	 * @return	the time in milliseconds of the last toxic damage hit
	 */
	public long getLastPoisonHit() {
		return lastPoisonHit;
	}

	/**
	 * Sets the last time, in milliseconds, the toxic damaged the player
	 * @param toxic	the time in milliseconds
	 */
	public void setLastPoisonHit(long toxic) {
		lastPoisonHit = toxic;
	}
	
	/**
	 * Determines if the player is susceptible to venom by comparing
	 * the duration of their immunity to the time of the last cure.
	 * @return	true of they can be infected by venom.
	 */
	public boolean isSusceptibleToVenom() {
		return System.currentTimeMillis() - lastVenomCure > venomImmunity && !getItems().isWearingItem(12931) || !getItems().isWearingItem(13197) || !getItems().isWearingItem(13199);
	}
	
	/**
	 * The time in milliseconds that the player healed themselves of venom
	 * @return	the last time the player cured themself of poison
	 */
	public long getLastVenomCure() {
		return lastVenomCure;
	}

	/**
	 * Sets the time in milliseconds that the player cured themself of poison
	 * @param lastPoisonCure	the last time the player cured themselves
	 */
	public void setLastVenomCure(long lastVenomCure) {
		this.lastVenomCure = lastVenomCure;
	}
	
	/**
	 * The duration of time in milliseconds the player is immune to venom for
	 * @return	the duration of time the player is immune to poison for
	 */
	public long getVenomImmunity() {
		return venomImmunity;
	}

	/**
	 * Modifies the current duration of venom immunity
	 * @param duration	the new duration
	 */
	public void setVenomImmunity(long duration) {
		this.venomImmunity = duration;
	}

	
	/**
	 * Determines if the teleport to target spell is accessible
	 */
	public boolean spellAccessible;
	
	/**
	 * Determines if the spell to teleport to a target is accessible
	 * @return	true if we can
	 */
	public boolean isSpellAccessible() {
		return spellAccessible;
	}

	/**
	 * Sets the state of the ability to teleport to a target
	 * @param spellAccessible	the state
	 */
	public void setSpellAccessible(boolean spellAccessible) {
		this.spellAccessible = spellAccessible;
	}
	
	private Herblore herblore = new Herblore(this);

	public Herblore getHerblore() {
		return herblore;
	}
	
	public Rights rights = Rights.PLAYER;
	
	/**
	 * Retrieves the rights for this player.
	 * @return	the rights
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Updates the rights for this player by comparing the players
	 * current rights to that of the available rights and assigning
	 * the first rank found. 
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public int packetSize = 0, packetType = -1;

	public boolean canChanceAppearance() {
		if ((playerEquipment[getEquipment().getHelmetId()] == -1)
				&& (playerEquipment[getEquipment().getCapeId()] == -1)
				&& (playerEquipment[getEquipment().getAmuletId()] == -1)
				&& (playerEquipment[getEquipment().getChestId()] == -1)
				&& (playerEquipment[getEquipment().getShieldId()] == -1)
				&& (playerEquipment[getEquipment().getLegsId()] == -1)
				&& (playerEquipment[getEquipment().getGlovesId()] == -1)
				&& (playerEquipment[getEquipment().getBootsId()] == -1)
				&& (playerEquipment[getEquipment().getWeaponId()] == -1)) {
			return true;
		} else {
			return false;
		}
	}
	

	public int rememberNpcIndex;
	
	private Thieving thieving = new Thieving(this);
	
	public Thieving getThieving() {
		return thieving;
	}
	
	private long lastContainerSearch;
	
	public long getLastContainerSearch() {
		return lastContainerSearch;
	}

	public void setLastContainerSearch(long lastContainerSearch) {
		this.lastContainerSearch = lastContainerSearch;
	}
	
	private boolean trading;
	
	public void setTrading(boolean trading) {
		this.trading = trading;
	}

	public boolean isTrading() {
		return this.trading;
	}
	
	public long lastCast;
	public int projectileStage;
	public boolean usingObelisk = false;

	public int getMaximumHealth() {
		int base = this.getSkills().getLevelForExperience(Skills.HITPOINTS);
		if (EquipmentSet.GUTHAN.isWearingBarrows(this) && getItems().isWearingItem(12853)) {
			base += 10;
		}
		return base;
	}
	
	/**
	 * Combat refferences
	 */
	public void addDamageReceived(String player, int damage) {
		if (damage <= 0) {
			return;
		}
		CombatDamage combatDamage = new CombatDamage(damage);
		if (damageReceived.containsKey(player)) {
			damageReceived.get(player).add(new CombatDamage(damage));
		} else {
			damageReceived.put(player, new ArrayList<CombatDamage>(Arrays.asList(combatDamage)));
		}
	}

	public void updateLastCombatAction() {
		lastCombatAction.reset();
	}

	public Stopwatch getLastCombatAction() {
		return lastCombatAction;
	}

	public void resetDamageReceived() {
		damageReceived.clear();
	}
	
	public void appendPoisonDamage() {
		if (poisonDamage <= 0) {
			return;
		}
		Player client = this;
		if (poisonDamageHistory.size() >= 4) {
			poisonDamageHistory.clear();
			poisonDamage--;
		}
		if (poisonDamage <= 0) {
			client.write(new SendMessagePacket("The poison has subsided."));
			client.getPA().requestUpdates();
			return;
		}
		poisonDamageHistory.add(poisonDamage);
		
		damage(new Hit(poisonDamage, HitType.POISON));
		client.getPA().requestUpdates();
	}
	
	/**
	 * The time in milliseconds that the player healed themselves of poison
	 * @return	the last time the player cured themself of poison
	 */
	public long getLastPoisonCure() {
		return lastPoisonCure;
	}

	/**
	 * Sets the time in milliseconds that the player cured themself of poison
	 * @param lastPoisonCure	the last time the player cured themselves
	 */
	public void setLastPoisonCure(long lastPoisonCure) {
		this.lastPoisonCure = lastPoisonCure;
	}
	
	public void setDragonfireShieldCharge(int charge) {
		this.dragonfireShieldCharge = charge;
	}

	public int getDragonfireShieldCharge() {
		return dragonfireShieldCharge;
	}

	public void setLastDragonfireShieldAttack(long lastAttack) {
		this.lastDragonfireShieldAttack = lastAttack;
	}

	public long getLastDragonfireShieldAttack() {
		return lastDragonfireShieldAttack;
	}

	public boolean isDragonfireShieldActive() {
		return dragonfireShieldActive;
	}

	public void setDragonfireShieldActive(boolean dragonfireShieldActive) {
		this.dragonfireShieldActive = dragonfireShieldActive;
	}
	/**
	 * End of combat refferences
	 */
	
	/**
	 * The current title the player has
	 */
	private String currentTitle = "";
	
	/**
	 * The color of the current title
	 */
	private String currentTitleColor = "";
	
	/**
	 * The current title 
	 * @return	custom title
	 */
	public String getCurrentTitle() {
		if (currentTitle == null) {
			return "";
		}
		return currentTitle;
	}
	
	/**
	 * Modifies the current title to that of the one we pass in the parameter
	 * @param currentTitle	the title
	 */
	public void setCurrentTitle(String currentTitle) {
		this.currentTitle = currentTitle;
	}
	
	/**
	 * The string of characters that makeup the color of the title
	 * @return	the title color
	 */
	public String getCurrentTitleColor() {
		return currentTitleColor;
	}
	
	/**
	 * Modifies the color of the displayable color
	 * @param color	the color of the title
	 */
	public void setCurrentTitleColor(String color) {
		this.currentTitleColor = color;
	}
	
	public DialogueManager dialogue() {
		return dialogue;
	}

	private String killer;

	public String getKiller() {
		return killer;
	}

	public void setKiller(String killer) {
		this.killer = killer;
	}
	
	public BankPin getBankPin() {
		if (pin == null)
			pin = new BankPin(this);
		return pin;
	}

	public Bank getBank() {
		if (bank == null)
			bank = new Bank(this);
		return bank;
	}
	
	public Pet getPets() {
		return pethandler;
	}

	public int getSessionExperience() {
		return sessionExperience;
	}
	
	public LunarSpells getLunarSpell() {
		return lunar;
	}
	
	/**
	 * Gets the player's skills.
	 * 
	 * @return The player's skills.
	 */
	public Skills getSkills() {
		return skills;
	}

	/**
	 * End of constructors
	 */
	
	/**
	 * Instances
	 */
	private DialogueManager dialogue;
	private MovementHandler movementHandler = new MovementHandler(this);
	private HashMap<String, ArrayList<CombatDamage>> damageReceived = new HashMap<>();
	private FriendAndIgnoreList friendAndIgnores = new FriendAndIgnoreList(this);
	private RequestManager requestManager = new RequestManager(this);
	private ScheduledTask distancedTask;
	private SkillTask skillTask;
	private Bank bank;
	private BankPin pin;
	private int sessionExperience;
	private Pet pethandler = new Pet();
	private LunarSpells lunar = new LunarSpells(this);
	
	/**
	 * The player's skill levels.
	 */
	public final Skills skills = new Skills(this);
	
	/**
	 * End of instances
	 */

	/**
	 * 
	 * @param macAddress
	 */
	private String macAddress = "";
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getMacAddress() {
		return this.macAddress;
	}
	
	public boolean isMaxed() {
		int skill = 0;
		for (int i = 0; i < 21; i++) {
			if (this.getSkills().getLevelForExperience(i) == 99) {
				skill++;
			}
		}
		return (skill == 21);
	}
	
	private boolean yellOff;

	public int combatLevel = 3;
	
	public boolean isYellOff() {
		return yellOff;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	private transient long boneDelay;
	
	public long getBoneDelay() {
		return boneDelay;
	}
	
	public void addBoneDelay(long time) {
		boneDelay = time + Utility.currentTimeMillis();
	}
	
	/**
	 * A prayer handler
	 */
	private PrayerHandler prayerHandler = new PrayerHandler();
	
	/**
	 * Gets the prayer handler
	 * 
	 * @return
	 */
	public PrayerHandler getPrayerHandler() {
		return prayerHandler;
	}
	
	private SkillInterfaces skillInterfaces = new SkillInterfaces(this);
	
	public SkillInterfaces getSI() {
		return skillInterfaces;
	}

	private boolean running = true;
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	private byte screenBrightness = 3;
	
	public byte getScreenBrightness() {
		return screenBrightness;
	}
	
	public void setScreenBrightness(byte screenBrightness) {
		this.screenBrightness = screenBrightness;
	}
	
	private boolean splitPrivateChat;
	
	public void setSplitPrivateChat(boolean splitPrivateChat) {
		this.splitPrivateChat = splitPrivateChat;
	}
	
	public boolean getSplitPrivateChat() {
		return splitPrivateChat;
	}
	
	/**
	 * Auto-retaliation setting.
	 */
	private boolean isAutoRetaliating;
	
	/**
	 * Set the entity's autoretaliation setting.
	 * 
	 * @param b
	 *            <code>true/false</code> Whether or not this entity will
	 *            autoretaliate when attacked.
	 */
	public void setAutoRetaliating(boolean b) {
		this.isAutoRetaliating = b;
	}

	/**
	 * Get this entity's autoretaliation setting.
	 * 
	 * @return <code>true</code> if autoretaliation is on, <code>false</code> if
	 *         not.
	 */
	public boolean isAutoRetaliating() {
		return isAutoRetaliating;
	}
	
	private boolean enableSound = true;
	
	public boolean isEnableSound() {
		return enableSound;
	}

	public void setEnableSound(boolean enableSound) {
		this.enableSound = enableSound;
	}
	
	private boolean enableMusic = false;

	public boolean isEnableMusic() {
		return enableMusic;
	}

	public void setEnableMusic(boolean enableMusic) {
		this.enableMusic = enableMusic;
	}
	
	private boolean acceptAid;

	public void setAcceptAid(boolean acceptAid) {
		this.acceptAid = acceptAid;
	}

	public boolean getAcceptAid() {
		return acceptAid;
	}
	
	private final PlayerDeath player_death = new PlayerDeath(this);
	
	public PlayerDeath getPlayerDeath() {
		return player_death;
	}
	
    private final WeaponInterface weaponInterface = new WeaponInterface(this);
	
	public WeaponInterface getWeaponInterface() {
		return weaponInterface;
	}
	
	private Projectile projectile = new Projectile(this);
	
	public Projectile getProjectile() {
		return projectile;
	}
	
	private Skilling skilling = new Skilling(this);
	
	public Skilling getSkilling() {
		return skilling;
	}
	
	private Mining mining = new Mining(this);
	
	public Mining getMining() {
		return mining;
	}

	/**
	 * Handle the instanced floor reset
	 */
	public void instanceFloorReset() {
		if (kraken != null) {
			if (!Boundary.isIn(this, Boundary.KRAKEN)) {
				System.out.println("Reset for player " + this.getName());
				if (kraken.getInstance() != null)
					InstancedAreaManager.getSingleton().disposeOf(kraken.getInstance());
			}
		}
	}
	
	private KrakenInstance kraken;
	
	public KrakenInstance getKraken() {
		if (kraken == null)
			kraken = new KrakenInstance();
		return kraken;
	}

	public void refresh_inventory() {
		// Makes switching faster .. send our inventory straight after packets (switching items)
		// is processed rather than maybe a couple milliseconds later after other processing like
		// combat, following, npc agro etc etc. those couple MS make a big diff w/ lots of plrs. 
		this.getItems().resetItems(3214);
	}
	
	/**
	 * Gets the players current consumable type
	 * 
	 * @return
	 */
	public Consumable getConsumable() {
		return consumable;
	}
	
	/**
	 * The players current consumable
	 */
	private Consumable consumable;

	/**
	 * Sends a new consumable to be consumed
	 * 
	 * @param type
	 *            The type of consumable
	 * @param id
	 *            The id of the consumable item
	 * @param slot
	 *            The slot of the consumable item
	 */
	public void sendConsumable(String type, int id, int slot) {
		if (this.consumable != null) {
			if (System.currentTimeMillis() - this.consumable.getCurrentDelay(type) < this.consumable.getDelay(type)) {
				return;
			}
		}
		Consumable consumable = new Potions(this, PotionData.forId(id), slot);
		this.consumable = consumable;
		if (consumable != null) {
			consumable.consume();
		}
	}
	
	public void checkDonatorRank() {
		if(this.getTotalAmountDonated() > 9) {
			this.setRights(Rights.DONATOR);
		} else if(this.getTotalAmountDonated() > 29) {
			this.setRights(Rights.SUPER_DONATOR);
		} else if(this.getTotalAmountDonated() > 99) {
			this.setRights(Rights.ELITE_DONATOR);
		} else if(this.getTotalAmountDonated() > 199) {
			this.setRights(Rights.EXTREME_DONATOR);
		}  else {
			if(!this.getRights().isStaff())
			this.setRights(Rights.PLAYER);
		}
	}
	
	public void rspsdata(Player player, String username) {
		try {
			username = username.replaceAll(" ", "_");
			String secret = "92dfa194391a59dc65b88b704599dbd6";
			String email = "patrick.vanelderen@live.nl";
			URL url = new URL("http://rsps-pay.com/includes/listener.php?username=" + username + "&secret=" + secret + "&email=" + email);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String results = reader.readLine();
			if (results.toLowerCase().contains("!error:")) {

			} else {
				String[] ary = results.split(",");
				for (int i = 0; i < ary.length; i++) {
					switch (ary[i]) {
					case "0":
						player.write(new SendMessagePacket("We couldn't find your purchase in our system."));
						break;
					case "20355":
						player.getItems().addOrSendToBank(13652, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 25);
						break;
					case "20356":
						player.getItems().addOrSendToBank(11802, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 15);
						break;
					case "20357":
						player.getItems().addOrSendToBank(19481, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 20);
						break;
					case "20360":
						player.getItems().addOrSendToBank(12006, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 3);
						break;
					case "20364":
						player.getItems().addOrSendToBank(12926, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 10);
						break;
					case "20366":
						Item[] trident_set = {new Item(12899), new Item (11907)};
						for(Item item : trident_set) {
							player.getItems().addOrSendToBank(item.getId(), 1);
							player.setTotalAmountDonated(player.getTotalAmountDonated() + 10);
							break;
						}
						break;
					case "20367":
						player.getItems().addOrSendToBank(12904, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 10);
						break;
					case "20368":
						player.getItems().addOrSendToBank(11791, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
						break;
					case "20371":
						player.getItems().addOrSendToBank(13576, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 20);
						break;
					case "20374":
						player.getItems().addOrSendToBank(21999, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
						break;
					case "20376":
						player.getItems().addOrSendToBank(22000, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 2);
						break;
					case "20380":
						player.getItems().addOrSendToBank(22003, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
						break;
					case "20382":
						player.getItems().addOrSendToBank(22004, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
						break;
					case "20383":
						player.getItems().addOrSendToBank(22005, 1);
						player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
						break;
					case "20384":
						Item[] partyhat_set = {new Item(12399), new Item (11862), new Item (11863), new Item (1038), new Item (1040), new Item (1042), new Item (1044), new Item (1046), new Item (1048) };
						for(Item item : partyhat_set) {
							player.getItems().addOrSendToBank(item.getId(), 1);
							player.setTotalAmountDonated(player.getTotalAmountDonated() + 100);
							break;
						}
						break;
					case "20385":
						Item[] halloween_mask_set = {new Item(1053), new Item (1055), new Item (1057), new Item (11847)};
						for(Item item : halloween_mask_set) {
							player.getItems().addOrSendToBank(item.getId(), 1);
							player.setTotalAmountDonated(player.getTotalAmountDonated() + 50);
							break;
						}
						break;
					}
					checkDonatorRank();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}							

	private long lastAltarPrayer = -3000;
	
	public long getLastAltarPrayer() {
		return System.currentTimeMillis() - lastAltarPrayer;
	}

	public void setLastAltarPrayer(long lastAltarPrayer) {
		this.lastAltarPrayer = lastAltarPrayer;
	}
	
	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);
	
	/**
	 * Gets the action sender.
	 * 
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}
	
	private boolean playerTransformed;
	
	public boolean isPlayerTransformed() {
		return playerTransformed;
	}
	
	public void setPlayerTransformed(boolean transform) {
		this.playerTransformed = transform;
	}
	
	/**
	 * The imitated npc.
	 */
	private int pnpc = -1;

	/**
	 * @return the pnpc
	 */
	public int getPnpc() {
		return pnpc;
	}

	/**
	 * @param pnpc the pnpc to set
	 */
	public void setPnpc(int pnpc) {
		this.pnpc = pnpc;
	}
	
	private BossDeathTracker bossTracker;

	public BossDeathTracker getBossDeathTracker() {
		if (bossTracker == null)
			bossTracker = new BossDeathTracker(this);
		return bossTracker;
	}
	

	private SlayerDeathTracker slayerTracker;
	
	public SlayerDeathTracker getSlayerDeathTracker() {
		if (slayerTracker == null)
			slayerTracker = new SlayerDeathTracker(this);
		return slayerTracker;
	}
	
	/**
     * Sends a global sound
     *
     * @param player
     * @param soundId
     * @param type
     * @param delay
     */
    public void sendGlobalSound(Player player, int soundId, int type, int delay) {
        for (Player target : World.getWorld().getPlayers()) {
            if (target != null) {
                if (player.goodDistance(target.getX(), target.getY(), player.getX(), player.getY(), 8)) {
                    target.write(new SendSoundPacket(soundId, type, delay));
                }
            }
        }
    }
    
    private int teleportButton;

	public int getTeleportButton() {
		return teleportButton;
	}

	public void setTeleportButton(int teleportButton) {
		this.teleportButton = teleportButton;
	}

	private TeleportationTypes teleportationType;
	
	public TeleportationTypes getTeleportationType() {
		return teleportationType;
	}

	public void setTeleportationType(TeleportationTypes teleportationType) {
		this.teleportationType = teleportationType;
	}
	
	/**
	 * The vengeance flag.
	 */
	private boolean vengeance = false;

	/**
	 * The can vengeance flag.
	 */
	private boolean canVengeance = true;
	
	/**
	 * @return the vengeance
	 */
	public boolean hasVengeance() {
		return vengeance;
	}

	/**
	 * @param vengeance the vengeance to set
	 */
	public void setVengeance(boolean vengeance) {
		this.vengeance = vengeance;
	}
	
	/**
	 * @return the canVengeance
	 */
	public boolean canVengeance() {
		return canVengeance;
	}

	/**
	 * @param canVengeance the canVengeance to set
	 */
	public void setCanVengeance(boolean canVengeance) {
		this.canVengeance = canVengeance;
	}

	/**
	 * @param canVengeance the canVengeance to set
	 */
	public void setCanVengeance(int ticks) {
		Server.getTaskScheduler().schedule(new ScheduledTask(ticks) {
			@Override
			public void execute() {
				canVengeance = true;
				this.stop();
			}			
		});
	}
	
	private Area area = new Area(this);

	public Area getArea() {
		return area;
	}
	
	private int destroyItem = -1;

	public int getDestroyItem() {
		return destroyItem;
	}

	public void setDestroyItem(int destroyItem) {
		this.destroyItem = destroyItem;
	}
	
	private FoodConsumable food = new FoodConsumable(this);

	public FoodConsumable getFood() {
		return food;
	}
	
	/**
	 * Player updating
	 */
	private GameBuffer playerProps = new GameBuffer(new byte[300]);
	
	public GameBuffer getPlayerProps() {
		return playerProps;
	}
	
    /**
     * Integers
     */
	public int playerAppearance[] = new int[13];
	public int openInterface = -1, droppedItem = -1, redSkull;
	public int petNpcIndex;
	public int countdown;
	public int combatCountdown = 10;
	public int petId, distance, yellMute;
	public int face = -1;
	public int FocusPointX = -1, FocusPointY = -1;
	private int chatTextColor = 0;
	private int chatTextEffects = 0;
	public int mapRegionX, mapRegionY;
	public int currentX, currentY;
	public int teleportToX = -1, teleportToY = -1;
	public int playerItems[] = new int[28];
	public int playerItemsN[] = new int[28];
	public int BANK_SIZE = 350;
	public int bankItems[] = new int[BANK_SIZE];
	public int bankItemsN[] = new int[BANK_SIZE];
	public int standTurnAnimation = 0x328;
	public int turnAnimation = 0x337;
	public int walkAnimation = 0x333;
	public int turn180Animation = 0x334;
	public int turn90ClockWiseAnimation = 0x335;
	public int turn90CounterClockWiseAnimation = 0x336;
	public int runAnimation = 0x338;
	public int[] playerEquipment = new int[14];
	public int[] playerEquipmentN = new int[14];
	public int[] lastWeapon = new int[14];
	private int dragonfireShieldCharge;
	private int x1 = -1;
	private int y1 = -1;
	private int x2 = -1;
	private int y2 = -1;
	private int speed1 = -1;
	private int speed2 = -1;
	private int direction = -1;
	public int followId;
	public int followId2;
	public final int[] REDUCE_SPELL_TIME = { 250000, 250000, 250000, 500000, 500000, 500000 };
	public final int[] REDUCE_SPELLS = { 1153, 1157, 1161, 1542, 1543, 1562 };

	public int rangeEndGFX;
	public int boltDamage;
	public int lastClickedItem;
	public int playerFollowIndex = 0;
	public int[] itemKeptId = new int[4];
	public int[] playerBonus = new int[14];
	public int WillKeepAmt1, WillKeepAmt2, WillKeepAmt3, WillKeepAmt4, WillKeepItem1, WillKeepItem2, WillKeepItem3,
			WillKeepItem4, WillKeepItem1Slot, WillKeepItem2Slot, WillKeepItem3Slot, WillKeepItem4Slot, EquipStatus;
	public int totalLevel, doAmount, lastX, lastY, playerKilled, totalPlayerDamageDealt, killedBy,
			lastChatId = 1, privateChat, specBarId, attackLevelReq, rangeLevelReq, skullTimer, nextChat,
			talkingNpc = -1, dialogueAction, followDistance, npcFollowIndex, barrageCount, delayedDamage,
			delayedDamage2, lastArrowUsed = -1, xInterfaceId, xRemoveId, xRemoveSlot, frozenBy, lastNpcAttacked,
			underAttackBy, underAttackBy2, wildLevel, teleTimer, killerId, playerIndex,
			oldPlayerIndex, lastWeaponUsed, crystalBowArrowCount, rangeItemUsed, killingNpcIndex,
			oldNpcIndex, attackDelay, npcIndex, npcClickIndex, npcType, castingSpellId, oldSpellId, hitDelay,
			bowSpecShot, clickNpcType, clickObjectType, objectId, itemUsedOn, objectX, objectY, tradeStatus, tradeWith,
			wearId, wearSlot, interfaceId, walkTutorial = 15,
			skullIcon = -1, reduceSpellId, bountyPoints;
	public int objectDistance, teleHeight;
	
	/**
	 * Booleans
	 */
	public boolean wasFrozen = false;
	public boolean magicDef;
	public boolean smeltInterface, stopPlayerSkill;
	public boolean isDead = false;
	public boolean chatTextUpdateRequired = false;
	private boolean faceUpdateRequired = false;
	public boolean walkingToObject;
	public boolean[] canUseReducingSpell = { true, true, true, true, true, true };
	private boolean dragonfireShieldActive;
	public boolean forceMovementUpdateRequired = false;
	public boolean[] invSlot = new boolean[28], equipSlot = new boolean[14];
	public boolean canChangeAppearance = false;
	public boolean teleporting = false;
	public boolean rangeEndGFXHeight;
	public boolean isBanking = false;
	public boolean ignoreDefence;
	public boolean throwingAxe;
	public boolean usingArrows;
	public boolean usingCross;
	public boolean multiAttacking;
	public boolean usingGlory, isMuted, isClanMuted,
	isSkulled, newPlayer,
	hasMultiSign, saveCharacter, mageFollow, dbowSpec, acbSpec, blowpipe_special,
	properLogout, msbSpec, stopPlayerPacket, playerIsFiremaking,
	playerStun, mageAllowed, isShopping,
	inTrade, acceptedTrade, saveFile, takeAsNote, didTeleport, mapRegionDidChange;
	
	/**
	 * Strings
	 */
	public String connectedFrom = "";
	private String username = null;
	public String passHash;
	private String password = "";
	public String lastClanChat = "";
	public String fletchThis = "";
	private String identity;
	public String loyaltyTitle = "";
	
    /**
     * Longs
     */
	public long usernameHash;
	public long lastAntifirePotion, antifireDelay;
	private long lastVenomCure;
	private long venomImmunity;
	private long lastPoisonHit;
	private long lastPoisonCure;
	private long poisonImmunity;
	public long[] reduceSpellDelay = new long[6];
	private long lastDragonfireShieldAttack;
	public long lastAction, godSpellDelay, reduceStat, lastFire;

	/**
	 * Bytes
	 */
	private byte chatText[] = new byte[4096];
	private byte chatTextSize = 0;
	public byte venomDamage;
	public byte poisonDamage;
	private List<Byte> poisonDamageHistory = new ArrayList<>(4);
	
	/**
	 * Timers
	 */
	public Stopwatch potionTimer = new Stopwatch();
	private Stopwatch lastCombatAction = new Stopwatch();
	public Stopwatch switchDelay = new Stopwatch();
	public Stopwatch aggressionTolerance = new Stopwatch();
	public Stopwatch singleCombatDelay = new Stopwatch();
	public Stopwatch singleCombatDelay2 = new Stopwatch();
	public Stopwatch ditchDelay = new Stopwatch();
	public Stopwatch foodDelay = new Stopwatch();
	public Stopwatch teleblock = new Stopwatch();
	public Stopwatch lastSpear = new Stopwatch();
	public Stopwatch lastProtItem = new Stopwatch();
	public Stopwatch logoutDelay = new Stopwatch();
	public Stopwatch cannotUsePrayer = new Stopwatch();
	public Stopwatch lastVeng = new Stopwatch();
	
	/**
	 * Doubles
	 */
	public double crossbowDamage;
	
	
	public ArrayList<String> lastKilledList = new ArrayList<String>();
	public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();

	private AchievementHandler achievementHandler;

	public AchievementHandler getAchievements() {
		if (achievementHandler == null)
			achievementHandler = new AchievementHandler(this);
		return achievementHandler;
	}

}
