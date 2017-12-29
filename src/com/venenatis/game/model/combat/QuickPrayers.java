package com.venenatis.game.model.combat;

import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelOptions;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;

/**
 * Handles quick prayers.
 * @author Professor Oak
 */
public class QuickPrayers extends PrayerHandler {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * The array holding the player's quick prayers.
	 */
	private PrayerData[] prayers = new PrayerData[PrayerData.values().length];

	/**
	 * Is the player currently selecting quick prayers?
	 * @param player
	 */
	private boolean selectingPrayers;

	/**
	 * Are the quick prayers currently enabled?
	 */
	private boolean enabled;

	/**
	 * The constructor
	 * @param player
	 */
	public QuickPrayers(Player player) {
		this.player = player;
	}


	/**
	 * Sends the current quick-prayer toggle-state for each prayer.
	 */
	public void sendChecks() {
		for(PrayerData prayer : PrayerData.values()) {
			sendCheck(prayer);
		}
	}

	/**
	 * Sends quick-prayer toggle-state for the specified prayer.
	 * @param prayer
	 */
	private void sendCheck(PrayerData prayer) {
		player.getActionSender().sendConfig(CONFIG_START + prayer.ordinal(), prayers[prayer.ordinal()] != null ? 1 : 0);
	}

	/**
	 *Unchecks the specified prayers but the exception.
	 */
	private void uncheck(int[] toDeselect, int exception) {
		for(int i : toDeselect) {
			if(i == exception) {
				continue;
			}
			uncheck(PrayerData.values()[i]);
		}
	}

	/**
	 * Unchecks the specified prayer.
	 * @param prayer
	 */
	private void uncheck(PrayerData prayer) {
		if(prayers[prayer.ordinal()] != null) {
			prayers[prayer.ordinal()] = null;
			sendCheck(prayer);
		}
	}

	/**
	 * Handles the action for clicking a prayer.
	 * 
	 * @param index
	 */
	private void toggle(int index) {
		PrayerData prayer = PrayerData.values()[index];

		//Has the player already selected this quick prayer?
		//Then reset it.
		if(prayers[prayer.ordinal()] != null) {
			uncheck(prayer);
			return;
		}
		
		if(!canUse(player, prayer, true)) {
			uncheck(prayer);
			return;
		}

		prayers[prayer.ordinal()] = prayer;
		sendCheck(prayer);
		
		switch (index) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			uncheck(DEFENCE_PRAYERS, index);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			uncheck(STRENGTH_PRAYERS, index);
			uncheck(RANGED_PRAYERS, index);
			uncheck(MAGIC_PRAYERS, index);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			uncheck(ATTACK_PRAYERS, index);
			uncheck(RANGED_PRAYERS, index);
			uncheck(MAGIC_PRAYERS, index);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			uncheck(STRENGTH_PRAYERS, index);
			uncheck(ATTACK_PRAYERS, index);
			uncheck(RANGED_PRAYERS, index);
			uncheck(MAGIC_PRAYERS, index);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			uncheck(DEFENCE_PRAYERS, index);
			uncheck(STRENGTH_PRAYERS, index);
			uncheck(ATTACK_PRAYERS, index);
			uncheck(RANGED_PRAYERS, index);
			uncheck(MAGIC_PRAYERS, index);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			uncheck(OVERHEAD_PRAYERS, index);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			uncheck(OVERHEAD_PRAYERS, index);
			break;
		}
	}


	/**
	 * Checks if the player has manually turned off
	 * any of the quick prayers.
	 * If all quick prayers are turned off,
	 * disable them completely.
	 */
	public void checkActive() {
		if(enabled) {
			for(PrayerData prayer : prayers) {
				if(prayer == null)
					continue;
				if(isActivated(player, prayer.ordinal())) {
					return;
				}
			}
			enabled = false;
			player.setHeadHint(-1);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.getActionSender().sendQuickPrayersState(false);
		}
	}

	/**
	 * Handles an incoming button.
	 * Check if it's related to quick prayers.
	 * 
	 * @param button
	 * @return
	 */
	public boolean handleButton(int button) {
		switch(button) {
		case TOGGLE_QUICK_PRAYERS:
			
			if (player.getDuelArena().getOptionActive()[DuelOptions.NO_PRAYER.getId()]) {
				player.getActionSender().sendMessage("The right to use prayer has been revoked during this duel.");
				return false;
			}

			if(player.getSkills().getLevel(Skills.PRAYER) <= 0) {
				player.getActionSender().sendMessage("You don't have enough Prayer points.");
				return true;
			}

			if(enabled) {
				for(PrayerData prayer : prayers) {
					if(prayer == null)
						continue;
					deactivatePrayer(player, prayer.ordinal());
				}
				enabled = false;
			} else {
				boolean found = false;
				for(PrayerData prayer : prayers) {
					if(prayer == null)
						continue;
					activatePrayer(player, prayer.ordinal());
					found = true;
				}
				
				if(!found) {
					player.getActionSender().sendMessage("You have not setup any quick-prayers yet.");
				}
				
				enabled = found;
			}

			player.getActionSender().sendQuickPrayersState(enabled);
			break;

		case SETUP_BUTTON:
			if(selectingPrayers) {
				player.getActionSender().sendSidebarInterface(5, 5608).sendTab(5);
				selectingPrayers = false;
			} else {
				sendChecks();
				player.getActionSender().sendSidebarInterface(5, QUICK_PRAYERS_TAB_INTERFACE_ID).sendTab(5);
				selectingPrayers = true;
			}
			break;

		case CONFIRM_BUTTON:
			if(selectingPrayers) {
				player.getActionSender().sendSidebarInterface(5, 5608);
				selectingPrayers = false;
			}
			break;
		}

		//Clicking prayers
		if(button >= 67050 && button <= 67078) {
			if(selectingPrayers) {
				final int index = button - 67050;
				toggle(index);
			}
			return true;
		}

		return false;
	}


	/**
	 * Sets enabled state
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Gets the selected quick prayers
	 * @return
	 */
	public PrayerData[] getPrayers() {
		return prayers;
	}
	
	/**
	 * Sets the selected quick prayers
	 * @param prayers
	 */
	public void setPrayers(PrayerData[] prayers) {
		this.prayers = prayers;
	}

	/**
	 * Toggle button
	 */
	private static final int TOGGLE_QUICK_PRAYERS = 19136;

	/**
	 * The button for starting to setup quick prayers.
	 */
	private static final int SETUP_BUTTON = 19137;

	/**
	 * The confirmation button in the interface.
	 */
	private static final int CONFIRM_BUTTON = 67080;

	/**
	 * The actual main interface id.
	 */
	private static final int QUICK_PRAYERS_TAB_INTERFACE_ID = 17200;

	/**
	 * The interface config buttons start.
	 */
	private static final int CONFIG_START = 620;
}