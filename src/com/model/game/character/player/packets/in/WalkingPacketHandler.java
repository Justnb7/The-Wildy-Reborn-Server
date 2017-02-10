package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.Trading;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.location.Location;

/**
 * Walking packet
 **/
public class WalkingPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (packetType == 248 || packetType == 164) {
			player.faceUpdate(0);
			player.npcIndex = 0;
			player.clickObjectType = 0;
			player.walkingToObject = false;
			player.clickNpcType = 0;
			player.playerIndex = 0;
			player.setOpenShop(null);
			if (player.followId > 0 || player.followId2 > 0) {
				player.getPA().resetFollow();
			}
		}

		if (player.isDead()) {
			return;
		}
		
		if (player.inTutorial()) {
    		return;
    	}
		
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);

		if (session != null && Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			if (session.getRules().contains(Rule.NO_MOVEMENT)) {
				return;
			}
		}
		// ken if statement, this makes it so that when you click on the minimap
		// while in dueling screen, you decline the duel. not sure if other
		// abyssal/ospvp servers have this, or if i invented this
		if (Objects.nonNull(session) && session.getStage().getStage() > MultiplayerSessionStage.REQUEST && session.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			player.write(new SendMessagePacket("You have declined the duel."));
			session.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
			session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			// return;
		}
		
		/*
		 * Stop our distanced action task because we reset the walking queue by walking
		 */
		player.stopDistancedTask();
		
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		
		if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
		
		if (player.teleporting || player.mapRegionDidChange) {
			return;
		}
		
		if (player.getMovementHandler().isForcedMovement()) {
			// dont walk while we're force walking
			return;
		}
		
		if (player.canChangeAppearance) {
			player.canChangeAppearance = false;
		}
		
		if (player.getSkilling().isSkilling()) {
			player.getSkilling().stop();
		}
		
		Combat.resetCombat(player);
		player.setOpenShop(null);
		player.isSkilling = false;
		player.mageFollow = false;
		player.clickNpcType = 0;
		player.clickObjectType = 0;
		if (player.inItemOnDeath) {
			player.inItemOnDeath = false;
		}
		if (player.playerStun) {
			return;
		}
		if (player.followId > 0 || player.followId2 > 0) {
			player.getPA().resetFollow();
		}
		if (player.isBanking) {
			player.isBanking = false;
		}
		
		player.write(new SendClearScreen());
		if (player.teleTimer > 0 || player.teleporting) {
			return;
		}

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
		if (packetType == 98) {
			player.walkingToObject = true;
			player.mageAllowed = true;
		}
		
		if (packetType == 248) {
			packetSize -= 14;
		}
		
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
		player.getMovementHandler().finish();
	}

}