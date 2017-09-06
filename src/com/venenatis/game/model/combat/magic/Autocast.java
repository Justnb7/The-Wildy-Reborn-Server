package com.venenatis.game.model.combat.magic;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.combat.data.AttackStyle;
import com.venenatis.game.model.entity.player.Player;

public class Autocast {

	/**
	 * Checks to see if the button is an autocast button
	 * 
	 * @param button
	 *            The id of the button being pressed
	 * @return
	 */
	public static boolean isAutoButton(int button) {
		AutocastButtons buttons = AutocastButtons.forId(button);
		return buttons != null;
	}

	private static enum AutocastButtons {
		WIND_STRIKE(7038, 0, "Wind strike"), WATER_STRIKE(7039, 1, "Water strike"), EARTH_STRIKE(7040, 2, "Earth strike"), FIRE_STRIKE(7041, 3, "Fire strike"),

		WIND_BOLT(7042, 4, "Wind bolt"), WATER_BOLT(7043, 5, "Water bolt"), EARTH_BOLT(7044, 6, "Earth bolt"), FIRE_BOLT(7045, 7, "Fire bolt"),

		WIND_BLAST(7046, 8, "Wind blast"), WATER_BLAST(7047, 9, "Water blast"), EARTH_BLAST(7048, 10, "Earth blast"), FIRE_BLAST(7049, 11, "Fire blast"),

		WIND_WAVE(7050, 12, "Wind wave"), WATER_WAVE(7051, 13, "Water wave"), EARTH_WAVE(7052, 14, "Earth wave"), FIRE_WAVE(7053, 15, "Fire wave"),

		SMOKE_RUSH(51133, 32, "Smoke rush"), SHADOW_RUSH(51185, 33, "Shadow rush"), BLOOD_RUSH(51091, 34, "Blood rush"), ICE_RUSH(24018, 35, "Ice rush"),

		SMOKE_BURST(51159, 36, "Smoke burst"), SHADOW_BURST(51211, 37, "Shadow burst"), BLOOD_BURST(51111, 38, "Blood burst"), ICE_BURST(51069, 39, "Ice burst"),

		SMOKE_BLITZ(51146, 40, "Smoke blitz"), SHADOW_BLITZ(51198, 41, "Shadow blitz"), BLOOD_BLITZ(51102, 42, "Blood blitz"), ICE_BLITZ(51058, 43, "Ice blitz"),

		SMOKE_BARRAGE(51172, 44, "Smoke barrage"), SHADOW_BARRAGE(51224, 45, "Shadow barrage"), BLOOD_BARRAGE(51122, 46, "Blood barrage"), ICE_BARRAGE(51080, 47, "Ice barrage"),

		CRUMBLE_UNDEAD(47020, 25, "Crumble undead"), MAGIC_DART(47019, 27, "Magic dart"), WIND_WAVE_SLAYER(47021, 12, "Wind wave"), WATER_WAVE_SLAYER(47022, 13, "Water wave"), EARTH_WAVE_SLAYER(47023, 14, "Earth wave"), FIRE_WAVE_SLAYER(47024, 15, "Fire wave");

		private int buttonId, spellId;
		private String name;

		private AutocastButtons(int buttonId, int spellId, String name) {
			this.buttonId = buttonId;
			this.spellId = spellId;
			this.name = name;
		}

		public int getSpellId() {
			return spellId;
		}

		public String getName() {
			return name;
		}

		private static Map<Integer, AutocastButtons> buttons = new HashMap<Integer, AutocastButtons>();

		static {
			for (AutocastButtons button : values()) {
				buttons.put(button.buttonId, button);
			}
		}

		public static AutocastButtons forId(int id) {
			return buttons.get(id);
		}
	}

	/**
	 * Assigns the autocast based on the button pressed
	 * 
	 * @param player
	 * @param button
	 */
	public static void assignAutocast(Player player, int button) {
		AutocastButtons buttons = AutocastButtons.forId(button);
		if (buttons == null) {
			return;
		}
		player.setAutocastId(buttons.getSpellId());
		player.setSpellId(buttons.getSpellId());
		player.getActionSender().sendString(buttons.getName(), 352);
		player.getActionSender().sendSidebarInterface(0, 328);
		player.getActionSender().sendConfig(43, 3);
		player.getActionSender().sendConfig(108, 3);
	}

	/**
	 * Resets autocast
	 * 
	 * @param player
	 */
	public static void resetAutocasting(Player player) {
		player.setSpellId(-1);
		player.setAutocastId(-1);
		player.getActionSender().sendConfig(108, 0);
		player.getActionSender().sendString("", 352);
		player.setAttackStyle(AttackStyle.ACCURATE);
		player.getActionSender().sendConfig(43, 0);
		player.getActionSender().sendSidebarInterface(0, 328);
	}
	
	

	/**
	 * Handles the action buttons for magic related content
	 * 
	 * @param player
	 *            The player pressing the button
	 * @param buttonId
	 *            The id of the button being pressed
	 */
	public static boolean handleActionButtons(Player player, int buttonId) {
		switch (buttonId) {

		case 1093:
		case 1094:
		case 1097:
		case 15486:
		case 353:
		case 350:
			if (player.getAutocastId() > -1) {
				resetAutocasting(player);
			} else {
				if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getId() == 4675) {
					if (player.getSpellBook() == SpellBook.MODERN_MAGICS) {
						player.getActionSender().sendMessage("You cannot autocast using a modern spell book.");
						return false;
					}
					player.getActionSender().sendSidebarInterface(0, 1689);
				} else {
					if (player.getSpellBook() == SpellBook.ANCIENT_MAGICKS) {
						player.getActionSender().sendMessage("You cannot autocast using a ancient spell book.");
						return false;
					}
					if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getId() == 4170) {
						player.getActionSender().sendSidebarInterface(0, 12050);
					} else {
						player.getActionSender().sendSidebarInterface(0, 1829);
					}
				}
			}
			return true;
		case 7212:
		case 47069:
		case 24017:
			resetAutocasting(player);
			return true;
		}
		return false;
	}
}