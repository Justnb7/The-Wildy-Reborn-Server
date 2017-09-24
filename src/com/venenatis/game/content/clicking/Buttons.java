package com.venenatis.game.content.clicking;

import com.venenatis.game.content.ItemsKeptOnDeath;
import com.venenatis.game.content.achievements.AchievementHandler.AchievementDifficulty;
import com.venenatis.game.content.achievements.AchievementInterface;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.interfaces.InterfaceWriter;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.sounds_and_music.MusicData;
import com.venenatis.game.content.teleportation.TeleportHandler;
import com.venenatis.game.content.teleportation.TeleportHandler.TeleportationTypes;
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
		System.out.println("btn "+button);
		switch (button) {
		
		case 1220:
			if (!player.showDamage()) {
				player.setShowDamage(true);
				player.getActionSender().sendMessage("Your exp counter will now show your hits instead of exp.");
			} else {
				player.setShowDamage(false);
				player.getActionSender().sendMessage("Your exp counter will now show your received experience instead of hits.");
			}
			break;
		
		case 53150:
			
			break;
		
		case 9190:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(1)) {
					break;
				}
			}
			break;
		case 9191:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(2)) {
					break;
				}
			}
			break;

		case 9192:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(3)) {
					break;
				}
			}
			break;

		case 9193:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(4)) {
					break;
				}
			}
			break;
			
		case 9194:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(5)) {
					break;
				}
			}
			break;

		case 9167:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(1)) {
					break;
				}
			}
			break;

		case 9168:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(2)) {
					break;
				}
			}
			break;

		case 9169:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(3)) {
					break;
				}
			}
			break;

		case 9178:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(1)) {
					break;
				}
			}
			break;

		case 9179:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(2)) {
					break;
				}
			}
			break;

		case 9180:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(3)) {
					break;
				}
			}
			break;

		case 9181:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(4)) {
					break;
				}
			}
			break;

		case 9157:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(1)) {
					break;
				}
			}
			break;

		case 9158:
			if (player.getDialogueManager().isActive()) {
				if (player.getDialogueManager().select(2)) {
					break;
				}
			}
			break;
		
		case 113230:
			InterfaceWriter.write(new AchievementInterface(player, AchievementDifficulty.EASY));
			AchievementInterface.sendInterfaceForAchievement(player, AchievementList.FIRST_KILL);
			player.getAttributes().put("ACHIEVEMENT_PAGE", AchievementDifficulty.EASY);
			player.getActionSender().sendInterface(35_000);
			break;
		
		/* Achievement */
		case 136201:
			InterfaceWriter.write(new AchievementInterface(player, AchievementDifficulty.EASY));
			AchievementInterface.sendInterfaceForAchievement(player, AchievementList.FIRST_KILL);
			player.getAttributes().put("ACHIEVEMENT_PAGE", AchievementDifficulty.EASY);
			player.getActionSender().sendInterface(35_000);
			break;
			
		case 136204:
			InterfaceWriter.write(new AchievementInterface(player, AchievementDifficulty.MEDIUM));
			AchievementInterface.sendInterfaceForAchievement(player, AchievementList.LEARNING_CURVE);
			player.getAttributes().put("ACHIEVEMENT_PAGE", AchievementDifficulty.MEDIUM);
			player.getActionSender().sendInterface(35_000);
			break;
			
		case 136207:
			InterfaceWriter.write(new AchievementInterface(player, AchievementDifficulty.HARD));
			AchievementInterface.sendInterfaceForAchievement(player, AchievementList.MASTER);
			player.getAttributes().put("ACHIEVEMENT_PAGE", AchievementDifficulty.HARD);
			player.getActionSender().sendInterface(35_000);
			break;
		
		/* Teleport */
		case 3082:
			TeleportHandler.open(player, player.getTeleportationType() == null ? TeleportationTypes.SKILLING : player.getTeleportationType());
			break;

		case 226150:
			TeleportHandler.open(player, TeleportationTypes.SKILLING);
			break;

		case 226154:
			TeleportHandler.open(player, TeleportationTypes.PVP);
			break;

		case 226158:
			TeleportHandler.open(player, TeleportationTypes.PVM);
			break;

		case 226162:
			TeleportHandler.open(player, TeleportationTypes.MINIGAME);
			break;

		case 226171:
			TeleportHandler.teleport(player);
			break;

		case 226195:
		case 226199:
		case 226203:
		case 226207:
		case 226211:
		case 226215:
		case 226219:
		case 226223:
		case 226227:
		case 226231:
		case 226235:
		case 226239:
		case 226243:
		case 226247:
			TeleportHandler.select(player, button);
			break;
		
		case 51061:
			player.getActionSender().sendInterface(37700);
			break;

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
		case 210242:
		case 226146:
		case 144141:
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
		case 152:
			player.getWalkingQueue().setRunningToggled(!player.getWalkingQueue().isRunningToggled());
			player.getActionSender().sendConfig(152, player.getWalkingQueue().isRunning() ? 1 : 0);
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

		case 48167:
			player.setAcceptAid(!player.getAcceptAid());
			player.getActionSender().sendConfig(200, player.getAcceptAid() ? 1 : 0);
			break;

		case 62237:
			player.setSplitPrivateChat(!player.getSplitPrivateChat());
			player.getActionSender().sendConfig(287, player.getSplitPrivateChat() ? 1 : 0);
			player.getActionSender().sendConfig(205, player.getSplitPrivateChat() ? 1 : 0);
			break;

		case 140186:
			player.setEnableSound(!player.isEnableSound());
			player.getActionSender().sendConfig(206, player.isEnableSound() ? 1 : 0);
			player.getActionSender().sendMessage(String.format("You have %s sound effects.", player.isEnableSound() ? "enabled" : "disabled"));
			break;

		case 140187:
			player.setEnableMusic(!player.isEnableMusic());
			player.getActionSender().sendConfig(207, player.isEnableMusic() ? 1 : 0);
			if (player.isEnableMusic()) {
				player.getActionSender().sendMessage("You've enabled your music player.");
				MusicData.playMusic(player);
			} else if (!player.isEnableMusic()) {
				player.getActionSender().sendMessage("You've disabled your music player.");
				player.getActionSender().sendSong(-1);
			}
			break;

		case 94051:
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
			if (!player.getCombatState().inCombat()) {
				player.getEquipment().setBonus();
				player.getActionSender().sendInterface(15106);
			} else {
				player.getActionSender().sendMessage("You cannot open the equip screen while in combat!");
			}
			break;
			
		/** Items kept on death  */	
		case 108006:
			if (!player.getCombatState().inCombat()) {
				ItemsKeptOnDeath.open(player);
			} else {
				player.getActionSender().sendMessage("You cannot view items kept on death while in combat!");
			}
			break;
			
		/** Call follower  */		
		case 108020:
			if (player.getPet() > -1) {
				//TODO ask Jak how to call the pet here
			} else {
				player.getActionSender().sendMessage("You do not have a follower.");
			}
			break;
			
		/* Logout */
		case 9154:
			player.logout();
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
