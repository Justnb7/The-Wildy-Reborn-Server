package com.model.game.character.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.Server;
import com.model.game.character.Graphic;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.utility.Utility;

public class PrayerHandler {

	public static enum Prayers {
		THICK_SKIN(21233, 1, 0.5, 83), 
		BURST_OF_STRENGTH(21234, 4, 0.5, 84), 
		CLARITY_OF_THOUGHT(21235, 7, 0.5, 85), 
		SHARP_EYE(77100, 8, 0.5, 700), 
		MYSTIC_WILL(77102, 9, 0.5, 701), 
		ROCK_SKIN(21236, 10, 1.0, 86), 
		SUPERHUMAN_STRENGTH(21237, 13, 1.0, 87), 
		IMPROVED_REFLEXES(21238, 16, 1.0, 88), 
		RAPID_RESTORE(21239, 19, 0.15, 89), 
		RAPID_HEAL(21240, 22, 0.3, 90), 
		PROTECT_ITEM(21241, 25, 0.3, 91), 
		HAWK_EYE(77104, 26, 1.0, 702), 
		MYSTIC_LORE(77106, 27, 1.0, 703), 
		STEEL_SKIN(21242, 28, 2.0, 92), 
		ULTIMATE_STRENGTH(21243, 31, 2.0, 93), 
		INCREDIBLE_REFLEXES(21244, 34, 2.0, 94), 
		PROTECT_FROM_MAGIC(21245, 37, 2.0, 95), 
		PROTECT_FROM_MISSILE(21246, 40, 2.0, 96), 
		PROTECT_FROM_MELEE(21247, 43, 2.0, 97), 
		EAGLE_EYE(77109, 44, 2.0, 704), 
		MYSTIC_MIGHT(77111, 45, 2.0, 705), 
		RETRIBUTION(2171, 46, 0.5, 98), 
		REDEMPTION(2172, 49, 1.0, 99), 
		SMITE(2173, 52, 2.0, 100), 
		PRESERVE(109097, 55, 1.0, 708),
		CHIVALRY(77113, 60, 3.0, 706), 
		PIETY(77115, 70, 4.0, 707),
		RIGOUR(109100, 74, 4.0, 710),
		AUGURY(109103, 77, 4.0, 712);

		private int buttonId;

		private int levelRequirement;

		private double drainRate;

		private int configId;
		
		/**
		 * The prayer's formatted name.
		 */
		private String name;

		private Prayers(int buttonId, int level, double drain, int configId) {
			this.buttonId = buttonId;
			this.levelRequirement = level;
			this.drainRate = drain;
			this.configId = configId;
		}

		public int getPrayerIndex(Prayers prayer) {
			for (Prayers data : Prayers.values()) {
				if (data == prayer) {
					return data.ordinal();
				}
			}
			return -1;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getConfigId() {
			return configId;
		}

		public int getLevelRequirement() {
			return levelRequirement;
		}

		public double getDrainRate() {
			return drainRate;
		}
		
		/**
		 * Gets the prayer's formatted name.
		 * @return	The prayer's name
		 */
		private final String getPrayerName() {
			if (name == null)
				return Utility.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			return name;
		}
	}

	public static void togglePrayer(final Player player, final int buttonId) {
		for (Prayers prayer : Prayers.values()) {
			if (buttonId == prayer.getButtonId()) {
				if (player.isActivePrayer(prayer)) {
					deactivatePrayer(player, prayer);
					return;
				} else {
					activatePrayer(player, prayer);
					return;
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
	public static boolean canActivate(Player player, final Prayers prayer, boolean msg) {
		if (player.getSkills().getLevelForExperience(Skills.PRAYER) < prayer.getLevelRequirement()) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.getActionSender().sendMessage("You need a Prayer level of at least " + prayer.getLevelRequirement() + " to use " + prayer.getPrayerName() + ".");
				deactivatePrayer(player, prayer);
			}
			return false;
		}
		return msg;
	}

	private static void activatePrayer(final Player player, final Prayers prayer) {
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(Rule.NO_PRAYER)) {
					player.getActionSender().sendMessage("Prayer has been disabled for this duel.");
					resetAllPrayers(player);
					return;
				}
			}
		}
		if (player.getSkills().getLevelForExperience(Skills.PRAYER) < prayer.getLevelRequirement()) {
			player.getActionSender().sendMessage("You need a prayer level of at least " + prayer.getLevelRequirement() + " to use " + prayer.toString().toLowerCase().replaceAll("_", " ") + ".");
			deactivatePrayer(player, prayer);
			return;
		}
		if (player.getSkills().getLevel(Skills.PRAYER) <= 0) {
			player.getActionSender().sendMessage("You have run out of prayer points; recharge your prayer points at an altar.");
			deactivatePrayer(player, prayer);
			return;
		}
		
		if (prayer == Prayers.PROTECT_ITEM) {
			if (!player.getAccount().getType().canUseItemProtection()) {
				player.getActionSender().sendMessage("You're account is restricted from using protect item prayer.");
				return;
			}
		}
		
		if (prayer != Prayers.PROTECT_ITEM)
			switchPrayer(player, prayer);
		if (prayer.equals(Prayers.PROTECT_FROM_MAGIC) || prayer.equals(Prayers.PROTECT_FROM_MISSILE) || prayer.equals(Prayers.PROTECT_FROM_MELEE) || prayer.equals(Prayers.RETRIBUTION) || prayer.equals(Prayers.REDEMPTION) || prayer.equals(Prayers.SMITE)) {
			int headIcon = -1;
			switch (prayer) {
			case PROTECT_FROM_MAGIC:
				headIcon = 2;
				break;
			case PROTECT_FROM_MISSILE:
				headIcon = 1;
				break;
			case PROTECT_FROM_MELEE:
				headIcon = 0;
				break;
			case RETRIBUTION:
				headIcon = 3;
				break;
			case REDEMPTION:
				headIcon = 5;
				break;
			case SMITE:
				headIcon = 4;
				break;
			default:
				break;
			}
			player.setPrayerIcon(headIcon);
			player.getPA().requestUpdates();
		}
		player.setActivePrayer(prayer, true);
		player.getActionSender().sendConfig(prayer.getConfigId(), 1);
		player.addPrayerDrainRate(prayer.getDrainRate());
	}

	public static void deactivatePrayer(final Player player, final Prayers prayer) {
		player.setActivePrayer(prayer, false);
		if (prayer.equals(Prayers.PROTECT_FROM_MAGIC) || prayer.equals(Prayers.PROTECT_FROM_MISSILE) || prayer.equals(Prayers.PROTECT_FROM_MELEE) || prayer.equals(Prayers.RETRIBUTION) || prayer.equals(Prayers.REDEMPTION) || prayer.equals(Prayers.SMITE)) {
			player.setPrayerIcon(-1);
			player.getPA().requestUpdates();
		}
		player.addPrayerDrainRate(-(prayer.getDrainRate()));
		player.getActionSender().sendConfig(prayer.getConfigId(), 0);
	}

	private static Prayers[] defensePrayers = { Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.STEEL_SKIN,

	};

	private static Prayers[] strengthPrayers = { Prayers.BURST_OF_STRENGTH, Prayers.SUPERHUMAN_STRENGTH, Prayers.ULTIMATE_STRENGTH,

	Prayers.CHIVALRY, Prayers.PIETY,

	Prayers.EAGLE_EYE, Prayers.HAWK_EYE, Prayers.SHARP_EYE,

	Prayers.MYSTIC_LORE, Prayers.MYSTIC_MIGHT, Prayers.MYSTIC_WILL

	};

	private static Prayers[] attackPrayers = { Prayers.CLARITY_OF_THOUGHT, Prayers.IMPROVED_REFLEXES, Prayers.INCREDIBLE_REFLEXES,

	Prayers.CHIVALRY, Prayers.PIETY,

	Prayers.EAGLE_EYE, Prayers.HAWK_EYE, Prayers.SHARP_EYE, Prayers.RIGOUR,

	Prayers.MYSTIC_LORE, Prayers.MYSTIC_MIGHT, Prayers.MYSTIC_WILL, Prayers.AUGURY

	};

	private static Prayers[] restorePrayers = { Prayers.RAPID_RESTORE, Prayers.RAPID_HEAL, Prayers.PRESERVE,

	};
	private static Prayers[] overheadPrayers = { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILE, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE

	};

	private static Map<Prayers, Prayers[]> unstackable = new HashMap<Prayers, Prayers[]>();

	static {
		for (Prayers p : defensePrayers) {
			unstackable.put(p, defensePrayers);
		}
		for (Prayers p : strengthPrayers) {
			unstackable.put(p, strengthPrayers);
		}
		for (Prayers p : attackPrayers) {
			unstackable.put(p, attackPrayers);
		}
		for (Prayers p : restorePrayers) {
			unstackable.put(p, restorePrayers);
		}
		for (Prayers p : overheadPrayers) {
			unstackable.put(p, overheadPrayers);
		}
	}

	/**
	 * Handles draining the prayer
	 * 
	 * @param player
	 *            The player with active prayers
	 */
	public static void handlePrayerDraining(Player player) {
		double toRemove = 0.0;
		for (Prayers prayer : Prayers.values()) {
			if (player.isActivePrayer(prayer)) {
				toRemove += prayer.getDrainRate() / 20;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.035 * player.playerBonus[11]));
		}
		//System.out.println("Remove prayer point: "+toRemove);
		player.setPrayerPoint(player.getPrayerPoint() - toRemove);
		if (player.getPrayerPoint() <= 0) {
			player.setPrayerPoint(1.0 + player.getPrayerPoint());
			if (player.getSkills().getLevel(Skills.PRAYER) > 1) {
				player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) - 1);
			} else {
				player.getActionSender().sendMessage("You have run out of prayer points.");
				player.getSkills().setLevel(Skills.PRAYER, 0);
				for (Prayers prayer : Prayers.values()) {
					deactivatePrayer(player, prayer);
				}
			}
		}
	}

	/**
	 * deactivates all of the prayers
	 * 
	 * @param player
	 */
	public static void resetAllPrayers(final Player player) {
		for (Prayers prayer : Prayers.values()) {
			deactivatePrayer(player, prayer);
		}
	}

	private static void switchPrayer(final Player player, final Prayers prayer) {
		Prayers[] toDeactivate = unstackable.get(prayer);

		if (player.isActivePrayer(Prayers.ROCK_SKIN) || player.isActivePrayer(Prayers.STEEL_SKIN) || player.isActivePrayer(Prayers.THICK_SKIN)) {
			if (prayer == Prayers.CHIVALRY || prayer == Prayers.PIETY) {
				if (player.isActivePrayer(Prayers.THICK_SKIN)) {
					deactivatePrayer(player, Prayers.THICK_SKIN);
				} else if (player.isActivePrayer(Prayers.ROCK_SKIN)) {
					deactivatePrayer(player, Prayers.ROCK_SKIN);
				} else if (player.isActivePrayer(Prayers.STEEL_SKIN)) {
					deactivatePrayer(player, Prayers.STEEL_SKIN);
				}
			}
		}

		for (int i = 0; i < toDeactivate.length; i++) {
			if (player.isActivePrayer(toDeactivate[i]) && toDeactivate[i] != prayer) {
				deactivatePrayer(player, toDeactivate[i]);
			}
		}
	}

	public static void handleSmite(Player player, Player defender, int damage) {
		int reduce = damage / 4;
		if (player.isActivePrayer(Prayers.SMITE)) {
			defender.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) - reduce);
			//player.getActionSender().sendMessage("reduced " + defender.getName() + "'s prayer level by, " + reduce + ". Also dealt "+damage+" damage."));
			
			if (defender.getSkills().getLevel(Skills.PRAYER) <= 0) {
				defender.getSkills().setLevel(Skills.PRAYER, 0);
				resetAllPrayers(defender);
			}
		}
	}
	
	public void appendRedemption(Player player) {
		if (player.isActivePrayer(Prayers.REDEMPTION)) {
			player.getSkills().setLevel(Skills.HITPOINTS, (int) (+ player.getSkills().getLevelForExperience(Skills.PRAYER) * .25));
			player.getSkills().setLevel(Skills.HITPOINTS, 0);
			player.playGraphics(Graphic.create(436, 0, 0));
			resetAllPrayers(player);
		}
	}
}