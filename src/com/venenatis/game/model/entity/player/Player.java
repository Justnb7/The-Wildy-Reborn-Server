package com.venenatis.game.model.entity.player;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.FriendAndIgnoreList;
import com.venenatis.game.content.HerbSack;
import com.venenatis.game.content.KillTracker;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.content.activity.trade.TradeContainer;
import com.venenatis.game.content.activity.trade.TradeSession;
import com.venenatis.game.content.activity.trade.TradeSession.TradeStage;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelContainer;
import com.venenatis.game.content.presets.PreloadingGear;
import com.venenatis.game.content.server_tasks.Tasks;
import com.venenatis.game.content.skills.SkillTask;
import com.venenatis.game.content.skills.farm.Farming;
import com.venenatis.game.content.skills.farming.Allotments;
import com.venenatis.game.content.skills.farming.FarmingVencillio;
import com.venenatis.game.content.skills.fishing.Fishing;
import com.venenatis.game.content.skills.herblore.Herblore;
import com.venenatis.game.content.skills.slayer.interfaceController.SlayerInterface;
import com.venenatis.game.content.skills.thieving.Thieving;
import com.venenatis.game.content.teleportation.Teleport;
import com.venenatis.game.content.teleportation.TeleportHandler.TeleportationTypes;
import com.venenatis.game.content.titles.Titles;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.InterfaceState;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.Skills.SkillCape;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.QuickPrayers;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.data.WeaponInterface;
import com.venenatis.game.model.combat.magic.Magic;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.combat.npcs.impl.zulrah.Zulrah;
import com.venenatis.game.model.combat.npcs.impl.zulrah.ZulrahLostItems;
import com.venenatis.game.model.container.impl.bank.BankContainer;
import com.venenatis.game.model.container.impl.equipment.EquipmentContainer;
import com.venenatis.game.model.container.impl.inventory.InventoryContainer;
import com.venenatis.game.model.container.impl.price_checker.PriceChecker;
import com.venenatis.game.model.container.impl.rune_pouch.RunePouchContainer;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.following.PlayerFollowing;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.NPCAggression;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.account.widget.Selection;
import com.venenatis.game.model.entity.player.clan.Clan;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.model.entity.player.controller.ControllerManager;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;
import com.venenatis.game.model.entity.player.dialogue.input.InputAmount;
import com.venenatis.game.model.entity.player.dialogue.input.InputString;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.instance.impl.FightCaveInstance;
import com.venenatis.game.model.entity.player.instance.impl.KrakenInstance;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.model.masks.WalkingQueue;
import com.venenatis.game.model.req.RequestManager;
import com.venenatis.game.net.network.rsa.GameBuffer;
import com.venenatis.game.net.network.rsa.ISAACRandomGen;
import com.venenatis.game.net.network.session.GameSession;
import com.venenatis.game.net.packet.ActionSender;
import com.venenatis.game.net.packet.Packet;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.DeathTask;
import com.venenatis.game.task.impl.DistancedActionTask;
import com.venenatis.game.util.MutableNumber;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

import io.netty.buffer.Unpooled;

public class Player extends Entity {
	
	private KillTracker killTracker = new KillTracker(this);
	
	/**
	 * Returns the single instance of the {@link KillTracker} class for this player.
	 * 
	 * @return the tracker clas
	 */
	public KillTracker getKillTracker() {
		return killTracker;
	}
	
	
	public void stun(int stunTime, String string, boolean gfx) {

		if (gfx) {
			playGraphic(Graphic.STUNNED_GRAPHIC);
		}
		getActionSender().sendMessage(string);
		setAttribute("stunned", true);
		World.getWorld().schedule(new Task(stunTime) {

			@Override
			public void execute() {
				removeAttribute("stunned");
				this.stop();
			}

		});

	}

	public void stunInstantly(int stunTime, String string, boolean gfx) {
		if (gfx) {
			playGraphic(Graphic.STUNNED_GRAPHIC);
		}
		getActionSender().sendMessage(string);
		setAttribute("stunned", true);
	}
	
    private boolean completionist;
	
	public boolean Completionist() {
		return completionist;
	}
	
	public void setCompletionist(boolean completionist) {
		this.completionist = completionist;
	}
	
	private boolean rng_god;
	
	public boolean unlockedRngGod() {
		return rng_god;
	}
	
	public void unlockRngGod(boolean unlocked) {
		this.rng_god = unlocked;
	}
	
   private boolean lucky;
	
	public boolean lucky() {
		return lucky;
	}
	
	public void setLucky(boolean lucky) {
		this.lucky = lucky;
	}
	
    private HerbSack herbSack = new HerbSack(this);
	
	public HerbSack getHerbSack() {
		return herbSack;	
	}
	
	/**
	 * The dialogue manager instance
	 */
	private final DialogueManager dialogueManager = new DialogueManager(this);
	
	/**
	 * Gets the dialogue manager
	 * @return
	 */
	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}
	
	/**
	 * The player's head icon hint.
	 */
	private int headHint = -1;

	/**
	 * Gets the player's current head hint index.
	 * @return	The player's head hint.
	 */
	public int getHeadHint() {
		return headHint;
	}

	/**
	 * Sets the player's head icon hint.
	 * @param headHint	The hint index to use.
	 * @return			The Appearance instance.
	 */
	public void setHeadHint(int headHint) {
		this.headHint = headHint;
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
	
	private int otherPlayerDuelIndex = -1;
	
	public int getOtherPlayerDuelIndex() {
		return otherPlayerDuelIndex;
	}

	public void setOtherPlayerDuelIndex(int otherPlayerDuelIndex) {
		this.otherPlayerDuelIndex = otherPlayerDuelIndex;
	}
	
	private DuelArena duelArena = new DuelArena(this);

	public DuelArena getDuelArena() {
		return duelArena;
	}

	public void setDuelArena(DuelArena duelArena) {
		this.duelArena = duelArena;
	}
	
	private final DuelContainer duelContainer = new DuelContainer(this);

	public DuelContainer getDuelContainer() {
		return duelContainer;
	}
	
	public boolean isDueling() {
		return getDuelArena().isDueling();
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
	 * Represents the players current wilderness streak, (inside the wilderness)
	 */
	private int wilderness_streak;
	
	public int getWildernessKillStreak() {
		return wilderness_streak;
	}
	
	public void setWildernessStreak(int wilderness_streak) {
		this.wilderness_streak = wilderness_streak;
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
	 * A reward for completing achievements
	 */
	private int achievementsPoints;
	
	public int getAchievementsPoints() {
		return achievementsPoints;
	}
	
	public void setAchievementPoints(int points) {
		this.achievementsPoints = points;
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
	
	private boolean drainingPrayer;
	
	public boolean isDrainingPrayer() {
		return drainingPrayer;
	}

	public void setDrainingPrayer(boolean drainingPrayer) {
		this.drainingPrayer = drainingPrayer;
	}
	
	private double prayerPointDrain;

	public double getPrayerPointDrain() {
		return prayerPointDrain;
	}

	public void setPrayerPointDrain(double prayerPointDrain) {
		this.prayerPointDrain = prayerPointDrain;
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
	private int spellId = -1;
	
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
	 * The players autocast Id
	 */
	private int autocastId = -1;
	
	/**
	 * Sets the players autocast id
	 * 
	 * @param id
	 *            The id of the spell
	 */
	public void setAutocastId(int id) {
		this.autocastId = id;
	}

	/**
	 * Returns the players autocast id
	 * 
	 * @return
	 */
	public int getAutocastId() {
		return autocastId;
	}
	
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
	private SpellBook spellBook;
	
	/**
	 * Get the players current spellbook
	 * 
	 * @return the spellbook
	 */
	public SpellBook getSpellBook() {
		return spellBook;
	}
	
	/**
	 * Set the players spellbook
	 *
	 */
	public void setSpellBook(SpellBook spellBook) {
		this.spellBook = spellBook;
	}
	
	private Teleport teleporting_action = new Teleport(this);
	
	public Teleport getTeleportAction() {
		return teleporting_action;
	}
	
	public void setTeleportingAction(Teleport teleport) {
		this.teleporting_action = teleport;
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
	private String yellColor = "";
	
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
	
	public boolean isInMinigame() {
		return Area.inDuelArena(this) || this.getDuelArena().isDueling() || Area.inBarrows(this) || DuelArena.inArena(this) || DuelArena.inObstacleArena(this) || MinigameHandler.search(this).isPresent();
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
	public boolean canDuel() {
		return getDuelArena().getStage() == DuelStage.REQUEST;
	}
	
	@Override
	public int getCombatCooldownDelay() {
		return CombatFormulae.getCombatCooldownDelay(this);
	}
	
	@Override
	public void onDeath() {
		MinigameHandler.execute(this, $it -> $it.onDeath(this));
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
	
    private int suffering = 0;
	
	public int getROSuffering() {
		return suffering;
	}

	public void setROSuffering(int suffering) {
		this.suffering = suffering;
	}

	// example {magicId, level req, animation, startGFX, projectile Id, endGFX, maxhit, exp gained,
	// rune 1, rune 1 amount,
	// rune 2, rune 2 amount,
	// rune 3, rune 3 amount,
	// rune 4, rune 4 amount}
	// theres another param on end idk what it is [16]
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

			{ 12445, 85, 1819, 0, 1299, -1, 0, 65, 563, 1, 562, 1, 560, 1, 0, 0, 0 }, // teleblock

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
			{ 12881, 70, 1979, 0, 0, 363, 22, 40, 560, 2, 562, 4, 555, 4, 0, 0, 0 }, // ice
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
			// example {magicId, level req, animation, startGFX, projectile Id, endGFX, maxhit, exp gained,
			// rune 1, rune 1 amount,
			// rune 2, rune 2 amount,
			// rune 3, rune 3 amount,
			// rune 4, rune 4 amount}
			

			// blast
			// water
			// blast

	};

	public boolean withinDistance(Player otherPlr) {
		if (getZ() != otherPlr.getZ()) {
			return false;
		}
		int deltaX = otherPlr.getX() - getX(), deltaY = otherPlr.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(NPC npc) {
		if (getZ() != npc.getZ()) {
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
		setMapRegionChanging(false);
		entityFaceIndex = 65535;
		setUpdateBlock(null);
		this.getUpdateFlags().primary = null;
		this.getUpdateFlags().secondary = null;
		this.getUpdateFlags().reset();
		this.setForceWalk(new int[0], false);
	}
	
	private WalkingQueue walkingQueue = new WalkingQueue(this);

	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
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
		getCombatState().setInCombat(true);
	}

	public String getIdentity() {
		return identity;
	}

	public String setIdentity(String identity) {
		return this.identity = identity;
	}

	public Player(String username) {
		super(EntityType.PLAYER);
		setMapRegionChanging(true);
		this.username = username;
		usernameHash = Utility.playerNameToInt64(username);
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		getWalkingQueue().reset();
		getAttributes().put("can_take_damage", true);
		outStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		outStream.offset = 0;
		inStream = new GameBuffer(new byte[Constants.BUFFER_SIZE]);
		inStream.offset = 0;
	}

	@Override
	public boolean moving() {
		return walkingQueue.isMoving();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void message(String s) {
		getActionSender().sendMessage(s);
	}

	@Override
	public Hit decrementHP(Hit hit) {
		int damage = hit.getDamage(); 
		if (this.getSkills().getLevel(3) - damage <= 0) {
			damage = this.getSkills().getLevel(3);
			//System.out.println("["+this.getName()+"] dmg was over current hp ("+getSkills().getLevel(3)+"), adjusted to "+damage);
		}
		
		//System.out.println("you're defo using the right method btw "+damage+" vs "+this.getSkills().getLevel(3));
		if (!this.getAttribute("infhp", false))
			this.getSkills().setLevel(3, this.getSkills().getLevel(3) - damage);

		/*
		 * Check if our player has died, if so start the death task
		 * 
		 */
		if (getSkills().getLevel(3) < 1 && !getCombatState().isDead()) {
			getCombatState().setDead(true);
			this.setAttribute("killer", hit.source); // who killed us (did the final blow) - NOT who did most damage. These are 2 distinct things.
			World.getWorld().schedule(new Task(3) {
				@Override
				public void execute() {
					playAnimation(Animation.create(0x900));
					this.stop();
				}
			});
			World.getWorld().schedule(new DeathTask(this, 8));
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

	/**
	 * Try to logout. Prompted by messages if not allowed, such as in a stake.
	 */
	public void logout() {
		
		if (isDueling() || getDuelArena().isInSession()) {
			getActionSender().sendMessage("You cannot logout while in duel arena.");
			return;
		}
		
		/*
		 * Remove from zulrah instance
		 */
		if (zulrah.getInstancedZulrah() != null) {
			InstancedAreaManager.getSingleton().disposeOf(zulrah.getInstancedZulrah());
		}
		
		/*
		 * Remove from kraken instance
		 */
		if (kraken != null && kraken.getInstance() != null)
			InstancedAreaManager.getSingleton().disposeOf(kraken.getInstance());
		
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
		farming.farmingProcess();
		World.getWorld().getEventManager().process();
		//long startTime = System.currentTimeMillis();
		
		if (this.getTimePlayed() < Integer.MAX_VALUE) {
			this.setTimePlayed(this.getTimePlayed() + 1);
		}

		// Follow player - not combat
		process_following();

		combatProcessing();

		if (controller != null) {
			controller.process(this);
		}

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
		
		if (getCombatState().getEatDelay() > 0) {
			getCombatState().decreaseEatDelay(1);
		}
		//long endTime = System.currentTimeMillis() - startTime; System.out.println("[process] end time: "+endTime + " : players online: " + World.getWorld().getPlayers().size());
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
			getCombatState().setInCombat(false);
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
		
		if (getCombatState().getSpellDelay() > 0) {
			getCombatState().decreaseSpellDelay(1);
		}

		// Handles following every game tick, only attacks when delay=0
		Combat.playerVsEntity(this);
	}

	public int getChunckX() {
		return (getX() >> 6);
	}

	public int getChunckY() {
		return (getY() >> 6);
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
		return !inCombat && !getCombatState().isDead();
	}

	public void setXLogDelay(long delay) {
		this.xlogDelay = delay;
	}

	public String getUsername() {
		return Utility.formatName(username);
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
		return "Player [username=" + getUsername() + ", index: " + getIndex() + "]";
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * A list of running tasks for this player
	 */
	private List<Task> runningTasks = new LinkedList<>();
	
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
	public void setDistancedTask(Task task) {
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
	public boolean setController(final Controller controller) {
		this.controller = controller;
		controller.onStartup(this);
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

	public List<Task> getTasks() {
		return runningTasks;
	}
	
	private String lastSlayerTask = "";

	public String getLastSlayerTask() {
		return lastSlayerTask;
	}

	public void setLastSlayerTask(String lastSlayerTask) {
		this.lastSlayerTask = lastSlayerTask;
	}
	
	private String slayerTask = "";
	
	public String getSlayerTask() {
		return slayerTask;
	}
	
	public void setSlayerTask(String task) {
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
	
	public long lastCast;

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

	public int getSessionExperience() {
		return sessionExperience;
	}

	/**
	 * End of constructors
	 */
	
	/**
	 * Instances
	 */
	private FriendAndIgnoreList friendAndIgnores = new FriendAndIgnoreList(this);
	private RequestManager requestManager = new RequestManager(this);
	private Task distancedTask;
	private SkillTask skillTask;
	private int sessionExperience;
	
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
	
	private String hostAddress = "";
	
	public String getHostAddress() {
		return hostAddress;
	}
	
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
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
	
	private boolean enableSound = false;
	
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
	
	private boolean dataOrbs;

	public void setDataOrbs(boolean dataOrbs) {
		this.dataOrbs = dataOrbs;
	}

	public boolean getDataOrbs() {
		return dataOrbs;
	}
	
	private boolean roofs;

	public void setRoofsToggled(boolean roofs) {
		this.roofs = roofs;
	}

	public boolean getRoofsToggled() {
		return roofs;
	}
	
	private boolean leftClickAttack;

	public void setLeftClickAttack(boolean leftClickAttack) {
		this.leftClickAttack = leftClickAttack;
	}

	public boolean getLeftClickAttack() {
		return leftClickAttack;
	}
	
	private boolean gameTimers;

	public void setGameTimers(boolean gameTimers) {
		this.gameTimers = acceptAid;
	}

	public boolean getGameTimers() {
		return gameTimers;
	}

	private boolean targetTracking;

	public void setTargetTracking(boolean targetTracking) {
		this.targetTracking = targetTracking;
	}

	public boolean toggleTargetTracking() {
		return targetTracking;
	}
	
	private boolean groundItems;

	public void setGroundItems(boolean groundItems) {
		this.groundItems = groundItems;
	}

	public boolean toggleGroundItems() {
		return groundItems;
	}
	
	private boolean shiftDrop;

	public void setShiftDrops(boolean shiftDrop) {
		this.shiftDrop = shiftDrop;
	}

	public boolean toggleShiftClick() {
		return shiftDrop;
	}
	
    private final WeaponInterface weaponInterface = new WeaponInterface(this);
	
	public WeaponInterface getWeaponInterface() {
		return weaponInterface;
	}

	/**
	 * Handle the instanced floor reset
	 */
	public void instanceFloorReset() {
		if (kraken != null) {
			if (!Boundary.isIn(this, Boundary.KRAKEN)) {
				System.out.println("Resetting kraken instance for: " + this.getUsername());
				if (kraken.getInstance() != null)
					InstancedAreaManager.getSingleton().disposeOf(kraken.getInstance());
			}
		} else if (fight_cave != null) {
			if (!Boundary.isIn(this, Boundary.FIGHT_CAVE)) {
				System.out.println("Resetting fight_cave instance for: " + this.getUsername());
				if (fight_cave.getInstance() != null)
					InstancedAreaManager.getSingleton().disposeOf(fight_cave.getInstance());
			}
		}
	}
	
	private KrakenInstance kraken;
	
	public KrakenInstance getKraken() {
		if (kraken == null)
			kraken = new KrakenInstance();
		return kraken;
	}
	
    private FightCaveInstance fight_cave;
	
	public FightCaveInstance getFightCave() {
		if (fight_cave == null)
			fight_cave = new FightCaveInstance();
		return fight_cave;
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
		Server.getTaskScheduler().schedule(new Task(ticks) {
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
	
	/**
	 * We can't perform actions while the other person is busy.
	 */
	public boolean isBusy() {
		if(getTeleportAction().isTeleporting() || isShopping() || isTrading()) {
			return true;
		}
		return false;
	}
	
	private int destroyItem = -1;

	public int getDestroyItem() {
		return destroyItem;
	}

	public void setDestroyItem(int destroyItem) {
		this.destroyItem = destroyItem;
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
	
	private int wildLevel;
	
	public int getWildLevel() {
		return wildLevel;
	}

	public void setWildLevel(int wildLevel) {
		this.wildLevel = wildLevel;
	}
	
    /**
     * Integers
     */
	private int chatTextColor = 0;
	private int chatTextEffects = 0;
	public int lastClickedItem;

	public int lastChatId = 1, privateChat, specBarId,
			xInterfaceId, xRemoveId, xRemoveSlot, frozenBy, walkTutorial = 15, bountyPoints;
	
	/**
	 * Booleans
	 */
	public boolean dropListSorted;
	public boolean dropRateInKills;
	public boolean properLogout;
	
	/**
	 * Strings
	 */
	private String username = null;
	private String password = "";
	private String identity;
	public String loyaltyTitle = "";
	
    /**
     * Longs
     */
	public long usernameHash;
	public long lastDropTableSelected;
	public long lastDropTableSearch;

	/**
	 * Bytes
	 */
	private byte chatText[] = new byte[4096];
	private byte chatTextSize = 0;
	
	/**
	 * Timers
	 */
	private Stopwatch lastCombatAction = new Stopwatch();
	public Stopwatch aggressionTolerance = new Stopwatch();
	public Stopwatch logoutDelay = new Stopwatch();
	public Stopwatch cannotUsePrayer = new Stopwatch();
	
	public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();

	public void debug(String string) {
		if (this.rights == Rights.OWNER && this.inDebugMode()) {
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
	
	private int pet = -1;

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
	
	private Selection select_game_mode = new Selection();

	public Selection getGameModeSelection() {
		return select_game_mode;
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
	
	private boolean didYouKnow;
	
	public boolean is_did_you_know_activated() {
		return didYouKnow;
	}
	
	public void setDidYouKnow(boolean active) {
		this.didYouKnow = active;
	}
	
	private boolean trivia;
	
	public boolean is_trivia_activated() {
		return trivia;
	}
	
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
		getDialogueManager().interrupt();
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
	
	private boolean newPlayer;
	
	public boolean isNewPlayer() {
		return newPlayer;
	}
	
	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}
	
	private Clan clan;

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}
	
	private String clanChat;
	
	public String getClanChat() {
		return clanChat;
	}

	public void setClanChat(String clanChat) {
		this.clanChat = clanChat;
	}
	
	private String savedClan;

	public String getSavedClan() {
		return savedClan;
	}

	public void setSavedClan(String savedClan) {
		this.savedClan = savedClan;
	}
	
	private String clanPromote;

	public String getClanPromote() {
		return clanPromote;
	}

	public void setClanPromote(String clanPromote) {
		this.clanPromote = clanPromote;
	}
	
	private InputString inputString;

	public void setInputAmount(InputAmount inputAmount) {
		this.inputAmount = inputAmount;
	}
	
	public InputAmount getInputAmount() {
		return inputAmount;
	}
	
	private InputAmount inputAmount;

	public void setInputString(InputString inputString) {
		this.inputString = inputString;
	}
	
	public InputString getInputString() {
		return inputString;
	}
	
	private int runEnergy;
	
	public int getRunEnergy() {
		return runEnergy;
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
	}
	
	private int runRestore;

	public int getRunRestore() {
		return runRestore;
	}

	public void setRunRestore(int runRestore) {
		this.runRestore = runRestore;
	}
	
	private int dfsWaitTimer;
	
	public int getDfsTimer() {
		return dfsWaitTimer;
	}

	public void setDfsTimer(int time) {
		this.dfsWaitTimer = time;
	}

	/**
	 * Determines if this player can keep their items upon death.
	 *
	 * @return {@code true} If they can keep their items on death. {@code false}
	 *         If they can not.
	 */
	public boolean canKeepItems() {
		if (MinigameHandler.execute(this, false, $it -> $it.canKeepItems())) {
			return true;
		} else {
			return false;
		}
	}
	
	private PreloadingGear presets = new PreloadingGear(this);

	public PreloadingGear getPresets() {
		return presets;
	}

	private int gear_points;
	
	public int getGearPoints() {
		return gear_points;
	}
	
	public void setGearPoints(int points) {
		this.gear_points = points;
	}

	private int vigour = 0;
	
	public int getVigour() {
		return vigour;
	}
	
	public void setVigour(int vigour) {
		this.vigour = vigour;
	}
	
	private int slayer_action = 0;
	
	public int getSlayerAction() {
		return slayer_action;
	}
	
	public void setSlayerAction(int slayer_action) {
		this.slayer_action = slayer_action;
	}

	public int getToxicBlowpipeCharge() {
		return 16383;
	}

	public int getToxicBlowpipeAmmo() {
		return 11230;
	}
	
	private boolean muted;
	
	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean m) {
		this.muted = m;
	}

	public void faceObject(GameObject object) {
		if (object != null) {
			if (object.getId() == 11374) {
				face(Location.create(2713, 3494));
				return;
			} else if (object.getId() == 11375) {
				face(object.getLocation());
				return;
			} else if (object.getId() == 11377) {
				face(Location.create(2704, 3464));
				return;
			}
			if (getLocation().equals(object.getLocation())) {
				int offX = 0;
				int offY = 0;
				switch (object.getDirection()) {
				case 0:
					offX = -1;
					break;
				case 1:
					offY = 1;
					break;
				case 2:
					offX = 1;
					break;
				case 3:
					offY = -1;
					break;
				}
				face(object.getLocation().transform(offX, offY, 0));
			} else if (object.getType() >= 9 && object.getType() <= 11
					&& (object.getDefinition().xLength() > 1 || object.getDefinition().yLength() > 1)) {
				face(object.getLocation().transform(object.getDefinition().xLength() >> 1,
						object.getDefinition().yLength() >> 1, 0));
			} else {
				face(object.getLocation());
			}
		}
	}
	
	private HashMap<Tasks, Integer> playerTask = new HashMap<Tasks, Integer>(Tasks.values().length) {
		private static final long serialVersionUID = 1842952445111093360L;

		{
			for (final Tasks task : Tasks.values()) {
				put(task, 0);
			}
		}
	};
	
	public HashMap<Tasks, Integer> getPlayerTask() {
		return playerTask;
	}
	
	public void setPlayerTask(HashMap<Tasks, Integer> playerTask) {
		this.playerTask = playerTask;
	}
	
	private HashMap<AchievementList, Integer> playerAchievements = new HashMap<AchievementList, Integer>(AchievementList.values().length) {
		private static final long serialVersionUID = 1842952445111093360L;

		{
			for (final AchievementList achievement : AchievementList.values()) {
				put(achievement, 0);
			}
		}
	};

	public HashMap<AchievementList, Integer> getPlayerAchievements() {
		return playerAchievements;
	}
	
	public void setPlayerAchievements(HashMap<AchievementList, Integer> playerAchievements) {
		this.playerAchievements = playerAchievements;
	}
	
	public int achievementsCompleted() {
		int completed = 0;
		for (final AchievementList achievement : this.getPlayerAchievements().keySet()) {
			if (achievement != null && this.getPlayerAchievements().get(achievement) == achievement.getCompleteAmount()) {
				completed++;
			}
		}
		return completed;
	}

	public boolean completedAchievements() {
		return achievementsCompleted() >= AchievementList.getTotal() ? true : false;
	}
	
	private Deque<String> lastKilledPlayers = new ArrayDeque<>(3);

	/**
	 * @return the lastKilledPlayers
	 */
	public Deque<String> getLastKilledPlayers() {
		return lastKilledPlayers;
	}

	/**
	 * @param lastKilledPlayers
	 *            the lastKilledPlayers to set
	 */
	public void setLastKilledPlayers(Deque<String> lastKilledPlayers) {
		this.lastKilledPlayers = lastKilledPlayers;
	}
	
	private Fishing fishing = new Fishing(this);

	public Fishing getFishing() {
		return fishing;
	}
	
	private int[] farmingSeedId = new int[Farming.MAX_PATCHES], farmingTime = new int[Farming.MAX_PATCHES],
			farmingState = new int[Farming.MAX_PATCHES], farmingHarvest = new int[Farming.MAX_PATCHES];
	
	public int getFarmingSeedId(int index) {
		return farmingSeedId[index];
	}

	public void setFarmingSeedId(int index, int farmingSeedId) {
		this.farmingSeedId[index] = farmingSeedId;
	}

	public int getFarmingTime(int index) {
		return this.farmingTime[index];
	}

	public void setFarmingTime(int index, int farmingTime) {
		this.farmingTime[index] = farmingTime;
	}

	public int getFarmingState(int index) {
		return farmingState[index];
	}

	public void setFarmingState(int index, int farmingState) {
		this.farmingState[index] = farmingState;
	}

	public int getFarmingHarvest(int index) {
		return farmingHarvest[index];
	}

	public void setFarmingHarvest(int index, int farmingHarvest) {
		this.farmingHarvest[index] = farmingHarvest;
	}
	
	private Farming farming = new Farming(this);

	public Farming farming() {
		if (farming == null) {
			farming = new Farming(this);
		}
		return farming;
	}
	
	private FarmingVencillio farmingVencillio = new FarmingVencillio(this);

	public FarmingVencillio getFarming() {
		return farmingVencillio;
	}

	public void setFarming(Farming farming) {
		this.farming = farming;
	}
	
	private Allotments allotments = new Allotments(this);
	
	public Allotments getAllotment() {
		return allotments;
	}
	
	private boolean showDamage;

	public boolean showDamage() {
		return showDamage;
	}
	
	public void setShowDamage(boolean show) {
		this.showDamage = show;
	}
	
	private boolean jailed;
	
	public boolean isJailed() {
		return jailed;
	}

	public void setJailed(boolean jailed) {
		this.jailed = jailed;
	}
	
	public boolean alreadyHasPet(Player player, int item) {
		final boolean inventory = player.getInventory().containsAny(item);
		final boolean bank = player.getBank().containsAny(item);
		final boolean equipment = player.getEquipment().containsAny(item);
		return inventory || bank || equipment;
	}
	
	private Magic magic = new Magic(this);

	public Magic getMagic() {
		return magic;
	}
	
	public void setMagic(Magic magic) {
		this.magic = magic;
	}

	private int venomDamage;

	public int getVenomDamage() {
		return venomDamage;
	}

	public void setVenomDamage(int venomDamage) {
		this.venomDamage = venomDamage;
	}
	
	private final QuickPrayers quickPrayers = new QuickPrayers(this);
	
	public QuickPrayers getQuickPrayers() {
		return quickPrayers;
	}
	
	/**
	 * Checks the players containers for untrimmed skillcapes.
	 */
	public void checkForSkillcapes() {
		int has99 = 0;
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			if(getSkills().getLevelForExperience(i) >= 99) {
				has99++;
				//getActionSender().sendConfig(313, 511); //Activates skillcape icon.
				if(has99 >= 2) {
					break;
				}
			}
		}
		if(has99 < 2) {
			return;
		}
		for(Item item : getInventory().toArray()) {
			if(item == null) {
				continue;
			}
			SkillCape cape = SkillCape.forUntrimmedId(item);
			if(cape != null && cape.getCapeTrim() != null) {//tells us that this item is an untrimmed cape.
				getInventory().remove(item);
				getInventory().add(new Item(cape.getCapeTrim().getId(), item.getAmount()));//make sure that we add the same amount of capes we deleted.
			}
		}
		for(Item item : getBank().toArray()) {
			if(item == null) {
				continue;
			}
			SkillCape cape = SkillCape.forUntrimmedId(item);
			if(cape != null) {//tells us that this item is an untrimmed cape.
				getBank().remove(item);
				getBank().add(new Item(cape.getCapeTrim().getId(), item.getAmount()));//make sure that we add the same amount of capes we deleted.
			}
		}
	}

	/**
	 * The mob's emote flag.
	 */
	private boolean emote = true;
	
	/**
	 * Gets the emote flag.
	 * @return The emote flag.
	 */
	public boolean canEmote() {
		return emote;
	}

	/**
	 * Sets the emote flag.
	 * @param animate The emote flag to set.
	 */
	public void setEmote(boolean emote) {
		this.emote = emote;
	}
	
	private long bestZulrahTime;

	public long setBestZulrahTime(long bestZulrahTime) {
		return this.bestZulrahTime = bestZulrahTime;
	}

	public long getBestZulrahTime() {
		return bestZulrahTime;
	}
	
	private Zulrah zulrah = new Zulrah(this);
	
	/**
	 * The zulrah event
	 * 
	 * @return event
	 */
	public Zulrah getZulrahEvent() {
		return zulrah;
	}

	private Titles titles = new Titles(this);
	
	/**
	 * Returns a single instance of the Titles class for this player
	 * 
	 * @return the titles class
	 */
	public Titles getTitles() {
		if (titles == null) {
			titles = new Titles(this);
		}
		return titles;
	}
	
	private boolean yellMuted;

	public boolean isYellMuted() {
		return yellMuted;
	}
	
	public void setYellMuted(boolean muted) {
		this.yellMuted = muted;
	}
	
	private String yellTag = "";
	
	public String getYellTag() {
		return yellTag;
	}
	
	public void setYellTag(String tag) {
		this.yellTag = tag;
	}
	
	private ZulrahLostItems lostItemsZulrah;

	public ZulrahLostItems getZulrahLostItems() {
		if (lostItemsZulrah == null) {
			lostItemsZulrah = new ZulrahLostItems(this);
		}
		return lostItemsZulrah;
	}
}
