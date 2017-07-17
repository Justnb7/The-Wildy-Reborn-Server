package com.venenatis.game.model.combat;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;

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
	private Prayers[] prayers = new Prayers[Prayers.values().length];

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
		for(Prayers prayer : Prayers.values()) {
			sendCheck(prayer);
		}
	}

	/**
	 * Sends quick-prayer toggle-state for the specified prayer.
	 * @param prayer
	 */
	private void sendCheck(Prayers prayer) {
		player.getActionSender().sendConfig(CONFIG_START + prayer.ordinal(), prayers[prayer.ordinal()] != null ? 0 : 1);
	}

	/**
	 *Unchecks the specified prayers but the exception.
	 */
	private void uncheck(Prayers[] prayer, int exception) {
		for(Prayers i : prayer) {
			if(i.ordinal() == exception) {
				continue;
			}
			uncheck(Prayers.values()[i.ordinal()]);
		}
	}

	/**
	 * Unchecks the specified prayer.
	 * @param prayer
	 */
	private void uncheck(Prayers prayer) {
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
		Prayers prayer = Prayers.values()[index];

		//Has the player already selected this quick prayer?
		//Then reset it.
		if(prayers[prayer.ordinal()] != null) {
			uncheck(prayer);
			return;
		}
		
		if(!canActivate(player, prayer, true)) {
			uncheck(prayer);
			return;
		}

		prayers[prayer.ordinal()] = prayer;
		sendCheck(prayer);
		
		switch (prayer) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			uncheck(DEFENCE_PRAYERS, index);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			uncheck(STRENGTH_PRAYERS, index);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			uncheck(ATTACK_PRAYERS, index);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			uncheck(STRENGTH_PRAYERS, index);
			uncheck(ATTACK_PRAYERS, index);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			uncheck(DEFENCE_PRAYERS, index);
			uncheck(STRENGTH_PRAYERS, index);
			uncheck(ATTACK_PRAYERS, index);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILE:
		case PROTECT_FROM_MELEE:
			uncheck(OVERHEAD_PRAYERS, index);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			uncheck(OVERHEAD_PRAYERS, index);
			break;
		case PRESERVE:
			break;
		case PROTECT_ITEM:
			break;
		case RAPID_HEAL:
			break;
		case RAPID_RESTORE:
			break;
		default:
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
			for(Prayers prayer : prayers) {
				if(prayer == null)
					continue;
				if(isActivated(player, prayer.ordinal())) {
					return;
				}
			}
			enabled = false;
			//player.getActionSender().sendQuickPrayersState(false);
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

			if(player.getSkills().getLevel(Skills.PRAYER) <= 0) {
				player.getActionSender().sendMessage("You don't have enough Prayer points.");
				return true;
			}

			if(enabled) {
				for(Prayers prayer : prayers) {
					if(prayer == null)
						continue;
					deactivatePrayer(player, prayer);
				}
				enabled = false;
			} else {
				boolean found = false;
				for(Prayers prayer : prayers) {
					if(prayer == null)
						continue;
					activatePrayer(player, prayer);
					found = true;
				}
				
				if(!found) {
					player.getActionSender().sendMessage("You have not setup any quick-prayers yet.");
				}
				
				enabled = found;
			}

			//player.getActionSender().sendQuickPrayersState(enabled);
			break;

		case SETUP_BUTTON:
			if(selectingPrayers) {
				player.getActionSender().sendSidebarInterface(5, 5608);
				selectingPrayers = false;
			} else {
				sendChecks();
				player.getActionSender().sendSidebarInterface(5, QUICK_PRAYERS_TAB_INTERFACE_ID);
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
		if(button >= 17202 && button <= 17230) {
			if(selectingPrayers) {
				final int index = button - 17202;
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
	public Prayers[] getPrayers() {
		return prayers;
	}
	
	/**
	 * Sets the selected quick prayers
	 * @param prayers
	 */
	public void setPrayers(Prayers[] prayers) {
		this.prayers = prayers;
	}

	/**
	 * Toggle button
	 */
	private static final int TOGGLE_QUICK_PRAYERS = 1500;

	/**
	 * The button for starting to setup quick prayers.
	 */
	private static final int SETUP_BUTTON = 1506;

	/**
	 * The confirmation button in the interface.
	 */
	private static final int CONFIRM_BUTTON = 17232;

	/**
	 * The actual main interface id.
	 */
	private static final int QUICK_PRAYERS_TAB_INTERFACE_ID = 17200;

	/**
	 * The interface config buttons start.
	 */
	private static final int CONFIG_START = 620;
}