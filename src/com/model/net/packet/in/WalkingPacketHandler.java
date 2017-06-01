package com.model.net.packet.in;

import com.model.Server;
import com.model.game.character.Entity;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.location.Location;
import com.model.net.packet.PacketType;

import java.util.Objects;

/**
 * Walking packet
 **/
public class WalkingPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		//We can't walk because of the following reasons
		if (player.isDead() || !player.getController().canMove(player) || player.inTutorial() || player.teleTimer > 0
				|| player.isTeleporting() || player.isMapRegionChanging() || player.isForcedMovement() || player.hasAttribute("busy")) {
			return;
		}
		
		if (player.getInterfaceState().getCurrentInterface() > 0) {
			player.getActionSender().removeAllInterfaces();
		}
		
		Entity combattarg = player.getCombatState().getTarget();
		//We're frozen we can't walk
		if (player.frozen()) {
			if (combattarg != null) {
				if (player.goodDistance(player.getX(), player.getY(), combattarg.getX(), combattarg.getY(), 1) && packetType != 98) {
					player.getCombatState().reset();
					return;
				}
			}
			if (packetType != 98) {
				player.getActionSender().sendMessage("A magical force stops you from moving.");
				player.getCombatState().reset();
			}
			return;
		}
		
		//Set our controller
		player.getController().onWalk(player);
		
		//When walking we have to close all open interfaces.
		player.getActionSender().removeAllInterfaces();
		
		//Stop our distanced action task because we reset the walking queue by walking
		player.stopDistancedTask();
		
		//Stop active skilling tasks
		player.removeAttribute("fishing");
		player.stopSkillTask();
		
		if (player.getSkillCyclesTask().isSkilling()) {
			player.getSkillCyclesTask().stop();
		}
		
		//When walking during a trade we don't decline trades
		if (player.isTrading()) {
			player.getTradeSession().declineTrade(false);
		}
		
		//When walking we close our duel invitation
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);

		if (session != null && Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			if (session.getRules().contains(Rule.NO_MOVEMENT)) {
				return;
			}
		}
		if (Objects.nonNull(session) && session.getStage().getStage() > MultiplayerSessionStage.REQUEST && session.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.getActionSender().sendMessage("You have declined the duel.");
			session.getOther(player).getActionSender().sendMessage("The challenger has declined the duel.");
			session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
		
		//When walking reset the following variables
		if (packetType == 248 || packetType == 164) {
			player.faceEntity(null);
			player.getCombatState().reset();
			Combat.resetCombat(player);
			player.setFollowing(null);
		}
		
		// Packet 248 is either clicking on the minimap or a npc/object/player
		// It has 14 less bytes at the start compared to normal walking, so we skip these.
		if (packetType == 248) {
			packetSize -= 14;
		}
		
		//We're walking to our target
		int steps = (packetSize - 5) / 2;
		if (steps < 0)
			return;
		int[][] path = new int[steps][2];
		int firstStepX = player.getInStream().readSignedWordBigEndianA();
		for (int i = 0; i < steps; i++) {
			path[i][0] = player.getInStream().readSignedByte();
			path[i][1] = player.getInStream().readSignedByte();
		}
		int firstStepY = player.getInStream().readSignedWordBigEndian();

		player.getMovementHandler().reset();
		player.getMovementHandler().setRunPath(player.getInStream().readSignedByteC() == 1);
		player.getMovementHandler().addToPath(new Location(firstStepX, firstStepY, 0));
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
			player.getMovementHandler().addToPath(new Location(path[i][0], path[i][1], 0));
		}
		//We've reached our destination
		player.getMovementHandler().finish();
	}
}