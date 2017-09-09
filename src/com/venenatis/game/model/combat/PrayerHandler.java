package com.venenatis.game.model.combat;

import java.util.HashMap;

import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 * 
 * @author Swiffy
 */
public class PrayerHandler {

	/**
	 * Represents a prayer's configurations, such as their
	 * level requirement, buttonId, configId and drain rate.
	 * 
	 * @author relex lawl
	 */
	public enum PrayerData {
		THICK_SKIN(1, 1, 21233, 83),
		BURST_OF_STRENGTH(4, 1, 21234, 84),
		CLARITY_OF_THOUGHT(7, 1, 21235, 85),
		SHARP_EYE(8, 1, 77100, 700),
		MYSTIC_WILL(9, 1, 77102, 701),
		ROCK_SKIN(10, 2, 21236, 86),
		SUPERHUMAN_STRENGTH(13, 1.5, 21237, 87),
		IMPROVED_REFLEXES(16, 1.5, 21238, 88),
		RAPID_RESTORE(19, .4, 21239, 89),
		RAPID_HEAL(22, .6, 21240, 90),
		PROTECT_ITEM(25, .6, 21241, 91),
		HAWK_EYE(26, 1.5, 77104, 702),
		MYSTIC_LORE(27, 1.5, 77106, 703),
		STEEL_SKIN(28, 2.5, 21242, 92),
		ULTIMATE_STRENGTH(31, 2.8, 21243, 93),
		INCREDIBLE_REFLEXES(34, 2.8, 21244, 94),
		PROTECT_FROM_MAGIC(37, 2.8, 21245, 95, 2),
		PROTECT_FROM_MISSILES(40, 2.8, 21246, 96, 1),
		PROTECT_FROM_MELEE(43, 2.8, 21247, 97, 0),
		EAGLE_EYE(44, 3, 77109, 704),
		MYSTIC_MIGHT(45, 3, 77111, 705),
		RETRIBUTION(46, 1, 2171, 98, 4),
		REDEMPTION(49, 2, 2172, 99, 5),
		SMITE(52, 5, 2173, 100, 100, 6),
		PRESERVE(55, 1, 109097, 708),
		CHIVALRY(60, 5, 77113, 706),
		PIETY(70, 6, 77115, 707),
		RIGOUR(74, 6.4, 109100, 710),
		AUGURY(77, 6.1, 109103, 712);

		private PrayerData(int requirement, double drainRate, int buttonId, int configId, int... hint) {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			if (hint.length > 0)
				this.hint = hint[0];
		}

		/**
		 * The prayer's level requirement for player to be able
		 * to activate it.
		 */
		private int requirement;

		/**
		 * The prayer's action button id in prayer tab.
		 */
		private int buttonId;

		/**
		 * The prayer's config id to switch their glow on/off by 
		 * sending the sendConfig packet.
		 */
		private int configId;

		/**
		 * The prayer's drain rate as which it will drain
		 * the associated player's prayer points.
		 */
		private double drainRate;

		/**
		 * The prayer's head icon hint index.
		 */
		private int hint = -1;

		/**
		 * The prayer's formatted name.
		 */
		private String name;

		/**
		 * Gets the prayer's formatted name.
		 * @return	The prayer's name
		 */
		private final String getPrayerName() {
			if (name == null)
				return Utility.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			return name;
		}

		/**
		 * Contains the PrayerData with their corresponding prayerId.
		 */
		private static HashMap <Integer, PrayerData> prayerData = new HashMap <Integer, PrayerData> ();

		/**
		 * Contains the PrayerData with their corresponding buttonId.
		 */
		private static HashMap <Integer, PrayerData> actionButton = new HashMap <Integer, PrayerData> ();

		/**
		 * Populates the prayerId and buttonId maps.
		 */
		static {
			for (PrayerData pd : PrayerData.values()) {
				prayerData.put(pd.ordinal(), pd);
				actionButton.put(pd.buttonId, pd);
			}
		}
	}

	/**
	 * Gets the protecting prayer based on the argued combat type.
	 * 
	 * @param type
	 *            the combat type.
	 * @return the protecting prayer.
	 */
	public static int getProtectingPrayer(CombatStyle type) {
		switch (type) {
		case MELEE:
			return PROTECT_FROM_MELEE;
		case MAGIC:
			return PROTECT_FROM_MAGIC;
		case RANGE:
			return PROTECT_FROM_MISSILES;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean isActivated(Entity e, int prayer) {
		return e.getPrayerActive()[prayer];
	}

	/**
	 * Activates a prayer with specified <code>buttonId</code>.
	 * @param player	The player clicking on prayer button.
	 * @param buttonId	The button the player is clicking.
	 */
	public static boolean togglePrayer(Player player, final int buttonId) {
		PrayerData prayerData = PrayerData.actionButton.get(buttonId);
		if(prayerData != null) {
			if (!player.getPrayerActive()[prayerData.ordinal()]) {
				activatePrayer(player, prayerData.ordinal());
			} else {
				deactivatePrayer(player, prayerData.ordinal());
			}
			return true;
		}
		return false;
	}

	/**
	 * Activates said prayer with specified <code>prayerId</code> and de-activates
	 * all non-stackable prayers.
	 * @param e		The player activating prayer.
	 * @param prayerId		The id of the prayer being turned on, also known as the ordinal in the respective enum.
	 */
	public static void activatePrayer(Entity e, final int prayerId) {

		//Get the prayer data.
		PrayerData pd = PrayerData.prayerData.get(prayerId);

		//Check if it's availble
		if(pd == null) {
			return;
		}

		//Check if we're already praying this prayer.
		if (e.getPrayerActive()[prayerId]) {

			//If we are an npc, make sure our headicon
			//is up to speed.
			if(e.isNPC()) {
				NPC npc = e.asNpc();
				if (pd.hint != -1) {
					int hintId = getHeadHint(e);
					if(npc.getHeadIcon() != hintId) {
						npc.setHeadIcon(hintId);
					}
				}
			}

			return;
		}

		//If we're a player, make sure we can use this prayer.
		if(e.isPlayer()) {
			Player player = e.asPlayer();
			if (player.getSkills().getLevel(Skills.PRAYER) <= 0) {
				player.getActionSender().sendConfig(pd.configId, 0);
				player.getActionSender().sendMessage("You do not have enough Prayer points.");
				return;
			}
			if(!canUse(player, pd, true)) {
				return;
			}
		}

		switch (prayerId) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			resetPrayers(e, DEFENCE_PRAYERS, prayerId);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			resetPrayers(e, STRENGTH_PRAYERS, prayerId);
			resetPrayers(e, RANGED_PRAYERS, prayerId);
			resetPrayers(e, MAGIC_PRAYERS, prayerId);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			resetPrayers(e, ATTACK_PRAYERS, prayerId);
			resetPrayers(e, RANGED_PRAYERS, prayerId);
			resetPrayers(e, MAGIC_PRAYERS, prayerId);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			resetPrayers(e, STRENGTH_PRAYERS, prayerId);
			resetPrayers(e, ATTACK_PRAYERS, prayerId);
			resetPrayers(e, RANGED_PRAYERS, prayerId);
			resetPrayers(e, MAGIC_PRAYERS, prayerId);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			resetPrayers(e, DEFENCE_PRAYERS, prayerId);
			resetPrayers(e, STRENGTH_PRAYERS, prayerId);
			resetPrayers(e, ATTACK_PRAYERS, prayerId);
			resetPrayers(e, RANGED_PRAYERS, prayerId);
			resetPrayers(e, MAGIC_PRAYERS, prayerId);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			resetPrayers(e, OVERHEAD_PRAYERS, prayerId);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			resetPrayers(e, OVERHEAD_PRAYERS, prayerId);
			break;
		}
		e.setPrayerActive(prayerId, true);

		if(e.isPlayer()) {
			Player p = e.asPlayer();
			p.getActionSender().sendConfig(pd.configId, 1);
			if (hasNoPrayerOn(p, prayerId) && !p.isDrainingPrayer()) {
				startDrain(p);
			}
			if (pd.hint != -1) {
				int hintId = getHeadHint(e);
				p.setHeadHint(hintId);
				p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}

			p.getEquipment().setBonus();
		} else if(e.isNPC()) {

			NPC npc = e.asNpc();
			if (pd.hint != -1) {
				int hintId = getHeadHint(e);
				if(npc.getHeadIcon() != hintId) {
					npc.setHeadIcon(hintId);
				}
			}
		}
	}


	/**
	 * Checks if the player can use the specified prayer.
	 * @param player
	 * @param prayer
	 * @return
	 */
	public static boolean canUse(Player player, PrayerData prayer, boolean msg) {
		if (player.getSkills().getMaxLevel(Skills.PRAYER) < (prayer.requirement)) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.getActionSender().sendMessage("You need a Prayer level of at least " + prayer.requirement + " to use " + prayer.getPrayerName() + ".");
			}
			return false;
		}
		if (prayer == PrayerData.CHIVALRY && player.getSkills().getMaxLevel(Skills.DEFENCE) < 60) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.getActionSender().sendMessage("You need a Defence level of at least 60 to use Chivalry.");
			}
			return false;
		}
		if (prayer == PrayerData.PIETY && player.getSkills().getMaxLevel(Skills.DEFENCE) < 70) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.getActionSender().sendMessage("You need a Defence level of at least 70 to use Piety.");
			}
			return false;
		}
		if((prayer == PrayerData.RIGOUR || prayer == PrayerData.AUGURY) && player.getSkills().getMaxLevel(Skills.DEFENCE) < 70) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.getActionSender().sendMessage("You need a Defence level of at least 70 to use that prayer.");
			}
			return false;
		}
		if(prayer == PrayerData.PROTECT_ITEM) {
			if(player.isSkulled() && player.getSkullType() == SkullType.RED_SKULL) {
				if(msg) {
					player.getActionSender().sendConfig(prayer.configId, 0);
					SimpleDialogues.sendStatement(player, "You cannot use the Protect Item prayer with a red skull!");
				}
				return false;
			}
		}
		/*if(player.cannotUsePrayer.elapsed(200)) {
			if(prayer == PrayerData.PROTECT_FROM_MELEE || prayer == PrayerData.PROTECT_FROM_MISSILES || prayer == PrayerData.PROTECT_FROM_MAGIC) {
				if(msg) {
					player.getActionSender().sendConfig(prayer.configId, 0);
					player.getActionSender().sendMessage("You have been disabled and can no longer use protection prayers.");
				}
				return false;
			}
		}*/

		//Prayer locks
		boolean locked = false;

		if(prayer == PrayerData.PRESERVE && !player.isPreserveUnlocked()
				|| prayer == PrayerData.RIGOUR && !player.isRigourUnlocked()
				|| prayer == PrayerData.AUGURY && !player.isAuguryUnlocked()) {
			locked = true;
		}

		if(locked) {
			if(msg) {
				player.getActionSender().sendMessage("You have not unlocked that Prayer yet.");
			}
			return false;
		}

		//Duel, disabled prayer?
		if (player.getDuelArena().isDueling()) {
			if (player.getDuelArena().getRules().get(DuelRule.PRAYER)) {
				if(msg) {
					SimpleDialogues.sendStatement(player, "Prayer has been disabled in this duel!");
					player.getActionSender().sendConfig(prayer.configId, 0);
				}
				return false;
			}
		}

		return true;
	}

	/**
	 * Deactivates said prayer with specified <code>prayerId</code>.
	 * @param e		The player deactivating prayer.
	 * @param prayerId		The id of the prayer being deactivated.
	 */
	public static void deactivatePrayer(Entity e, int prayerId) {
		if (!e.getPrayerActive()[prayerId]) {
			return;
		}
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		e.getPrayerActive()[prayerId] = false;
		if(e.isPlayer()) {
			Player p = e.asPlayer();
			p.getActionSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(e);
				p.setHeadHint(hintId);
			}

			p.getQuickPrayers().checkActive();
			p.getEquipment().setBonus();
		} else if (e.isNPC()) {
			if (pd.hint != -1) {
				int hintId = getHeadHint(e);
				if(e.asNpc().getHeadIcon() != hintId) {
					e.asNpc().setHeadIcon(hintId);
				}
			}
		}		
		//	Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates every prayer in the player's prayer book.
	 * @param player	The player to deactivate prayers for.
	 */
	public static void deactivatePrayers(Entity e) {
		for (int i = 0; i < e.getPrayerActive().length; i++) {
			deactivatePrayer(e, i);
		}
		if(e.isPlayer()) {
			e.asPlayer().getQuickPrayers().setEnabled(false);
			//e.asPlayer().getActionSender().sendQuickPrayersState(false);
		} else if(e.isNPC()) {
			if(e.asNpc().getHeadIcon() != -1) {
				e.asNpc().setHeadIcon(-1);
			}
		}
	}
	
	public static void resetAll(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			PrayerData pd = PrayerData.prayerData.get(i);
			if(pd == null)
				continue;
			player.getPrayerActive()[i] = false;
			player.getActionSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(player);
				player.setHeadHint(hintId);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
		}
		player.getQuickPrayers().setEnabled(false);
		//TODO ask Jak why the packet size is incorrect
		//player.getActionSender().sendQuickPrayersState(false);
	}

	/**
	 * Gets the player's current head hint if they activate or deactivate
	 * a head prayer.
	 * @param player	The player to fetch head hint index for.
	 * @return			The player's current head hint index.
	 */
	private static int getHeadHint(Entity character) {
		boolean[] prayers = character.getPrayerActive();
		if (prayers[PROTECT_FROM_MELEE])
			return 0;
		if (prayers[PROTECT_FROM_MISSILES])
			return 1;
		if (prayers[PROTECT_FROM_MAGIC])
			return 2;
		if (prayers[RETRIBUTION])
			return 3;
		if (prayers[SMITE])
			return 4;
		if (prayers[REDEMPTION])
			return 5;
		return -1;
	}

	/**
	 * Initializes the player's prayer drain once a first prayer
	 * has been selected.
	 * @param player	The player to start prayer drain for.
	 */
	private static void startDrain(final Player player) {
		if (getDrain(player) <= 0 && !player.isDrainingPrayer())
			return;
		player.setDrainingPrayer(true);
		World.getWorld().schedule(new Task(1, false) {
			@Override
			public void execute() {

				if(player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
					this.stop();
					return;
				}

				double drainAmount = getDrain(player);

				if (drainAmount <= 0) {
					this.stop();
					return;
				}

				if (player.getPrayerPointDrain() < 0) {
					int total = (int) (player.getSkills().getLevel(Skills.PRAYER) - 1);
					player.getSkills().setLevel(Skills.PRAYER, total);
					player.setPrayerPointDrain(1.0);
				}

				if (player.getSkills().getLevel(Skills.PRAYER) <= 0) {
					deactivatePrayers(player);
					player.getActionSender().sendMessage("You have run out of Prayer points!");
					this.stop();
					return;
				}

				player.setPrayerPointDrain(player.getPrayerPointDrain() - drainAmount);

			}
			@Override
			public void stop() {
				player.setDrainingPrayer(false);
			}
		});
	}

	/**
	 * Gets the amount of prayer to drain for <code>player</code>.
	 * @param player	The player to get drain amount for.
	 * @return			The amount of prayer that will be drained from the player.
	 */
	private static final double getDrain(Player player) {
		double toRemove = 0.0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				PrayerData prayerData = PrayerData.prayerData.get(i);
				toRemove += prayerData.drainRate / 10;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.05 * player.getBonuses()[11]));		
		}
		return toRemove;
	}

	/**
	 * Checks if a player has no prayer on.
	 * @param player		The player to check prayer status for.
	 * @param exceptionId	The prayer id currently being turned on/activated.
	 * @return				if <code>true</code>, it means player has no prayer on besides <code>exceptionId</code>.
	 */
	private final static boolean hasNoPrayerOn(Player player, int exceptionId) {
		int prayersOn = 0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i] && i != exceptionId)
				prayersOn++;
		}
		return prayersOn == 0;
	}

	/**
	 * Resets <code> prayers </code> with an exception for <code> prayerID </code>
	 * 
	 * @param prayers	The array of prayers to reset
	 * @param prayerID	The prayer ID to not turn off (exception)
	 */
	public static void resetPrayers(Entity c, int[] prayers, int prayerID) {
		for (int i = 0; i < prayers.length; i++) {
			if (prayers[i] != prayerID)
				deactivatePrayer(c, prayers[i]);
		}
	}

	/**
	 * Resets prayers in the array
	 * @param player
	 * @param prayers
	 */
	public static void resetPrayers(Player player, int[] prayers) {
		for (int i = 0; i < prayers.length; i++) {
			deactivatePrayer(player, prayers[i]);
		}
	}

	/**
	 * Checks if action button ID is a prayer button.
	 * 
	 * @param buttonId
	 * 						action button being hit.
	 */
	public static final boolean isButton(final int actionButtonID) {
		return PrayerData.actionButton.containsKey(actionButtonID);
	}

	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
			ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9, 
			PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
			INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17,
			PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, PRESERVE = 24,
			CHIVALRY = 25, PIETY = 26, RIGOUR = 27, AUGURY = 28;

	/**
	 * Contains every prayer that counts as a defense prayer.
	 */
	public static final int[] DEFENCE_PRAYERS = {THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY};

	/**
	 * Contains every prayer that counts as a strength prayer.
	 */
	public static final int[] STRENGTH_PRAYERS = {BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY, PIETY};

	/**
	 * Contains every prayer that counts as an attack prayer.
	 */
	public static final int[] ATTACK_PRAYERS = {CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY};

	/**
	 * Contains every prayer that counts as a ranged prayer.
	 */
	public static final int[] RANGED_PRAYERS = {SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR};

	/**
	 * Contains every prayer that counts as a magic prayer.
	 */
	public static final int[] MAGIC_PRAYERS = {MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY};

	/**
	 * Contains every prayer that counts as an overhead prayer, excluding protect from summoning.
	 */
	public static final int[] OVERHEAD_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE, RETRIBUTION, REDEMPTION, SMITE};

	/**
	 * Contains every protection prayer
	 */
	public static final int[] PROTECTION_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE};
}