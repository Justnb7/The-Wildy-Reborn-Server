package com.venenatis.game.model.combat;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.util.Utility;

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
	
	public static boolean isActivated(Player player, int prayer) {
		return player.getPrayers()[prayer];
	}

	public static boolean togglePrayer(final Player player, final int buttonId) {
		for (Prayers prayer : Prayers.values()) {
			if (buttonId == prayer.getButtonId()) {
				if (player.isActivePrayer(prayer)) {
					deactivatePrayer(player, prayer);
					return false;
				} else {
					activatePrayer(player, prayer);
					return true;
				}
			}
		}
		return false;
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
				player.message("You need a Prayer level of at least " + prayer.getLevelRequirement() + " to use " + prayer.getPrayerName() + ".");
				deactivatePrayer(player, prayer);
			}
			return false;
		}
		
		if (prayer == Prayers.CHIVALRY && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 60) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.message("You need a Defence level of at least 60 to use Chivalry.");
			}
			return false;
		}
		
		if ((prayer == Prayers.PIETY || prayer == Prayers.RIGOUR || prayer == Prayers.AUGURY) && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
			if(msg) {
				player.getActionSender().sendConfig(prayer.configId, 0);
				player.message("You need a Defence level of at least 70 to use "+prayer.getPrayerName()+".");
			}
			return false;
		}
		
		if (prayer == Prayers.PROTECT_ITEM) {
			if (!player.getAccount().getType().canUseItemProtection()) {
				player.message("You're account is restricted from using protect item prayer.");
				return false;
			}
		}
		
		// Prayer locks
		boolean locked = false;

		if (prayer == Prayers.PRESERVE && !player.isPreserveUnlocked()
				|| prayer == Prayers.RIGOUR && !player.isRigourUnlocked()
				|| prayer == Prayers.AUGURY && !player.isAuguryUnlocked()) {
			locked = true;
		}

		if (locked) {
			if (msg) {
				player.message("You have not unlocked that Prayer yet.");
			}
			return false;
		}
		
		return true;
	}

	public static void activatePrayer(final Player player, final Prayers prayer) {
		
		if (player.getDuelArena().isDueling()) {
			if (player.getDuelArena().getRules().get(DuelRule.PRAYER)) {
				player.message("Prayer has been disabled in this duel.");
				return;
			}
		}
		
		if (player.getSkills().getLevel(Skills.PRAYER) <= 0) {
			player.message("You have run out of prayer points; recharge your prayer points at an altar.");
			deactivatePrayer(player, prayer);
			return;
		}
		
		if(!canActivate(player, prayer, true)) {
			return;
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
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		player.setActivePrayer(prayer, true);
		player.getActionSender().sendConfig(prayer.getConfigId(), 1);
		player.addPrayerDrainRate(prayer.getDrainRate());
	}

	public static void deactivatePrayer(final Player player, final Prayers prayer) {
		player.setActivePrayer(prayer, false);
		if (prayer.equals(Prayers.PROTECT_FROM_MAGIC) || prayer.equals(Prayers.PROTECT_FROM_MISSILE) || prayer.equals(Prayers.PROTECT_FROM_MELEE) || prayer.equals(Prayers.RETRIBUTION) || prayer.equals(Prayers.REDEMPTION) || prayer.equals(Prayers.SMITE)) {
			player.setPrayerIcon(-1);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		player.addPrayerDrainRate(-(prayer.getDrainRate()));
		player.getActionSender().sendConfig(prayer.getConfigId(), 0);
	}

	public static Prayers[] DEFENCE_PRAYERS = { Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY, Prayers.RIGOUR, Prayers.AUGURY

	};

	public static Prayers[] STRENGTH_PRAYERS = { Prayers.BURST_OF_STRENGTH, Prayers.SUPERHUMAN_STRENGTH, Prayers.ULTIMATE_STRENGTH,

	Prayers.CHIVALRY, Prayers.PIETY,

	Prayers.EAGLE_EYE, Prayers.HAWK_EYE, Prayers.SHARP_EYE,

	Prayers.MYSTIC_LORE, Prayers.MYSTIC_MIGHT, Prayers.MYSTIC_WILL

	};

	public static Prayers[] ATTACK_PRAYERS = { Prayers.CLARITY_OF_THOUGHT, Prayers.IMPROVED_REFLEXES, Prayers.INCREDIBLE_REFLEXES,

	Prayers.CHIVALRY, Prayers.PIETY,

	Prayers.EAGLE_EYE, Prayers.HAWK_EYE, Prayers.SHARP_EYE, Prayers.RIGOUR,

	Prayers.MYSTIC_LORE, Prayers.MYSTIC_MIGHT, Prayers.MYSTIC_WILL, Prayers.AUGURY

	};

	private static Prayers[] restorePrayers = { Prayers.RAPID_RESTORE, Prayers.RAPID_HEAL, Prayers.PRESERVE,

	};
	
	public static Prayers[] OVERHEAD_PRAYERS = { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILE, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE

	};

	private static Map<Prayers, Prayers[]> unstackable = new HashMap<Prayers, Prayers[]>();

	static {
		for (Prayers p : DEFENCE_PRAYERS) {
			unstackable.put(p, DEFENCE_PRAYERS);
		}
		for (Prayers p : STRENGTH_PRAYERS) {
			unstackable.put(p, STRENGTH_PRAYERS);
		}
		for (Prayers p : ATTACK_PRAYERS) {
			unstackable.put(p, ATTACK_PRAYERS);
		}
		for (Prayers p : restorePrayers) {
			unstackable.put(p, restorePrayers);
		}
		for (Prayers p : OVERHEAD_PRAYERS) {
			unstackable.put(p, OVERHEAD_PRAYERS);
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
			toRemove /= (1 + (0.035 * player.getBonuses()[11]));
		}
		//System.out.println("Remove prayer point: "+toRemove);
		player.setPrayerPoint(player.getPrayerPoint() - toRemove);
		if (player.getPrayerPoint() <= 0) {
			player.setPrayerPoint(1.0 + player.getPrayerPoint());
			if (player.getSkills().getLevel(Skills.PRAYER) > 1) {
				player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) - 1);
			} else {
				player.message("You have run out of prayer points.");
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
			//player.message("reduced " + defender.getName() + "'s prayer level by, " + reduce + ". Also dealt "+damage+" damage."));
			
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