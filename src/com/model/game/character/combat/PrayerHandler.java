package com.model.game.character.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.packets.encode.impl.SendConfig;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;

public class PrayerHandler {

	public static enum Prayer {
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
		CHIVALRY(77113, 60, 3.0, 706), 
		PIETY(77115, 70, 4.0, 707);

		private int buttonId;

		private int levelRequirement;

		private double drainRate;

		private int configId;

		private Prayer(int buttonId, int level, double drain, int configId) {
			this.buttonId = buttonId;
			this.levelRequirement = level;
			this.drainRate = drain;
			this.configId = configId;
		}

		public int getPrayerIndex(Prayer prayer) {
			for (Prayer data : Prayer.values()) {
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
	}

	public static void togglePrayer(final Player player, final int buttonId) {
		for (Prayer prayer : Prayer.values()) {
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

	private static void activatePrayer(final Player player, final Prayer prayer) {
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(Rule.NO_PRAYER)) {
					player.write(new SendMessagePacket("Prayer has been disabled for this duel."));
					resetAllPrayers(player);
					return;
				}
			}
		}
		if (player.getSkills().getLevelForExperience(Skills.PRAYER) < prayer.getLevelRequirement()) {
			player.write(new SendMessagePacket("You need a prayer level of at least " + prayer.getLevelRequirement() + " to use " + prayer.toString().toLowerCase().replaceAll("_", " ") + "."));
			deactivatePrayer(player, prayer);
			return;
		}
		if (player.getSkills().getLevel(Skills.PRAYER) <= 0) {
			player.write(new SendMessagePacket("You have run out of prayer points; recharge your prayer points at an altar."));
			deactivatePrayer(player, prayer);
			return;
		}
		if (prayer != Prayer.PROTECT_ITEM)
			switchPrayer(player, prayer);
		if (prayer.equals(Prayer.PROTECT_FROM_MAGIC) || prayer.equals(Prayer.PROTECT_FROM_MISSILE) || prayer.equals(Prayer.PROTECT_FROM_MELEE) || prayer.equals(Prayer.RETRIBUTION) || prayer.equals(Prayer.REDEMPTION) || prayer.equals(Prayer.SMITE)) {
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
		player.write(new SendConfig(prayer.getConfigId(), 1));
		player.addPrayerDrainRate(prayer.getDrainRate());
	}

	public static void deactivatePrayer(final Player player, final Prayer prayer) {
		player.setActivePrayer(prayer, false);
		if (prayer.equals(Prayer.PROTECT_FROM_MAGIC) || prayer.equals(Prayer.PROTECT_FROM_MISSILE) || prayer.equals(Prayer.PROTECT_FROM_MELEE) || prayer.equals(Prayer.RETRIBUTION) || prayer.equals(Prayer.REDEMPTION) || prayer.equals(Prayer.SMITE)) {
			player.setPrayerIcon(-1);
			player.getPA().requestUpdates();
		}
		player.addPrayerDrainRate(-(prayer.getDrainRate()));
		player.write(new SendConfig(prayer.getConfigId(), 0));
	}

	private static Prayer[] defensePrayers = { Prayer.THICK_SKIN, Prayer.ROCK_SKIN, Prayer.STEEL_SKIN,

	};

	private static Prayer[] strengthPrayers = { Prayer.BURST_OF_STRENGTH, Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH,

	Prayer.CHIVALRY, Prayer.PIETY,

	Prayer.EAGLE_EYE, Prayer.HAWK_EYE, Prayer.SHARP_EYE,

	Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.MYSTIC_WILL

	};

	private static Prayer[] attackPrayers = { Prayer.CLARITY_OF_THOUGHT, Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES,

	Prayer.CHIVALRY, Prayer.PIETY,

	Prayer.EAGLE_EYE, Prayer.HAWK_EYE, Prayer.SHARP_EYE,

	Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.MYSTIC_WILL

	};

	private static Prayer[] restorePrayers = { Prayer.RAPID_RESTORE, Prayer.RAPID_HEAL

	};
	private static Prayer[] overheadPrayers = { Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MISSILE, Prayer.PROTECT_FROM_MELEE, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE

	};

	private static Map<Prayer, Prayer[]> unstackable = new HashMap<Prayer, Prayer[]>();

	static {
		for (Prayer p : defensePrayers) {
			unstackable.put(p, defensePrayers);
		}
		for (Prayer p : strengthPrayers) {
			unstackable.put(p, strengthPrayers);
		}
		for (Prayer p : attackPrayers) {
			unstackable.put(p, attackPrayers);
		}
		for (Prayer p : restorePrayers) {
			unstackable.put(p, restorePrayers);
		}
		for (Prayer p : overheadPrayers) {
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
		for (Prayer prayer : Prayer.values()) {
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
				player.write(new SendMessagePacket("You have run out of prayer points."));
				player.getSkills().setLevel(Skills.PRAYER, 0);
				for (Prayer prayer : Prayer.values()) {
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
		for (Prayer prayer : Prayer.values()) {
			deactivatePrayer(player, prayer);
		}
	}

	private static void switchPrayer(final Player player, final Prayer prayer) {
		Prayer[] toDeactivate = unstackable.get(prayer);

		if (player.isActivePrayer(Prayer.ROCK_SKIN) || player.isActivePrayer(Prayer.STEEL_SKIN) || player.isActivePrayer(Prayer.THICK_SKIN)) {
			if (prayer == Prayer.CHIVALRY || prayer == Prayer.PIETY) {
				if (player.isActivePrayer(Prayer.THICK_SKIN)) {
					deactivatePrayer(player, Prayer.THICK_SKIN);
				} else if (player.isActivePrayer(Prayer.ROCK_SKIN)) {
					deactivatePrayer(player, Prayer.ROCK_SKIN);
				} else if (player.isActivePrayer(Prayer.STEEL_SKIN)) {
					deactivatePrayer(player, Prayer.STEEL_SKIN);
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
		if (player.isActivePrayer(Prayer.SMITE)) {
			defender.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) - reduce);
			//player.write(new SendMessagePacket("reduced " + defender.getName() + "'s prayer level by, " + reduce + ". Also dealt "+damage+" damage."));
			
			if (defender.getSkills().getLevel(Skills.PRAYER) <= 0) {
				defender.getSkills().setLevel(Skills.PRAYER, 0);
				resetAllPrayers(defender);
			}
		}
	}
	
	public void appendRedemption(Player player) {
		if (player.isActivePrayer(Prayer.REDEMPTION)) {
			player.getSkills().setLevel(Skills.HITPOINTS, (int) (+ player.getSkills().getLevelForExperience(Skills.PRAYER) * .25));
			player.getSkills().setLevel(Skills.HITPOINTS, 0);
			player.playGraphics(Graphic.create(436, 0, 0));
			resetAllPrayers(player);
		}
	}
	
	public void prayAltar(Player player) {
        if (player.getRights().isBetween(2, 7)) {
			if (player.getLastAltarPrayer() < 120000) {
				player.write(new SendMessagePacket("You can only use the altar to restore your special attack every 2 minutes"));
			} else {
				player.setSpecialAmount(100);
				player.setLastAltarPrayer(System.currentTimeMillis());
				player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, player.getSkills().getLevelForExperience(Skills.HITPOINTS));
			}
        } else if (Utility.random(4) == 0) {
        	player.write(new SendMessagePacket("Did you know if you were a donator you'd restore special energy and hitpoints?"));
        }

        if (player.getPrayerPoint() >= player.getSkills().getLevelForExperience(Skills.PRAYER)) {
        	player.write(new SendMessagePacket("You already have full prayer points."));
            return;
        }
        player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
        player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
        if (player.getActionSender() != null) {
            player.getActionSender().sendSkills();
        }
        player.playAnimation(Animation.create(645));
        player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
        player.write(new SendMessagePacket("You pray at the altar..."));
    }
}