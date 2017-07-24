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
		System.out.println("enter "+button);
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
		case 66207:
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
			player.getPresets().uploadCheck(player.getPresets().getViewing());
			break;
		case 222227:
			player.getPresets().clear(player.getPresets().getViewing());
			break;
		case 222230:
			player.getPresets().setDeathOpen(player.getPresets().isDeathOpen() ? false : true);
			player.getActionSender().sendConfig(345, player.getPresets().isDeathOpen() ? 1 : 0);
			player.getActionSender().sendMessage("The preloading gear will " + (player.getPresets().isDeathOpen() ? "now" : "now not") + " open on death.");
			break;
		case 222232:
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

		case FIRST_DIALOGUE_OPTION_OF_FIVE:
		case FIRST_DIALOGUE_OPTION_OF_FOUR:
		case FIRST_DIALOGUE_OPTION_OF_THREE:
		case FIRST_DIALOGUE_OPTION_OF_TWO:
			if(player.getDialogueOptions() != null) {
				player.getDialogueOptions().handleOption(player, 1);
			}
			break;

		case SECOND_DIALOGUE_OPTION_OF_FIVE:
		case SECOND_DIALOGUE_OPTION_OF_FOUR:
		case SECOND_DIALOGUE_OPTION_OF_THREE:
		case SECOND_DIALOGUE_OPTION_OF_TWO:
			if(player.getDialogueOptions() != null) {
				player.getDialogueOptions().handleOption(player, 2);
			}
			break;

		case THIRD_DIALOGUE_OPTION_OF_FIVE:
		case THIRD_DIALOGUE_OPTION_OF_FOUR:
		case THIRD_DIALOGUE_OPTION_OF_THREE:
			if(player.getDialogueOptions() != null) {
				player.getDialogueOptions().handleOption(player, 3);
			}
			break;

		case FOURTH_DIALOGUE_OPTION_OF_FIVE:
		case FOURTH_DIALOGUE_OPTION_OF_FOUR:
			if(player.getDialogueOptions() != null) {
				player.getDialogueOptions().handleOption(player, 4);
			}
			break;

		case FIFTH_DIALOGUE_OPTION_OF_FIVE:
			if(player.getDialogueOptions() != null) {
				player.getDialogueOptions().handleOption(player, 5);
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
	
	// Dialogues
	private static final int FIRST_DIALOGUE_OPTION_OF_FIVE = 9190;
	private static final int SECOND_DIALOGUE_OPTION_OF_FIVE = 9191;
	private static final int THIRD_DIALOGUE_OPTION_OF_FIVE = 9192;
	private static final int FOURTH_DIALOGUE_OPTION_OF_FIVE = 9193;
	private static final int FIFTH_DIALOGUE_OPTION_OF_FIVE = 9194;

	private static final int FIRST_DIALOGUE_OPTION_OF_FOUR = 9178;
	private static final int SECOND_DIALOGUE_OPTION_OF_FOUR = 9179;
	private static final int THIRD_DIALOGUE_OPTION_OF_FOUR = 9180;
	private static final int FOURTH_DIALOGUE_OPTION_OF_FOUR = 9181;

	private static final int FIRST_DIALOGUE_OPTION_OF_THREE = 9167;
	private static final int SECOND_DIALOGUE_OPTION_OF_THREE = 9168;
	private static final int THIRD_DIALOGUE_OPTION_OF_THREE = 9169;

	private static final int FIRST_DIALOGUE_OPTION_OF_TWO = 9157;
	private static final int SECOND_DIALOGUE_OPTION_OF_TWO = 9158;
}
