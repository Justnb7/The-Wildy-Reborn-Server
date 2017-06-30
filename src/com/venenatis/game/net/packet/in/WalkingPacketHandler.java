package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

/**
 * Walking packet
 **/
public class WalkingPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		//We can't walk because of the following reasons
		if (player.getCombatState().isDead() || !player.getController().canMove() || player.inTutorial() || player.teleTimer > 0
				|| player.isTeleporting() || player.isForcedMovement() || player.hasAttribute("busy")) {
			return;
		}
		
		if (player.getInterfaceState().getCurrentInterface() > 0 && !player.getDuelArena().isInSession() && !player.getTradeSession().isTrading()) {
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
		
		//When walking during a trade we don't decline trades
		if (player.isTrading()) {
			player.getTradeSession().declineTrade(false);
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
		
		player.getWalkingQueue().reset();
		
		//We're walking to our target
		walk(player, packetSize);
	}
	
	private void walk(Player player, int size) {
		final int steps = (size - 5) / 2;
		final int[][] path = new int[steps][2];

		final int firstX = player.getInStream().readSignedWordBigEndianA();
		for (int i = 0; i < steps; i++) {
		    path[i][0] = player.getInStream().readSignedByte();
		    path[i][1] = player.getInStream().readSignedByte();
		}
		final int firstY = player.getInStream().readSignedWordBigEndian();
		final boolean runSteps = player.getInStream().readSignedByteC() == 1;
		
		player.getWalkingQueue().setRunningQueue(runSteps);
		player.getWalkingQueue().addStep(firstX, firstY );
		
		for (int i = 0; i < steps; i++) {
		    path[i][0] += firstX;
		    path[i][1] += firstY;
		    player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		player.getWalkingQueue().finish();
	}
}