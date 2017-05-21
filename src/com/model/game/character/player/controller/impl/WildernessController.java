package com.model.game.character.player.controller.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.content.bounty_hunter.BountyHunterConstants;
import com.model.game.character.player.controller.Controller;
import com.model.game.location.Location;
import com.model.net.packet.out.SendWalkableInterfacePacket;



public class WildernessController extends Controller {

	private final Location WILDERNESS_RESPAWN = new Location(3096, 3503, 0);

	@Override
	public void tick(Player player) {
	}

	@Override
	public boolean canTalk(Player player) {
		return true;
	}

	@Override
	public boolean canMove(Player player) {
		return true;
	}

	@Override
	public boolean canSave() {
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
	public boolean canAttackPlayer(Player attacker, Player victim) {
		return true;
	}

	@Override
	public boolean allowMultiSpells(Player player) {
		return true;
	}

	@Override
	public boolean allowPvPCombat(Player player) {
		return true;
	}

	@Override
	public void onDeath(Player player) {
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return WILDERNESS_RESPAWN;
	}

	@Override
	public boolean canLogOut(Player player) {
		return true;
	}

	@Override
	public void onDisconnect(Player player) {

	}

	@Override
	public void onControllerInit(Player player) {
		int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
		player.wildLevel = (((modY - 3520) / 8) + 1);
		player.getActionSender().sendInteractionOption("Attack", 3, true);
		player.getActionSender().sendString("@yel@Level: " + player.wildLevel, 199);
		player.setAttribute("left_wild_delay", 0);
		BountyHunter.writeBountyStrings(player);
		player.write(new SendWalkableInterfacePacket(BountyHunterConstants.BOUNTY_INTERFACE_ID));
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public void onTeleport(Player player) {

	}

	@Override
	public boolean canTrade(Player player) {
		return true;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player player) {
		return true;
	}

	@Override
	public boolean canEat(Player player) {
		return true;
	}

	@Override
	public boolean canDrink(Player player) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player player) {
		return true;
	}

	@Override
	public boolean transitionOnWalk(Player player) {
		return true;
	}

	@Override
	public void onWalk(Player player) {
		int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
		int wildLevel = (((modY - 3523) / 8) + 1);
		if (player.wildLevel != wildLevel) {
			player.wildLevel = wildLevel;
			player.getActionSender().sendString("@yel@Level: " + player.wildLevel, 199);
		}
		player.write(new SendWalkableInterfacePacket(BountyHunterConstants.BOUNTY_INTERFACE_ID));
	}

	@Override
	public String toString() {
		return "Wilderness Controller";
	}

	@Override
	public void onControllerLeave(Player player) {
		player.setAttribute("left_wild_delay", System.currentTimeMillis());
	}

}
