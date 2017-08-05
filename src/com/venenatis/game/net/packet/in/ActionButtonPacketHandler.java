package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.EmotesManager.EmoteData;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.content.clicking.Buttons;
import com.venenatis.game.content.quest_tab.QuestTabPage;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.skills.fletching.Fletching;
import com.venenatis.game.content.teleportation.Teleport;
import com.venenatis.game.content.teleportation.TeleportationInterface;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.AttackStyle.FightType;
import com.venenatis.game.model.combat.magic.Magic;
import com.venenatis.game.model.combat.special_attacks.SpecialAttackHandler;
import com.venenatis.game.model.entity.npc.drop_system.DropManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanButtons;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.net.packet.in.button.ActionButtonEventListener;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;;

/**
 * Handles clicking on most buttons in the interface.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ActionButtonPacketHandler implements PacketType {

	@Override
	public void handle(final Player player, int id, int size) {
		int button = Utility.hexToInt(player.getInStream().buffer, 0, size);
		//player.getInStream().readUnsignedWord();
		
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
		if(player.isTeleporting()) {
			return;
		}
		
		/**
		 * When we're in the tutorial we're only allowed to click the following buttons
		 */
		if (player.inTutorial() && button != 9178 && button != 9179 && button != 9180 && button != 9181 && button != 9154 && button != 165179 && button != 165162 && button != 165163 && button != 165164 && button != 165165) {
			return;
		}
		
		player.debug(String.format("ActionButtonPacket: button %d - packet %d - packetSize %d%n", button, id, size));
		
		/**
		 * We've passed all checks now we can activate our actions
		 */
		
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
		if (player != null) {
			EmoteData.useBookEmote(player, button);
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
		
		/* Prayer */
		if(!PrayerHandler.togglePrayer(player, button)) {
			return;
		}
		
		/* Handle clickable spells such as Vengeance */
		if(Magic.handleButton(player, button)) {
			return;
		}
		
		/* Custom teleporting interface */
		if(TeleportationInterface.actions(player, button)) {
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
