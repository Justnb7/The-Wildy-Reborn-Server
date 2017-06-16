package com.model.game.character.player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import com.model.Appearance;
import com.model.InterfaceState;
import com.model.UpdateFlags.UpdateFlag;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.DamageMap;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.effect.SkullType;
import com.model.game.character.combat.magic.LunarSpells;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.combat.weapon.WeaponInterface;
import com.model.game.character.following.PlayerFollowing;
import com.model.game.character.npc.NPC;
import com.model.game.character.npc.NPCAggression;
import com.model.game.character.npc.SlayerDeathTracker;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.account.Account;
import com.model.game.character.player.account.ironman.GameModeSelection;
import com.model.game.character.player.content.FriendAndIgnoreList;
import com.model.game.character.player.content.KillTracker.KillEntry;
import com.model.game.character.player.content.achievements.AchievementHandler;
import com.model.game.character.player.content.clan.ClanMember;
import com.model.game.character.player.content.consumable.Consumable;
import com.model.game.character.player.content.consumable.food.FoodConsumable;
import com.model.game.character.player.content.consumable.potion.PotionData;
import com.model.game.character.player.content.consumable.potion.Potions;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.Duel;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.content.teleport.TeleportHandler.TeleportationTypes;
import com.model.game.character.player.controller.Controller;
import com.model.game.character.player.controller.ControllerManager;
import com.model.game.character.player.dialogue.DialogueManager;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.character.player.instances.impl.FightCaveInstance;
import com.model.game.character.player.instances.impl.KrakenInstance;
import com.model.game.character.player.minigames.fight_caves.FightCaves;
import com.model.game.character.player.minigames.warriors_guild.WarriorsGuild;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.character.player.skill.herblore.Herblore;
import com.model.game.character.player.skill.slayer.interfaceController.SlayerInterface;
import com.model.game.character.player.skill.thieving.Thieving;
import com.model.game.character.walking.MovementHandler;
import com.model.game.item.container.impl.BankContainer;
import com.model.game.item.container.impl.InventoryContainer;
import com.model.game.item.container.impl.PriceChecker;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.item.container.impl.equipment.EquipmentContainer;
import com.model.game.item.container.impl.trade.TradeContainer;
import com.model.game.item.container.impl.trade.TradeSession;
import com.model.game.item.container.impl.trade.TradeSession.TradeStage;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Area;
import com.model.game.location.Location;
import com.model.net.network.rsa.GameBuffer;
import com.model.net.network.rsa.ISAACRandomGen;
import com.model.net.network.session.GameSession;
import com.model.net.packet.Packet;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.task.impl.DeathEvent;
import com.model.task.impl.DistancedActionTask;
import com.model.utility.MutableNumber;
import com.model.utility.Stopwatch;
import com.model.utility.Utility;

import io.netty.buffer.Unpooled;

public class Player extends Entity {
	
	/**
	 * The players damage map
	 */
	private DamageMap damageMap = new DamageMap();
	
	/**
	 * Gets the players damage map
	 * 
	 * @return
	 */
	public DamageMap getDamageMap() {
		return damageMap;
	}
	
	/**
	 * The player's appearance information.
	 */
	public Appearance appearance = new Appearance();
	
	/**
	 * Gets the player's appearance.
	 *
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}
	
	private final MutableNumber poisonImmunity = new MutableNumber();
	
	/**
     * Gets the poison immunity counter value.
     *
     * @return the poison immunity counter.
     */
    public MutableNumber getPoisonImmunity() {
        return poisonImmunity;
    }
	
	private Thieving thieving = new Thieving(this);
	
	public Thieving getThieving() {
		return thieving;
	}
	
	/**
	 * The account type
	 */
    private Account account;
	
    /**
     * Gets the account type, exampe Ironman Account
     * @return the accounts
     */
	public Account getAccount() {
		if (account == null)
			account = new Account(this);
		return account;
	}
	
	private final BankContainer bank = new BankContainer(this);
	
	public BankContainer getBank() {
		return bank;
	}
	
	/**
     * The container that holds the inventory items.
     */
	private final InventoryContainer inventory = new InventoryContainer(this);
	
	/**
     * Gets the container that holds the inventory items.
     *
     * @return the container for the inventory.
     */
	public InventoryContainer getInventory() {
		return inventory;
	}
    
    private int otherPlayerTradeIndex = -1;
    
    public int getOtherPlayerTradeIndex() {
		return otherPlayerTradeIndex;
	}

	public void setOtherPlayerTradeIndex(int otherPlayerTradeIndex) {
		this.otherPlayerTradeIndex = otherPlayerTradeIndex;
	}
	
	private final TradeContainer tradeContainer = new TradeContainer(this);

	public TradeContainer getTradeContainer() {
		return tradeContainer;
	}

	public boolean isTrading() {
		return getTradeSession().isTrading();
	}
	
	private TradeSession tradeSession = new TradeSession(this);
	
	public TradeSession getTradeSession() {
		return tradeSession;
	}
	
	private boolean tradeRequest;

	/**
	 * @param tradeRequest
	 *            The flag that denotes a player sent a trade request.
	 */
	public void setTradeRequest(boolean tradeRequest) {
		this.tradeRequest = tradeRequest;
	}

	/**
	 * Determines if this player sent a trade request.
	 *
	 * @return {@code true} If this player sent a trade request. {@code false}
	 *         otherwise.
	 */
	public boolean isTradeRequest() {
		return tradeRequest;
	}
    
    /**
     * The container that holds the equipment items.
     */
    private final EquipmentContainer equipment = new EquipmentContainer(this);
    
    /**
     * Gets the container that holds the equipment items.
     *
     * @return the container for the equipment.
     */
    public EquipmentContainer getEquipment() {
        return equipment;
    }
	
	private Duel duelSession = new Duel(this);
	
	public Duel getDuel() {
		return duelSession;
	}
	
	/**
     * The flag that determines if items should be inserted when banking.
     */
    private boolean insertItem;
	
	/**
     * Determines if items should be inserted when banking.
     *
     * @return {@code true} if items should be inserted, {@code false}
     *         otherwise.
     */
    public boolean isInsertItem() {
        return insertItem;
    }

    /**
     * Sets the value for {@link Player#insertItem}.
     *
     * @param insertItem
     *            the new value to set.
     */
    public void setInsertItem(boolean insertItem) {
        this.insertItem = insertItem;
    }
    

    /**
     * The flag that determines if a bank item should be withdrawn as a note.
     */
    private boolean withdrawAsNote;

    /**
     * Determines if a bank item should be withdrawn as a note.
     *
     * @return {@code true} if items should be withdrawn as notes, {@code false}
     *         otherwise.
     */
    public boolean isWithdrawAsNote() {
        return withdrawAsNote;
    }

    /**
     * Sets the value for {@link Player#withdrawAsNote}.
     *
     * @param withdrawAsNote
     *            the new value to set.
     */
    public void setWithdrawAsNote(boolean withdrawAsNote) {
        this.withdrawAsNote = withdrawAsNote;
    }
	
	/**
	 * The player is still in the tutorial
	 */
    private boolean tutorial;
	
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
	 * Teleport to slayer task abilitie
	 */
	private boolean canTeleToTask;
	
	public boolean canTeleportToSlayerTask() {
		return canTeleToTask;
	}
	
	public void setCanTeleportToTask(boolean able_to_tele) {
		this.canTeleToTask = able_to_tele;
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
	 * A reward for playing pest control
	 */
	private int pestPoints;
	
	public int getPestControlPoints() {
		return pestPoints;
	}
	
	public void setPestControlPoints(int pestPoints) {
		this.pestPoints = pestPoints;
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
	 * The players current prayer points
	 */
	private double prayerPoint = 1.0;

	/**
	 * The players current active prayers
	 */
	private boolean[] activePrayer = new boolean[29];

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
	public boolean isActivePrayer(Prayers prayer) {
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
	public Player setActivePrayer(Prayers prayer, boolean active) {
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
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public final RunePouchContainer runePouchContainer = new RunePouchContainer(this);
	
	/**
	 * @see {@link #runePouchContainer}.
	 * <b>no point in documentating a getter</b>
	 */
	public RunePouchContainer getRunePouch() {
		return runePouchContainer;
	}
	
	/**
	 * The player's spell book.
	 */
	public SpellBook book = SpellBook.MODERN;
	
	/**
	 * Get the players current spellbook
	 * 
	 * @return the spellbook
	 */
	public SpellBook getSpellBook() {
		return book;
	}
	
	/**
	 * Set the players spellbook
	 *
	 */
	public void setSpellBook(SpellBook spell) {
		this.book = spell;
	}
	
	/**
	 * The player's skulltype.
	 */
	private SkullType skullType = SkullType.NONE;
	
	/**
	 * Get the players current skulltype
	 * 
	 * @return the skulltype
	 */
	public SkullType getSkullType() {
		return skullType;
	}

	/**
	 * Set the players skullType
	 * 
	 * @param the
	 *            skullType
	 */
	public void setSkullType(SkullType skullType) {
		this.skullType = skullType;
	}
	
	/**
	 * The player's skull timer.
	 */
	private int skullTimer;

	/**
	 * Is the player skulled
	 * 
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean isSkulled() {
		return skullTimer > 0;
	}

	/**
	 * Set The player's skull timer
	 * 
	 * @param the
	 *            time the player remains skulled for
	 */
	public void setSkullTimer(int skullTimer) {
		this.skullTimer = skullTimer;
	}

	/**
	 * Removes ticks from the skull timer
	 * 
	 * @return the ticks
	 */
	public int decrementSkullTimer() {
		return this.skullTimer--;
	}

	/**
	 * Get the players skull timer
	 * 
	 * @return the skulltimer
	 */
	public int getSkullTimer() {
		return this.skullTimer;
	}
	
	/**
	 * A custom yell color
	 */
	private String yellColor = "ff0000";
	
	/**
	 * Get the custom yell colot
	 * 
	 * @return the custom color
	 */
	public String getYellColor() {
		return yellColor;
	}

	/**
	 * Set the custom yell color
	 * 
	 * @param the
	 *            custom yellColor
	 */
	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}
	
	/**
	 * The player is in debug mode
	 */
	private boolean debugMode;
	
	/**
	 * Are we using the debug mode?
	 * 
	 * @return the debug option
	 */
	public boolean inDebugMode() {
		return debugMode;
	}
	
	/**
	 * Activate the debug mode
	 * 
	 * @param We're
	 *            activating the debug option
	 */
	public void setDebugMode(boolean on) {
		this.debugMode = on;
	}
	
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

	private long xlogDelay;
	
	public int getId() {
		return getIndex();
	}
	
	/**
	 * The player's skill levels.
	 */
	public Skills skills = new Skills(this);
	
	public Skills getSkills() {
		return skills;
	}
	
	@Override
	public boolean canTrade() {
		return getTradeSession().getTradeStage() == TradeStage.REQUEST;
	}
	
	@Override
	public int getCombatCooldownDelay() {
		return CombatFormulae.getCombatCooldownDelay(this);
	}
	
	@Override
	public boolean isNPC() {
		return false;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}
	
	@Override
	public int yLength() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public Location getLocation() {
		return new Location(absX, absY, heightLevel);
	}
	
	@Override
	public Location getCentreLocation() {
		return getLocation();
	}
	
	@Override
	public int getProjectileLockonIndex() {
		return -getIndex() - 1;
	}
	
	@Override
	public void setDefaultAnimations() {
		setStandAnimation(Animation.create(808).getId());
		setRunAnimation(Animation.create(824).getId());
		setWalkAnimation(Animation.create(819).getId());
		setStandTurnAnimation(Animation.create(823).getId());
		setTurn180Animation(Animation.create(820).getId());
		setTurn90ClockwiseAnimation(Animation.create(821).getId());
		setTurn90CounterClockwiseAnimation(Animation.create(822).getId());
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

			{ 12445, 85, 1819, 0, 0, 1299, 0, 65, 563, 1, 562, 1, 560, 1, 0, 0, 0 }, // teleblock

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
				c.getActionSender().sendConfig(108, 1);
				c.getActionSender().sendSidebarInterface(0, 328);
				break;
			}
		}
	}

	public boolean withinDistance(Player otherPlr) {
		if (heightLevel != otherPlr.heightLevel) {
			return false;
		}
		int deltaX = otherPlr.getX() - getX(), deltaY = otherPlr.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(NPC npc) {
		if (heightLevel != npc.heightLevel) {
			return false;
		}
		if (!npc.isVisible()) {
			return false;
		}
		int deltaX = npc.getX() - getX(), deltaY = npc.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	private GameBuffer updateBlock = null;

	public void clearUpdateFlags() {
		this.reset();
		setTeleporting(false);
		faceTileX = -1;
		faceTileY = -1;
		entityFaceIndex = 65535;
		setUpdateBlock(null);
		this.getUpdateFlags().primary = null;
		this.getUpdateFlags().secondary = null;
		this.getUpdateFlags().reset();
		this.setForceWalk(new int[0], false);
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
		logoutDelay.reset();
		updateLastCombatAction();
		setInCombat(true);
	}

	public String getIdentity() {
		return identity;
	}

	public String setIdentity(String identity) {
		return this.identity = identity;
	}

	public Player(String username) {
		super(EntityType.PLAYER);
		this.username = username;
		usernameHash = Utility.playerNameToInt64(username);
		this.teleportToX = 3087;
		this.teleportToY = 3499;
		this.teleHeight = 0;
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		dialogue = new DialogueManager(this);
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		getMovementHandler().reset();
		outStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		outStream.offset = 0;
		inStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		inStream.offset = 0;
	}

	@Override
	public boolean moving() {
		return movementHandler.isMoving();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Hit decrementHP(Hit hit) {
		int damage = hit.getDamage(); 
		if (this.getSkills().getLevel(3) - damage <= 0) {
			damage = this.getSkills().getLevel(3);
			//System.out.println("["+this.getName()+"] dmg was over current hp ("+getSkills().getLevel(3)+"), adjusted to "+damage);
		}
		
		//System.out.println("you're defo using the right method btw "+damage+" vs "+this.getSkills().getLevel(3));
		if (!this.hasAttribute("infhp"))
			this.getSkills().setLevel(3, this.getSkills().getLevel(3) - damage);

		/*
		 * Check if our player has died, if so start the death task
		 * 
		 */
		if (getSkills().getLevel(3) < 1 && !isDead()) {
			setDead(true);
			Server.getTaskScheduler().schedule(new DeathEvent(this));
		}
		return new Hit(damage, hit.getType());
	}

	public void flushOutStream() {
		if (outStream == null || getSession() == null || !getSession().getChannel().isOpen() || (outStream.offset == 0)) {
			return;
		}
		byte[] temp = new byte[outStream.offset];
		System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
		getSession().getChannel().writeAndFlush(new Packet(-1, Unpooled.wrappedBuffer(temp)));
		outStream.offset = 0;
	}
	
	public boolean isMuted() {
		return this.isMuted;
	}

	public void logout() {
		//Are we allowed to logout
		if (!controller.canLogOut(this)) {
			return;
		}
		
		//Reset poison and venom
		this.infection = 0;
		this.infected = false;
		this.poisonDamage = 0;
		
		//Dueling check
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(this, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() >= MultiplayerSessionStage.FURTHER_INTERACTION) {
				getActionSender().sendMessage("You are not permitted to logout during a duel. If you forcefully logout you will");
				getActionSender().sendMessage("lose all of your staked items, if any, to your opponent.");
			}
		}
		
		//If we're no longer in combat we can goahead and logout
		if (logoutDelay.elapsed(10000) && getLastCombatAction().elapsed(600)) {
			outStream.writeFrame(109);
			flushOutStream();
			properLogout = true;
			World.getWorld().unregister(this);
		} else {
			getActionSender().sendMessage("You must wait 10 seconds before logging out.");
		}
	}

	@Override
	public void process() {
		if (this.getTimePlayed() < Integer.MAX_VALUE) {
			this.setTimePlayed(this.getTimePlayed() + 1);
		}

		PrayerHandler.handlePrayerDraining(this);

		// Follow player - not combat
		process_following();

		combatProcessing();

		controller.tick(this);

		NPCAggression.process(this);

		if (hasAttribute("antiFire")) {
			if (System.currentTimeMillis() - (long)getAttribute("antiFire", 0L) < 360000) {
				if (System.currentTimeMillis() - (long)getAttribute("antiFire", 0L) > 15000 && System.currentTimeMillis() - (long)getAttribute("antiFire", 0L) < 14000) {
					getActionSender().sendMessage("Your anti fire potion is about to wear off!");
				}
			} else if ((long)getAttribute("antiFire", 0L) > 0L && System.currentTimeMillis() - (long)getAttribute("antiFire", 0L) > 360000) {
				getActionSender().sendMessage("Your resistance to dragon breath has worn off!");
				removeAttribute("antiFire");
			}
		}

	}

	public GameBuffer getInStream() {
		return inStream;
	}

	public GameBuffer getOutStream() {
		return outStream;
	}

	private PlayerFollowing player_following = new PlayerFollowing(this);
	
	public PlayerFollowing getPlayerFollowing() {
		return player_following;
	}
	
	public void process_following() {
		if (followTarget != null) {
			// Following complete, reset
			if (!goodDistance(followTarget.getX(), followTarget.getY(), getX(), getY(), 1))
				getPlayerFollowing().follow(!asPlayer().getCombatState().noTarget(), followTarget);
		}
	}

	public void combatProcessing() {
		if (lastCombatAction.elapsed(6000)) {
			setInCombat(false);
		}

		if (getSkullTimer() > 0) {
			decrementSkullTimer();
			if (getSkullTimer() == 1) {
				attackedPlayers.clear();
				setSkullType(SkullType.NONE);
				setSkullTimer(0);
				getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
		}

		super.frozen_process();

		if (getCombatState().getAttackDelay() > 0) {
			getCombatState().decreaseAttackDelay(1);
		}

		// Handles following every game tick, only attacks when delay=0
		Combat.playerVsEntity(this);
	}

	public void updateWalkEntities() {
		ControllerManager.setControllerOnWalk(this);
		ControllerManager.updateControllerOnWalk(this);
		
		if (hasMultiSign && !getArea().inMulti()) {
			hasMultiSign = false;
			this.getActionSender().sendMultiway(-1);
		} else if (!hasMultiSign && getArea().inMulti()) {
			hasMultiSign = true;
			this.getActionSender().sendMultiway(1);
		}
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

	/**
	 * Checks if the player can unregister from the server, this will prevent
	 * xlogging
	 *
	 * @return If the player is capable of being unregistered from the server
	 */
	public boolean canUnregister() {
		if (System.currentTimeMillis() - lastWasHitTime > 4000) { // out of cb
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
	 * Determines if the player is susceptible to venom by comparing
	 * the duration of their immunity to the time of the last cure.
	 * @return	true of they can be infected by venom.
	 */
	public boolean isSusceptibleToVenom() {
		return System.currentTimeMillis() - lastVenomCure > venomImmunity && !getEquipment().contains(12931) && !getEquipment().contains(13197) && !getEquipment().contains(13199);
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
	
	public long lastCast;
	public boolean usingObelisk = false;

	public int getMaximumHealth() {
		int base = this.getSkills().getLevelForExperience(Skills.HITPOINTS);
		return base;
	}

	public void updateLastCombatAction() {
		lastCombatAction.reset();
	}

	public Stopwatch getLastCombatAction() {
		return lastCombatAction;
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

	public int getSessionExperience() {
		return sessionExperience;
	}
	
	public LunarSpells getLunarSpell() {
		return lunar;
	}

	/**
	 * End of constructors
	 */
	
	/**
	 * Instances
	 */
	private DialogueManager dialogue;
	private MovementHandler movementHandler = new MovementHandler(this);
	private FriendAndIgnoreList friendAndIgnores = new FriendAndIgnoreList(this);
	private RequestManager requestManager = new RequestManager(this);
	private ScheduledTask distancedTask;
	private SkillTask skillTask;
	private int sessionExperience;
	private LunarSpells lunar = new LunarSpells(this);
	
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
	
    private final WeaponInterface weaponInterface = new WeaponInterface(this);
	
	public WeaponInterface getWeaponInterface() {
		return weaponInterface;
	}

	/**
	 * Handle the instanced floor reset
	 */
	public void instanceFloorReset() {
		if(fci != null) {
			if (!Boundary.isIn(this, Boundary.FIGHT_CAVE)) {
				System.out.println("Restting fight cave instance for: " + this.getName());
				if(fci.getInstance() != null)
					InstancedAreaManager.getSingleton().disposeOf(fci.getInstance());
			}
		} else if (kraken != null) {
			if (!Boundary.isIn(this, Boundary.KRAKEN)) {
				System.out.println("Resetting kraken instance for: " + this.getName());
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
	
	private FightCaveInstance fci;
	
	public FightCaveInstance getFCI() {
		if(fci == null)
			fci = new FightCaveInstance();
		return fci;
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
	
	public CopyOnWriteArrayList<KillEntry> getKillTracker() {
		return killTracker;
	}
	
	private CopyOnWriteArrayList<KillEntry> killTracker = new CopyOnWriteArrayList<KillEntry>();

	public void setKillTracker(CopyOnWriteArrayList<KillEntry> entry) {
		this.killTracker = entry;
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
                    target.getActionSender().sendSound(soundId, type, delay);
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
	
	/**
	 * @param set shopping true or false
	 */
	public void setShopping(boolean shopping) {
		this.shopping = shopping;
	}
	
	/**
	 * The shopping flag.
	 */
	private boolean shopping = false;

	/**
	 * @return we're shopping
	 */
	public boolean isShopping() {
		return shopping;
	}
	
    private boolean banking;
	
	public void setBanking(boolean banking) {
		this.banking = banking;
	}

	public boolean isBanking() {
		return this.banking;
	}
	
	/**
	 * We can't perform actions while the other person is busy.
	 */
	public boolean isBusy() {
		if(isTeleporting() || isShopping() || isTrading() || isBanking()) {
			return true;
		}
		return false;
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
	 * Gets the players bonuses
	 * 
	 * @return
	 */
	public int[] getBonuses() {
		return bonuses;
	}
	
	/**
	 * The players equipment bonuses
	 */
	private int[] bonuses = new int[Combat.BONUS_NAMES.length];
	
	public List<Integer> searchList = new ArrayList<>();
	
    /**
     * Integers
     */
	public int countdown;
	public int combatCountdown = 10;
	private int chatTextColor = 0;
	private int chatTextEffects = 0;
	public int mapRegionX, mapRegionY;
	public int currentX, currentY;
	public int teleportToX = -1, teleportToY = -1, teleHeight;
	public int lastClickedItem;

	public int totalLevel,
			lastChatId = 1, privateChat, specBarId,
			xInterfaceId, xRemoveId, xRemoveSlot, frozenBy, wildLevel, teleTimer, walkTutorial = 15, bountyPoints;
	
	/**
	 * Booleans
	 */
	public boolean dropListSorted;
	public boolean dropRateInKills;
	public boolean isMuted, isClanMuted, hasMultiSign, properLogout;
	
	/**
	 * Strings
	 */
	public String connectedFrom = "";
	private String username = null;
	private String password = "";
	public String lastClanChat = "";
	private String identity;
	public String loyaltyTitle = "";
	
    /**
     * Longs
     */
	public long usernameHash;
	private long lastVenomCure;
	private long venomImmunity;
	public long godSpellDelay;
	public long lastDropTableSelected;
	public long lastDropTableSearch;

	/**
	 * Bytes
	 */
	private byte chatText[] = new byte[4096];
	private byte chatTextSize = 0;
	public byte venomDamage;
	public byte poisonDamage;
	
	/**
	 * Timers
	 */
	private Stopwatch lastCombatAction = new Stopwatch();
	public Stopwatch switchDelay = new Stopwatch();
	public Stopwatch aggressionTolerance = new Stopwatch();
	public Stopwatch ditchDelay = new Stopwatch();
	public Stopwatch foodDelay = new Stopwatch();
	public Stopwatch teleblock = new Stopwatch();
	public Stopwatch lastSpear = new Stopwatch();
	public Stopwatch lastProtItem = new Stopwatch();
	public Stopwatch logoutDelay = new Stopwatch();
	public Stopwatch cannotUsePrayer = new Stopwatch();
	public Stopwatch lastVeng = new Stopwatch();
	
	
	public ArrayList<String> lastKilledList = new ArrayList<String>();
	public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();

	private AchievementHandler achievementHandler;

	public AchievementHandler getAchievements() {
		if (achievementHandler == null)
			achievementHandler = new AchievementHandler(this);
		return achievementHandler;
	}

	public void debug(String string) {
		if (this.rights == Rights.ADMINISTRATOR) {
			this.getActionSender().sendMessage(string);
		}
	}
	
	/**
	 * The last fire made.
	 */
	private long lastFire;

	public long getLastFire() {
		return lastFire;
	}

	public void setLastFire(long lastFire) {
		this.lastFire = lastFire;
	}
	
	private int pet;

	public void setPet(int pet) {
		this.pet = pet;
	}

	public int getPet() {
		return pet;
	}

	@Override
	public int clientIndex() {
		return 32768 + this.getIndex();
	}
	
	private GameModeSelection select_game_mode = new GameModeSelection();

	public GameModeSelection getGameModeSelection() {
		return select_game_mode;
	}
	
	//Minigame variables
	public boolean isAnimatedArmourSpawned;
	public int waveId;
	public boolean secondOption;
	
	private FightCaves fightcave = null;
	
	public FightCaves getFightCave() {
		if (fightcave == null)
			fightcave = new FightCaves(this);
		return fightcave;
	}
	
    private boolean completedFightCaves;
	
	public boolean hasCompletedFightCaves() {
		return completedFightCaves;
	}

	public void setCompletedFightCaves() {
		if(!completedFightCaves) {
			completedFightCaves = true;
		}
	}
	
	private WarriorsGuild warriorsGuild = new WarriorsGuild(this);

	public WarriorsGuild getWarriorsGuild() {
		return warriorsGuild;
	}
	
	/**
	 * The progress bar.
	 */
	private int progressBar;

	public int getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(int progressBar) {
		this.progressBar = progressBar;
	}
	
	private boolean preserveUnlocked;

	public boolean isPreserveUnlocked() {
		return preserveUnlocked;
	}

	public void setPreserveUnlocked(boolean preserveUnlocked) {
		this.preserveUnlocked = preserveUnlocked;
	}
	
	private boolean rigourUnlocked;

	public boolean isRigourUnlocked() {
		return rigourUnlocked;
	}

	public void setRigourUnlocked(boolean rigourUnlocked) {
		this.rigourUnlocked = rigourUnlocked;
	}
	
	private boolean auguryUnlocked;

	public boolean isAuguryUnlocked() {
		return auguryUnlocked;
	}

	public void setAuguryUnlocked(boolean auguryUnlocked) {
		this.auguryUnlocked = auguryUnlocked;
	}
	
	/**
	 * @return the standAnimation
	 */
	public int getStandAnimation() {
		return standAnimation;
	}

	/**
	 * @param standAnimation
	 *            the standAnimation to set
	 */
	public void setStandAnimation(int standAnimation) {
		this.standAnimation = standAnimation;
	}

	/**
	 * @return the runAnimation
	 */
	public int getRunAnimation() {
		return runAnimation;
	}

	/**
	 * @param runAnimation
	 *            the runAnimation to set
	 */
	public void setRunAnimation(int runAnimation) {
		this.runAnimation = runAnimation;
	}

	/**
	 * @return the walkAnimation
	 */
	public int getWalkAnimation() {
		return walkAnimation;
	}

	/**
	 * @param walkAnimation
	 *            the walkAnimation to set
	 */
	public void setWalkAnimation(int walkAnimation) {
		this.walkAnimation = walkAnimation;
	}

	/**
	 * @return the standTurnAnimation
	 */
	public int getStandTurnAnimation() {
		return standTurnAnimation;
	}

	/**
	 * @param standTurnAnimation
	 *            the standTurnAnimation to set
	 */
	public void setStandTurnAnimation(int standTurnAnimation) {
		this.standTurnAnimation = standTurnAnimation;
	}

	/**
	 * @return the turn90ClockwiseAnimation
	 */
	public int getTurn90ClockwiseAnimation() {
		return turn90ClockwiseAnimation;
	}

	/**
	 * @param turn90ClockwiseAnimation
	 *            the turn90ClockwiseAnimation to set
	 */
	public void setTurn90ClockwiseAnimation(int turn90ClockwiseAnimation) {
		this.turn90ClockwiseAnimation = turn90ClockwiseAnimation;
	}

	/**
	 * @return the turn90CounterClockwiseAnimation
	 */
	public int getTurn90CounterClockwiseAnimation() {
		return turn90CounterClockwiseAnimation;
	}

	/**
	 * @param turn90CounterClockwiseAnimation
	 *            the turn90CounterClockwiseAnimation to set
	 */
	public void setTurn90CounterClockwiseAnimation(int turn90CounterClockwiseAnimation) {
		this.turn90CounterClockwiseAnimation = turn90CounterClockwiseAnimation;
	}

	/**
	 * @return the turn180Animation
	 */
	public int getTurn180Animation() {
		return turn180Animation;
	}

	/**
	 * @param turn180Animation
	 *            the turn180Animation to set
	 */
	public void setTurn180Animation(int turn180Animation) {
		this.turn180Animation = turn180Animation;
	}
	
	/**
	 * The stand animation.
	 */
	private int standAnimation = Animation.create(808).getId();

	/**
	 * The run animation.
	 */
	private int runAnimation = Animation.create(824).getId();

	/**
	 * The walk animation.
	 */
	private int walkAnimation = Animation.create(819).getId();

	/**
	 * The stand-turn animation.
	 */
	private int standTurnAnimation = Animation.create(823).getId();

	/**
	 * The turn 90 clockwise animation.
	 */
	private int turn90ClockwiseAnimation = Animation.create(821).getId();

	/**
	 * The turn 90 counter clockwise animation.
	 */
	private int turn90CounterClockwiseAnimation = Animation.create(822).getId();

	/**
	 * The turn 180 animation.
	 */
	private int turn180Animation = Animation.create(820).getId();
    
    DecimalFormat format = new DecimalFormat("##.##");

	public static double getRatio(int kills, int deaths) {
		double ratio = kills / Math.max(1D, deaths);
		return ratio;
	}

	public double getRatio(Player player) {
		return getRatio(player.getKillCount(), player.getDeathCount());
	}

	public String displayRatio(Player player) {
		return format.format(getRatio(player));
	}

	public void resetAutoCast() {
        autocastId = 0;
        onAuto = false;
        autoCast = false;
        getActionSender().sendConfig(108, 0);
    }

	/**
     * Gets the formatted version of the username for this player.
     *
     * @return the formatted username.
     */
    public String getFormatUsername() {
        return Utility.capitalize(username);
    }
    
	private boolean dropWarning = true;
	
	/**
	 * Determines whether a warning will be shown when dropping an item.
	 * 
	 * @return True if it's the case, False otherwise.
	 */
	public boolean showDropWarning() {
		return dropWarning;
	}

	/**
	 * Change whether a warning will be shown when dropping items.
	 * 
	 * @param shown
	 *            True in case a warning must be shown, False otherwise.
	 */
	public void setDropWarning(boolean shown) {
		dropWarning = shown;
	}
    
	private long lastIncentive;
	
    public long getLastIncentive() {
		return lastIncentive;
	}

	public void setLastIncentive(long lastIncentive) {
		this.lastIncentive = lastIncentive;
	}
	
	private boolean incentiveWarning;

	public boolean receivedIncentiveWarning() {
		return this.incentiveWarning;
	}

	public void updateIncentiveWarning() {
		this.incentiveWarning = true;
	}
	
	private long timePlayed;
	
	public void setTimePlayed(long time) {
		this.timePlayed = time;
	}
	
	public long getTimePlayed() {
		return this.timePlayed;
	}
	
	public boolean didYouKnow;
	
	public void setDidYouKnow(boolean active) {
		this.didYouKnow = active;
	}
	
	public boolean trivia;
	
	public void setTrivia(boolean active) {
		this.trivia = active;
	}

	public int slayerSelectionHolder;
	
	public String slayerSelectString;
	
	public int ordinal;
	
	public int slayerNpcId;
	
	public int type;
	
	public void setSlayerSelection(int buttonId, int slayerNpcId, String name, int ordinal , int type) {
		this.slayerSelectionHolder = buttonId;
		this.slayerNpcId = slayerNpcId;
		this.slayerSelectString = name;
		this.ordinal = ordinal;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public String getSlayerSelectionName(){
		return slayerSelectString;
	}
	
	public int getSlayerSelection() {
		return slayerSelectionHolder;
	}
	
	public int getSlayerNpcId() {
		return slayerNpcId;
	}
	
	public int getSlayerOrdinal() {
		return ordinal;
	}
	
	SlayerInterface slayerInterface = new SlayerInterface();

	public int[] tasksBlocked = new int[6];

	public int[] getBlockedTask() {
		return tasksBlocked;
	}
	
	public int getBlockedTaskIndex(int index) {
		return tasksBlocked[index];
	}
	
	public void blockTask(int index, int value) {
		this.tasksBlocked[index] = value;
	}
	
	public SlayerInterface getSlayerInterface() {
		return slayerInterface;
	}
	
	/**
	 * Represents slayer streak record
	 */
	private int slayerStreak;
	
	public int getSlayerStreak() {
		return slayerStreak;
	}
	
	public void setSlayerStreak(int slayerStreak) {
		this.slayerStreak = slayerStreak;
	}
	/**
	 * Represents slayer streak record
	 */
	private int slayerStreakRecord;
	
	public int getSlayerStreakRecord() {
		return slayerStreakRecord;
	}
	
	public void setSlayerStreakRecord(int slayerStreakRecord) {
		this.slayerStreakRecord = slayerStreakRecord;
	}

	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);
	
	/**
	 * Gets the interface state.
	 * 
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}
	
	public void removeInterfaceAttributes() {
		setShopping(false);
		setBanking(false);
		dialogue().interrupt();
	}
	
	private final PriceChecker priceChecker = new PriceChecker(this);
	
	public PriceChecker getPriceChecker() {
		return priceChecker;
	}
	
	private int shopId;

	public int getShopId() {
		return shopId;
	}
	
	public void setShopId(final int shopId) {
		this.shopId = shopId;
	}

}
