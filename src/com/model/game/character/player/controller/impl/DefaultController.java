package com.model.game.character.player.controller.impl;

import com.model.game.Constants;
import com.model.game.character.player.Player;
import com.model.game.character.player.controller.Controller;
import com.model.game.character.player.packets.out.SendWalkableInterfacePacket;
import com.model.game.location.Position;

public class DefaultController extends Controller {

	@Override
	public boolean canTalk(Player player) {
		return true;
	}

	@Override
	public boolean canMove(Player p) {
		return true;
	}

	@Override
	public boolean canClick(Player player) {
		return true;
	}

	@Override
	public boolean isSafe() {
		return false;
	}

	@Override
	public boolean canAttackNPC(Player player) {
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		return false;
	}

	@Override
	public void onDisconnect(Player p) {
	}

	@Override
	public String toString() {
		return "DEFAULT";
	}

	@Override
	public boolean canLogOut(Player player) {
		return true;
	}

	@Override
	public void tick(Player player) {
	}

	@Override
	public void onControllerInit(Player player) {
		player.write(new SendWalkableInterfacePacket(-1));
		player.getActionSender().sendInteractionOption("null", 3, true);
	}

	@Override
	public void onDeath(Player p) {
	}

	@Override
	public Position getRespawnLocation(Player player) {
		return Constants.RESPAWN_PLAYER_LOCATION;
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public boolean canTrade(Player player) {
		return true;
	}

	@Override
	public boolean canEquip(Player p, int id, int slot) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player p) {
		return true;
	}

	@Override
	public boolean canEat(Player p) {
		return true;
	}

	@Override
	public boolean canDrink(Player p) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player p) {
		return true;
	}

	@Override
	public boolean transitionOnWalk(Player p) {
		return true;
	}

	@Override
	public void onTeleport(Player p) {
	}

	@Override
	public boolean allowMultiSpells(Player player) {
		return true;
	}

	@Override
	public boolean allowPvPCombat(Player player) {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public void onWalk(Player player) {
		player.write(new SendWalkableInterfacePacket(-1));
		player.getActionSender().sendInteractionOption("null", 3, true);
	}

	@Override
	public void onControllerLeave(Player player) {
		// TODO Auto-generated method stub
		
	}
}
