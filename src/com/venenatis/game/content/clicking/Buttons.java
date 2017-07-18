package com.venenatis.game.content.clicking;

import com.venenatis.game.content.ItemsKeptOnDeath;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.sounds_and_music.MusicData;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the actions of clicking on buttons from the client.
 * 
 * @author Patrick van Elderen
 *
 */
public class Buttons {

	/**
	 * Handles the button click for a player.
	 * 
	 * @param player
	 *            The player clicking the button
	 * 
	 * @param buttonId
	 *            The id of the button being clicked.
	 */
	public static void handleButton(Player player, int button) {
		player.debug("enter");
		switch (button) {

		/* Close */
		case 42210:
		case 90163:
		case 55096:
		case 86019:
		case 140162:
		case 162235:
		case 222170:
		case 116181:
		case 195081:
		case 166023:
			player.getActionSender().removeAllInterfaces();
			break;
			
		/* Presets */
		case 222174:
			player.getPresets().open(0);
			break;
		case 222177:
			player.getPresets().open(1);
			break;
		case 222180:
			player.getPresets().open(2);
			break;
		case 222183:
			player.getPresets().open(3);
			break;
		case 222186:
			player.getPresets().open(4);
			break;
		case 222189:
			player.getPresets().open(5);
			break;
		case 222218:
			player.getPresets().gearUp(player.getPresets().getViewing());
			break;
		case 222222:
			//TODO save preset
			break;
		case 222227:
			player.getPresets().clear(player.getPresets().getViewing());
			break;
		case 222230:
		case -8473:
			player.getPresets().setDeathOpen(player.getPresets().isDeathOpen() ? false : true);
			player.getActionSender().sendConfig(345, player.getPresets().isDeathOpen() ? 1 : 0);
			player.getActionSender().sendMessage("The preloading gear will " + (player.getPresets().isDeathOpen() ? "now" : "now not") + " open on death.");
			break;
		case 222232:
		case -8471:
			player.getPresets().setGearBank(player.getPresets().isGearBank() ? false : true);
			player.getActionSender().sendConfig(346, player.getPresets().isGearBank() ? 1 : 0);
			player.getActionSender().sendMessage("All equipment and inventory will " + (player.getPresets().isGearBank() ? "now" : "now not") + " automatically deposit into bank.");
			break;
			
		case 165179:
			player.getGameModeSelection().confirm(player);
			break;
			
		case 165162:
		case 165163:
		case 165164:
		case 165165:
			player.getGameModeSelection().selectMode(player, button);
			break;

		/**
		 * Exp counter 'reset exp'
		 */
		case 1219:
			player.getSkills().setExpCounter(0);
			player.getActionSender().sendExperienceCounter(0, 0);
			player.getActionSender().sendMessage(
					"You have reset your experience counter to zero.");
			break;

		/**
		 * Item destroy
		 */
		case 55095:
			handleDestroyItem(player);
			break;

		/** Bank */
		case 21010:
			player.setWithdrawAsNote(true);
			break;

		case 21011:
			player.setWithdrawAsNote(false);
			break;

		/** Settings */
		case 4026:
			player.getWalkingQueue().setRunningToggled(
					!player.getWalkingQueue().isRunningToggled());
			player.getActionSender().sendConfig(152,
					player.getWalkingQueue().isRunning() ? 1 : 0);
			break;

		case 3138:
			player.setScreenBrightness((byte) 1);
			break;
		case 3140:
			player.setScreenBrightness((byte) 2);
			break;
		case 3142:
			player.setScreenBrightness((byte) 3);
			break;
		case 3144:
			player.setScreenBrightness((byte) 4);
			break;

		case 140181:
			player.setAcceptAid(!player.getAcceptAid());
			player.getActionSender().sendConfig(200,
					player.getAcceptAid() ? 1 : 0);
			break;

		case 140185:
			player.setSplitPrivateChat(!player.getSplitPrivateChat());
			player.getActionSender().sendConfig(287,
					player.getSplitPrivateChat() ? 1 : 0);
			player.getActionSender().sendConfig(205,
					player.getSplitPrivateChat() ? 1 : 0);
			break;

		case 140186:
			player.setEnableSound(!player.isEnableSound());
			player.getActionSender().sendConfig(206,
					player.isEnableSound() ? 1 : 0);
			player.getActionSender().sendMessage(
					String.format("You have %s sound effects.",
							player.isEnableSound() ? "enabled" : "disabled"));
			break;

		case 140187:
			player.setEnableMusic(!player.isEnableMusic());
			player.getActionSender().sendConfig(207,
					player.isEnableMusic() ? 1 : 0);
			if (player.isEnableMusic()) {
				player.getActionSender().sendMessage(
						"You've enabled your music player.");
				MusicData.playMusic(player);
			} else if (!player.isEnableMusic()) {
				player.getActionSender().sendMessage(
						"You've disabled your music player.");
				player.getActionSender().sendSong(-1);
			}
			break;

		case 89061:
			// System.out.println("tick: " +player.isAutoRetaliating());
			player.setAutoRetaliating(!player.isAutoRetaliating());
			player.getCombatState().reset();
			break;
		case 114226:
			QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
			player.getActionSender().sendMessage(
					"You refresh your information tab.");
			break;
		/** Equipment screen */
		case 108005:
			player.getActionSender().sendInterface(15106);
			break;
		/* Logout */
		case 9154:
			player.logout();
			break;
		case 108006:
			if (player.getLastCombatAction().elapsed(0)) {
				ItemsKeptOnDeath.open(player);
			} else {
				player.getActionSender().sendMessage(
						"You cannot view items kept on death while in combat!");
			}
			break;

		/** Dialogue options */
		case 9190:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(1)) {
					break;
				}
			}
			break;
		case 9191:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(2)) {
					break;
				}
			}
			break;

		case 9192:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(3)) {
					break;
				}
			}
			break;

		case 9193:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(4)) {
					break;
				}
			}
			break;

		case 9194:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(5)) {
					break;
				}
			}
			break;

		case 9167:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(1)) {
					break;
				}
			}
			break;

		case 9168:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(2)) {
					break;
				}
			}
			break;

		case 9169:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(3)) {
					break;
				}
			}
			break;

		case 9178:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(1)) {
					break;
				}
			}
			break;

		case 9179:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(2)) {
					break;
				}
			}
			break;

		case 9180:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(3)) {
					break;
				}
			}
			break;

		case 9181:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(4)) {
					break;
				}
			}
			break;

		case 9157:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(1)) {
					break;
				}
			}
			break;

		case 9158:
			if (player.dialogue().isActive()) {
				if (player.dialogue().select(2)) {
					break;
				}
			}
			break;
		}
	}

	private static void handleDestroyItem(Player player) {
		if (player.getDestroyItem() != -1) {
			Item item = player.getInventory().get(player.getDestroyItem());
			if (item != null) {
				player.getInventory().remove(item);
				player.setDestroyItem(-1);
				player.getActionSender().removeAllInterfaces();
			}
		}
	}
}
