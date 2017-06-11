package com.model.net.packet.in;

import com.model.game.character.Animation;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.weapon.AttackStyle.FightType;
import com.model.game.character.combat.weaponSpecial.SpecialAttackHandler;
import com.model.game.character.npc.drops.DropManager;
import com.model.game.character.player.EmotesManager.EmoteData;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.ItemsKeptOnDeath;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.content.music.MusicData;
import com.model.game.character.player.content.questtab.QuestTabPage;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.content.teleport.TeleportHandler;
import com.model.game.character.player.content.teleport.TeleportHandler.TeleportationTypes;
import com.model.game.character.player.content.teleport.Teleports;
import com.model.game.character.player.skill.fletching.Fletching;
import com.model.game.item.Item;
import com.model.net.packet.PacketType;
import com.model.net.packet.buttons.ActionButtonEventListener;
import com.model.server.Server;
import com.model.utility.Utility;

import java.util.Objects;

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
		
		DuelSession duelSession = null;
		
		if (player.isDead()) {
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
		
		// First verify this button is something even remotely related to teleports
		if(Teleports.isTeleportButton(player, button)) {
			// Activate a teleport for that button
			Teleports.startTeleport(player, button);
			return;
		}
		
		if (SpecialAttackHandler.handleButtons(player, button)) {
			return;
		}
		
		if (FightType.setStyle(player, button)) {
			return;
		}
		
		if (ClanManager.handleButtons(player, button)) {
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
		
		case 114220:
			player.getAchievements().drawInterface(0);
			break;
			
		case 191109:
			player.getAchievements().drawInterface(0);
			break;
			
		case 191112:
			player.getAchievements().drawInterface(1);
			break;
			
		case 191115:
			player.getAchievements().drawInterface(2);
			break;
		
		case 222174:
			TeleportHandler.open(player, TeleportationTypes.SKILLING);
			break;
		case 222178:
			TeleportHandler.open(player, TeleportationTypes.PVP);
			break;
		case 222182:
			TeleportHandler.open(player, TeleportationTypes.PVM);
			break;
		case 222186:
			TeleportHandler.open(player, TeleportationTypes.MINIGAME);
			break;
		case 222195:
			TeleportHandler.teleport(player);
			break;
			
		case 222219:
		case 222223:
		case 222227:
		case 222231:
		case 222235:
		case 222239:
		case 222243:
		case 222247:
		case 222251:
		case 222255:
		case 223003:
		case 223007:
		case 223011:
		case 223015:
		case 223019: 
		case 223023:
		case 223027:
		case 223031:
		case 223035:
		case 223039:
		case 223043:
		case 223047:
			TeleportHandler.select(player, button);
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
			if(player.getArea().inWild()) {
				return;
			}
			if (player.onAuto) {
				player.getActionSender().sendMessage("You can't switch spellbooks with Autocast enabled.");
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBook.ANCIENT);
				player.getActionSender().sendSidebarInterface(6, 12855);
				player.getActionSender().sendMessage("An ancient wisdom fills your mind.");
				break;
			case ANCIENT:
				player.setSpellBook(SpellBook.LUNAR);
				player.getActionSender().sendSidebarInterface(6, 29999);
				player.getActionSender().sendMessage("The power of the moon overpowers you.");
				break;
			case LUNAR:
				player.setSpellBook(SpellBook.MODERN);
				player.getActionSender().sendSidebarInterface(6, 1151);
				player.getActionSender().sendMessage("You feel a drain on your memory.");
				break;
			}
			player.autocastId = -1;
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
			player.setRunning(!player.isRunning());
			//player.getActionSender().sendConfig(152, player.isRunning() ? 1 : 0));
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
			if (player.autocastId > 0) {
				player.resetAutoCast();
			} else {
				if (player.getSpellBook() == SpellBook.ANCIENT) {
					player.getActionSender().sendSidebarInterface(0, 1689);
				} else if (player.getSpellBook() == SpellBook.MODERN) {
					player.getActionSender().sendSidebarInterface(0, 12050);
				}
			}
			break;
			
			/** Dueling **/
			case 26065:
			case 26040:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.FORFEIT);
				break;
			case 26066: // no movement
			case 26048:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				if (!duelSession.getRules().contains(Rule.FORFEIT)) {
					duelSession.toggleRule(player, Rule.FORFEIT);
				}
				duelSession.toggleRule(player, Rule.NO_MOVEMENT);
				break;

			case 26069: // no range
			case 26042:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_RANGE);
				break;

			case 26070: // no melee
			case 26043:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_MELEE);
				break;

			case 26071: // no mage
			case 26041:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_MAGE);
				break;

			case 26072: // no drinks
			case 26045:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_DRINKS);
				break;

			case 26073: // no food
			case 26046:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_FOOD);
				break;

			case 26074: // no prayer
			case 26047:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_PRAYER);
				break;

			case 26076: // obsticals
			case 26075:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.OBSTACLES);
				break;

			case 2158: // fun weapons
			case 2157:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				if (duelSession.getRules().contains(Rule.WHIP_AND_DDS)) {
					duelSession.toggleRule(player, Rule.WHIP_AND_DDS);
					return;
				}
				if (!Rule.WHIP_AND_DDS.getReq().get().meets(player)) {
					player.getActionSender().sendString("You must have a whip and dragon dagger to select this.", 6684);
					return;
				}
				if (!Rule.WHIP_AND_DDS.getReq().get().meets(duelSession.getOther(player))) {
					player.getActionSender().sendString("Your opponent does not have a whip and dragon dagger.", 6684);
					return;
				}
				if (duelSession.getStage().getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
					player.getActionSender().sendMessage("You cannot change rules whilst on the second interface.");
					return;
				}
				duelSession.getRules().reset();
				for (Rule rule : Rule.values()) {
					int index = rule.ordinal();
					if (index == 3 || index == 8 || index == 10 || index == 14) {
						continue;
					}
					duelSession.toggleRule(player, rule);
				}
				break;

			case 30136: // sp attack
			case 30137:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_SPECIAL_ATTACK);
				break;

			case 53245: // no helm
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_HELM);
				break;

			case 53246: // no cape
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_CAPE);
				break;

			case 53247: // no ammy
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_AMULET);
				break;

			case 53249: // no weapon
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_WEAPON);
				break;

			case 53250: // no body
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_BODY);
				break;

			case 53251: // no shield
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_SHIELD);
				break;

			case 53252: // no legs
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_LEGS);
				break;

			case 53255: // no gloves
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_GLOVES);
				break;

			case 53254: // no boots
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_BOOTS);
				break;

			case 53253: // no rings
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_RINGS);
				break;

			case 53248: // no arrows
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.isNull(duelSession)) {
					return;
				}
				duelSession.toggleRule(player, Rule.NO_ARROWS);
				break;

			case 26018:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (System.currentTimeMillis() - player.getDuel().getLastAccept() < 1000) {
					return;
				}
				if (Objects.nonNull(duelSession) && duelSession instanceof DuelSession) {
					duelSession.accept(player, MultiplayerSessionStage.OFFER_ITEMS);
				}
				break;

			case 25120:
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (System.currentTimeMillis() - player.getDuel().getLastAccept() < 1000) {
					return;
				}
				if (Objects.nonNull(duelSession) && duelSession instanceof DuelSession) {
					duelSession.accept(player, MultiplayerSessionStage.CONFIRM_DECISION);
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
