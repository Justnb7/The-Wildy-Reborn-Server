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
import com.venenatis.server.Server;

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
			player.debug("duel blocking");
			return;
		}
		
		/* Minigames */
		MinigameHandler.execute(player, $it -> $it.onButtonClick(player, button));
		
		/* Trading */
		if (player.getTradeSession().isTrading()) {
			player.getTradeSession().onButtonClick(button);
			player.debug("trade blocking");
			return;
		}
		
		/* Banking */
		if (player.getBank().clickButton(button)) {
			player.debug("bank blocking");
			return;
		}
		
		/* Price checker */
		if(player.getPriceChecker().buttonAction(button)) {
			player.debug("pc blocking");
			return;
		}
		
		/* Drop Tables */
		if (button >= 166035 && button < 166035 + DropManager.AMOUNT_OF_TABLES) {
			Server.getDropManager().select(player, button);
			player.debug("drop blocking");
			return;
		}
		
		/* Clan Chat */
		if (ClanButtons.handle(player, button)) {
			player.debug("clan blocking");
			return;
		}
		
		/* Slayer Actions */
		if (player.getSlayerInterface().controlPanel(player, button)) {
			player.debug("slayer blocking");
			return;
		}

		/* Emotes */
		if (player != null) {
			EmoteData.useBookEmote(player, button);
		}
		
		/* Teleporting */
		if(Teleport.isTeleportButton(player, button)) {
			player.getTeleportAction().handleButtons(button);
			player.debug("teleport blocking");
			return;
		}
		
		/* Special attacks */
		if (SpecialAttackHandler.handleButtons(player, button)) {
			player.debug("spec blocking");
			return;
		}
		
		/* Attack style */
		if (FightType.setStyle(player, button)) {
			player.debug("attk blocking");
			return;
		}
		
		/* Fletching */
		if (Fletching.SINGLETON.clickButton(player, button)) {
			player.debug("fletcing blocking");
			return;
		}
		
		/* Prayer */
		if(!PrayerHandler.togglePrayer(player, button)) {
			player.debug("prayer blocking");
			return;
		}
		
		/* Handle clickable spells such as Vengeance */
		if(Magic.handleButton(player, button)) {
			player.debug("magic blocking");
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
