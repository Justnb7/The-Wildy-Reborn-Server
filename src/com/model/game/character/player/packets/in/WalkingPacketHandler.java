package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendRemoveInterface;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.location.Position;

/**
 * Walking packet
 **/
public class WalkingPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		//We can't walk because of the following reasons
		if (player.isDead() || !player.getController().canMove(player) || player.inTutorial() || player.teleTimer > 0
				|| player.isTeleporting() || player.mapRegionDidChange || player.getMovementHandler().isForcedMovement()
				|| player.playerStun) {
			return;
		}
		
		//we have a bank pin, so we can't walk
		if (player.getBankPin().requiresUnlock()) {
			player.setBanking(false);
			player.getBankPin().open(2);
			return;
		}
		
		//We're frozen we can't walk
		if (player.frozen()) {
			if (World.getWorld().getPlayers().get(player.playerIndex) != null) {
				if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 1) && packetType != 98) {
					player.playerIndex = 0;
					return;
				}
			}
			if (packetType != 98) {
				player.write(new SendMessagePacket("A magical force stops you from moving."));
				player.playerIndex = 0;
			}
			return;
		}
		
		//Set our controller
		player.getController().onWalk(player);
		
		//When walking we have to close all open interfaces.
		player.write(new SendRemoveInterface());
		
		//Stop our distanced action task because we reset the walking queue by walking
		player.stopDistancedTask();
		
		//PI logic?
		if (player.canChangeAppearance) {
			player.canChangeAppearance = false;
		}
		
		//When walking stop our skilling action
		if (player.getSkilling().isSkilling()) {
			player.getSkilling().stop();
		}
		
		//When walking during a trade we decline
		if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
		
		//When walking we close our duel invitation
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);

		if (session != null && Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			if (session.getRules().contains(Rule.NO_MOVEMENT)) {
				return;
			}
		}
		if (Objects.nonNull(session) && session.getStage().getStage() > MultiplayerSessionStage.REQUEST && session.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.write(new SendMessagePacket("You have declined the duel."));
			session.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
			session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
		
		//When walking reset the following variables
		if (packetType == 248 || packetType == 164) {
			player.faceUpdate(0);
			player.npcIndex = 0;
			player.clickObjectType = 0;
			player.walkingToObject = false;
			player.clickNpcType = 0;
			player.playerIndex = 0;
			player.setOpenShop(null);
			player.mageFollow = false;
			Combat.resetCombat(player);
			if (player.followId > 0 || player.followId2 > 0) {
				player.getPA().resetFollow();
			}
		}
		
		//Don't know what this does
		if (packetType == 98) {
			player.walkingToObject = true;
			player.mageAllowed = true;
		}
		
		//Don't know what this does either
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
		player.getMovementHandler().addToPath(new Position(firstStepX, firstStepY, 0));
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
			player.getMovementHandler().addToPath(new Position(path[i][0], path[i][1], 0));
		}
		//We've reached our destination
		player.getMovementHandler().finish();
	}
}