package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.ItemsKeptOnDeath;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.content.EmotesManager.EmoteData;
import com.venenatis.game.content.quest_tab.QuestTabPage;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.skills.fletching.Fletching;
import com.venenatis.game.content.sounds_and_music.MusicData;
import com.venenatis.game.content.teleportation.Teleport;
import com.venenatis.game.content.teleportation.Teleport.SpellBookTypes;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.AttackStyle.FightType;
import com.venenatis.game.model.combat.special_attacks.SpecialAttackHandler;
import com.venenatis.game.model.entity.npc.drop_system.DropManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanButtons;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.net.packet.in.button.ActionButtonEventListener;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

/**
 * Handles clicking on most buttons in the interface.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ActionButtonPacketHandler implements PacketType {
	
	private static final int OPEN_ITEMS_KEPT_ON_DEATH_SCREEN = 108006;

	@Override
	public void handle(final Player player, int id, int size) {
		int button = Utility.hexToInt(player.getInStream().buffer, 0, size);
		
		if (player.getCombatState().isDead()) {
			return;
		}
		
		if (player.inDebugMode()) {
			System.out.printf("ActionButtonPacket: button %d - packet %d - packetSize %d%n", button, id, size);
		}
		
		if (button == 165179) {
			player.getGameModeSelection().confirm(player);
			return;
		}
		
		if (button >= 165162 && button <= 165165) {
			player.getGameModeSelection().selectMode(player, button);
			return;
		}
		
		if (player.inTutorial() && button != 9178 && button != 9179 && button != 9180 && button != 9181
				&& button != 14067 && button != 9154) {
			return;
		}
		
		/* Controller Block */
		if (!player.getController().canClickButton(button)) {
			return;
		}
		
		/* Clan Chat */
		if (ClanButtons.handle(player, button)) {
			return;
		}
		
		if (player.getSlayerInterface().controlPanel(player, button)) {
			return;
		}
		
		if(player.getPriceChecker().buttonAction(button)){
			return;
		}
		
		if (button >= 166035 && button < 166035 + DropManager.AMOUNT_OF_TABLES) {
			Server.getDropManager().select(player, button);
			return;
		}

		if (player != null) {
			EmoteData.useBookEmote(player, button);
		}
		
		/* Banking */
		if (player.getBank().clickButton(button)) {
			return;
		}
		
		/* Trading */
		if (player.getTradeSession().isTrading()) {
			player.getTradeSession().onButtonClick(button);
			return;
		}
		
		/* Dueling */
		if (player.getDuelArena().isInSession() || player.getDuelArena().getStage() == DuelStage.REWARD) {
			player.getDuelArena().handleButton(button);
			return;
		}

		/* Minigames */
		MinigameHandler.execute(player, $it -> $it.onButtonClick(player, button));
		
		// First verify this button is something even remotely related to teleports
		if(Teleport.isTeleportButton(player, button)) {
			// Activate a teleport for that button
			player.getTeleportAction().handleButtons(button);
			return;
		}
		
		if (SpecialAttackHandler.handleButtons(player, button)) {
			return;
		}
		
		if (FightType.setStyle(player, button)) {
			return;
		}
		
		if (Fletching.SINGLETON.clickButton(player, button)) {
			return;
		}
		/*Obelisks.chooseTeleport(player, button);*/
		PrayerHandler.togglePrayer(player, button);
		player.getLunarSpell().processLunarSpell(button);
		QuestTabPage page = player.getAttribute(QuestTabPageHandler.QUEST_TAB_PAGE, QuestTabPages.HOME_PAGE).getPage();
		page.onButtonClick(player, button);
		// AttackStyle.switchAttackStyle(player, buttonId);
		ActionButtonEventListener.onButtonClick(player, button);
		switch (button) {
		
		case OPEN_ITEMS_KEPT_ON_DEATH_SCREEN:
			if (player.getLastCombatAction().elapsed(0)) {
				ItemsKeptOnDeath.open(player);
			} else {
				player.getActionSender().sendMessage("You cannot view items kept on death while in combat!");
			}
			break;
		
		/**
		 * Exp counter 'reset exp'
		 */
		case 1219:
			player.getSkills().setExpCounter(0);
			player.getActionSender().sendExperienceCounter(0, 0);
			player.getActionSender().sendMessage("You have reset your experience counter to zero.");
			break;

		case 114230:
			if(Area.inWilderness(player)) {
				return;
			}
			if (player.onAuto) {
				player.getActionSender().sendMessage("You can't switch spellbooks with Autocast enabled.");
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBookTypes.ANCIENTS);
				player.getActionSender().sendSidebarInterface(6, 12855);
				player.getActionSender().sendMessage("An ancient wisdom fills your mind.");
				break;
			case ANCIENTS:
				player.setSpellBook(SpellBookTypes.LUNARS);
				player.getActionSender().sendSidebarInterface(6, 29999);
				player.getActionSender().sendMessage("The power of the moon overpowers you.");
				break;
			case LUNARS:
				player.setSpellBook(SpellBookTypes.MODERN);
				player.getActionSender().sendSidebarInterface(6, 1151);
				player.getActionSender().sendMessage("You feel a drain on your memory.");
				break;
			}
			player.setAutocastId(-1);
			player.resetAutoCast();
			player.onAuto = false;
			break;

		case 114226:
			QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
			player.getActionSender().sendMessage("You refresh your information tab.");
			break;

		case 19137:
			player.getActionSender().sendSidebarInterface(5, 17200);
			break;

		case 104078:
			player.getActionSender().sendSidebarInterface(3, 3213);
			break;
			

		/** Equipment screen */
		case 108005:
			player.getActionSender().sendInterface(15106);
			break;

		case 108020:
			break;

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
			
		case 142131:
			player.getActionSender().sendSidebarInterface(3, 3213);
			break;
			
		case 55095:
			handleDestroyItem(player);
			break;

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

		case 9154:
			player.logout();
			break;

		case 105230:
			player.getActionSender().removeAllInterfaces();
			break;

		case 21010:
			player.setWithdrawAsNote(true);
			break;
			
		case 21011:
			player.setWithdrawAsNote(false);
			break;
			
		/*case 31194:
			player.setInsertItem(true);
			break;
			
		case 31195:
			player.setInsertItem(false);
			break;*/

		case 39178:
			player.playAnimation(Animation.create(65535));
			player.getActionSender().removeAllInterfaces();
			break;

		case 59004:
			player.getActionSender().removeAllInterfaces();
			break;

		case 70212:
			player.getActionSender().sendInterface(18300);
			break;

		/** Settings */
		case 140188:
			player.getActionSender().sendMessage(":updateSettings:");
			player.getActionSender().sendSidebarInterface(11, 28400);
			break;
		case 110245:
			player.getActionSender().sendMessage(":saveSettings:");
			player.getActionSender().sendSidebarInterface(11, 36000);
			player.getActionSender().sendMessage("@red@Your settings have been saved!");
			break;
		case 110248:
			player.getActionSender().sendMessage(":defaultSettings:");
			player.getActionSender().sendMessage("@red@Your settings have been reset!");
			break;
		case 140191:
			player.getActionSender().sendInterface(28200);
			break;
		case 110046:
			player.getActionSender().sendMessage(":transparentTab:");
			break;
		case 110047:
			player.getActionSender().sendMessage(":transparentChatbox:");
			break;
		case 110048:
			player.getActionSender().sendMessage(":sideStones:");
			break;

		case 4026:
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

		case 140181:
			player.setAcceptAid(!player.getAcceptAid());
			player.getActionSender().sendConfig(200, player.getAcceptAid() ? 1 : 0);
			break;	
			
		case 140185:
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
			if(player.isEnableMusic()) {
				player.getActionSender().sendMessage("You've enabled your music player.");
				MusicData.playMusic(player);
			} else if(!player.isEnableMusic()) {
				player.getActionSender().sendMessage("You've disabled your music player.");
				player.getActionSender().sendSong(-1);
			}
			break;

		case 89061:
			//System.out.println("tick: " +player.isAutoRetaliating());
			player.setAutoRetaliating(!player.isAutoRetaliating());
			player.getCombatState().reset();
			break;
			
		case 7217:
		case 7212:
		case 24017:
			player.resetAutoCast();
			break;
			
		case 1093:
		case 1094:
		case 1097:
			player.onAuto = true;
			if (player.getAutocastId() > 0) {
				player.resetAutoCast();
			} else {
				if (player.getSpellBook() == SpellBookTypes.ANCIENTS) {
					player.getActionSender().sendSidebarInterface(0, 1689);
				} else if (player.getSpellBook() == SpellBookTypes.MODERN) {
					player.getActionSender().sendSidebarInterface(0, 12050);
				}
			}
			break;
				
		}
		if (player.isAutoButton(button)) {
			player.assignAutocast(button);
		}
	}

	private void handleDestroyItem(Player player) {
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
