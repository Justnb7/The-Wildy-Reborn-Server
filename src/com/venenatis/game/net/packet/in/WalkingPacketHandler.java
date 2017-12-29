package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.gamble.Gamble.GambleStage;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelOptions;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelStage;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * Walking packet
 **/
public class WalkingPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		//Were busy we can't walk
		if (player.getAttribute("busy") != null) {
			return;
		}
		
		//We can't walk if were dead
		if (player.getCombatState().isDead()) {
			return;
		}
		
		//The controller blocks us from walking
		if(!player.getController().canMove()) {
			
		}
		
		//We can't walk whilst being stunned
		if (player.hasAttribute("stunned")) {
			player.getActionSender().sendMessage("You're stunned!");
			return;
		}
		
		//We can't walk whilst teleporting
		if(player.isTeleporting()) {
			return;
		}
		
		if (player.getAttributes().get("duel_stage") != null && player.getAttributes().get("duel_stage") != DuelStage.FIGHTING_STAGE) {
			player.getDuelArena().decline();
			return;
		}
		
		if (player.getDuelArena().getOptionActive()[DuelOptions.NO_MOVEMENT.getId()]) {
			player.getActionSender().sendMessage("The right to move has been revoked during this stake.");
			return;
		}

		if (player.getGamble().getStage() != null && player.getGamble().getStage() != GambleStage.GAMBLE_STAGE) {
			player.getGamble().decline();
		}
		
		if (player.getInterfaceState().getCurrentInterface() > 0 && !player.getTradeSession().isTrading()) {
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
		
		if(!player.canEmote()) {
			return; //stops walking during skillcape animations.
		}
		
		//Set our controller
		player.getController().onWalk(player);
		
		//When walking we have to close all open interfaces.
		player.getActionSender().removeAllInterfaces();
		
		//Stop our distanced action task because we reset the walking queue by walking
		player.stopDistancedTask();
		
		//Stop active skilling tasks
		player.removeAttribute("fishing");

		if (player.getTradeSession().isTrading()) {
			player.getActionSender().sendMessage("Please close what you're doing before trying to move.");
			return;
		}
		
		//When walking reset the following variables
		if (packetType == 248 || packetType == 164) {
			player.face(null);
			player.getCombatState().reset();
			Combat.resetCombat(player);
			player.following().setFollowing(null);
			player.resetFaceEntity();
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