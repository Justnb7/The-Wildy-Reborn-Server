package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.Emotes;
import com.venenatis.game.content.SetSkill;
import com.venenatis.game.content.achievements.AchievementButtons;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.content.clicking.Buttons;
import com.venenatis.game.content.help.HelpDatabase;
import com.venenatis.game.content.quest_tab.QuestTabPage;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.skills.cooking.Cooking;
import com.venenatis.game.content.skills.crafting.Crafting;
import com.venenatis.game.content.skills.fletching.Fletching;
import com.venenatis.game.content.skills.smithing.SmithingConstants;
import com.venenatis.game.content.teleportation.Teleport;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.AttackStyle.FightType;
import com.venenatis.game.model.combat.magic.Autocast;
import com.venenatis.game.model.combat.special_attacks.SpecialAttackHandler;
import com.venenatis.game.model.entity.npc.drop_system.DropManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanButtons;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.net.packet.in.button.ActionButtonEventListener;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

/**
 * Handles clicking on most buttons in the interface.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ActionButtonPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(final Player player, int id, int size) {
		int button = Utility.hexToInt(player.getInStream().buffer, 0, size);
		//player.getInStream().readUnsignedWord();
		if(player.isJailed()) {
			return;
		}
		
		if (player.getAttribute("busy") != null && player.receivedStarter()) {
			return;
		}
		
		/**
		 * How are we going to click buttons when we're dead!
		 */
		if (player.getCombatState().isDead()) {
			return;
		}
		
		/**
		 * In certain controllers we're unable to use clicking buttons
		 */
		if (!player.getController().canClickButton(button)) {
			return;
		}
		
		/**
		 * We can't click buttons during teleporting
		 */
		if(player.getTeleportAction().isTeleporting()) {
			return;
		}
		
		player.debug(String.format("ActionButtonPacket: button %d - packet %d - packetSize %d%n", button, id, size));
		
		/**
		 * We've passed all checks now we can activate our actions
		 */
		/* Help database */
		if (button >= 232182 && button <= 233022) {
			HelpDatabase.getDatabase().view(player, button);
			HelpDatabase.getDatabase().delete(player, button);
			return;
		}
		
		/* Titles */
		if (player.getTitles().click(button)) {
			return;
		}
		
		/* Autocasting */
		if (Autocast.isAutoButton(button)) {
			Autocast.assignAutocast(player, button);
			return;
		}
		
		if (Autocast.handleActionButtons(player, button)) {
			return;
		}
		
		/* Smithing */
		if (SmithingConstants.clickSmeltSelection(player, button)) {
			return;
		}
		
		/* Set Skills */
		if (SetSkill.handle(player, button)) {
			return;
		}
		
		/* Cooking */
		if (Cooking.cook(player, button)) {
			return;
		}
		
		/* Achievements */
		if (AchievementButtons.handleButtons(player, button)) {
			return;
		}
		
		/* Dueling */
		if (player.getDuelArena().isInSession() || player.getDuelArena().getStage() == DuelStage.REWARD) {
			player.getDuelArena().handleButton(button);
			return;
		}
		
		/* Minigames */
		MinigameHandler.execute(player, $it -> $it.onButtonClick(player, button));
		
		/* Trading */
		if (player.getTradeSession().isTrading()) {
			player.getTradeSession().onButtonClick(button);
			return;
		}
		
		/* Banking */
		if (player.getBank().clickButton(button)) {
			return;
		}
		
		/* Price checker */
		if(player.getPriceChecker().buttonAction(button)) {
			return;
		}
		
		/* Drop Tables */
		if (button >= 166035 && button < 166035 + DropManager.AMOUNT_OF_TABLES) {
			Server.getDropManager().select(player, button);
			return;
		}
		
		/* Clan Chat */
		if (ClanButtons.handle(player, button)) {
			return;
		}
		
		/* Slayer Actions */
		if (player.getSlayerInterface().controlPanel(player, button)) {
			return;
		}

		/* Emotes */
		if (Emotes.isEmoteButton(player, button)) {
			Emotes.execute(player, button);
			return;
		}
		
		/* Teleporting */
		if(Teleport.isTeleportButton(player, button)) {
			player.getTeleportAction().handleButtons(button);
			return;
		}
		
		/* Special attacks */
		if (SpecialAttackHandler.handleButtons(player, button)) {
			return;
		}
		
		/* Attack style */
		if (FightType.setStyle(player, button)) {
			return;
		}
		
		/* Fletching */
		if (Fletching.SINGLETON.clickButton(player, button)) {
			return;
		}
		
		/* Crafting */
		if (Crafting.SINGLETON.clickButton(player, button)) {
			return;
		}
		
		/* Prayer */
		if(PrayerHandler.togglePrayer(player, button)) {
			return;
		}
		/* Quick Prayers */
		if(player.getQuickPrayers().handleButton(button)) {
			return;
		}
		
		/* Handle clickable spells such as Vengeance */
		if(player.getMagic().handleButton(player, button)) {
			return;
		}
		
		/* The pest control reward interface */
		if(player.getPestControlRewards().click(button)) {
			return;
		}
		
		/* Other Clicking Buttons */
		Buttons.handleButton(player, button);
		
		/* Quest book */
		QuestTabPage page = player.getAttribute(QuestTabPageHandler.QUEST_TAB_PAGE, QuestTabPages.HOME_PAGE).getPage();
		page.onButtonClick(player, button);
		
		/* Action buttons for events such as sounds, music etc...*/
		ActionButtonEventListener.onButtonClick(player, button);
	}

}
