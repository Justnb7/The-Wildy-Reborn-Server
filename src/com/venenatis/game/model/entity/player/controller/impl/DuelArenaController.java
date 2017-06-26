package com.venenatis.game.model.entity.player.controller.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;

public class DuelArenaController extends Controller {

	private final Location RESPAWN = new Location(Constants.DUELING_RESPAWN_X, Constants.DUELING_RESPAWN_Y, 0);

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
		return true;
	}

	@Override
	public boolean canAttackNPC(Player player) {
		return false;
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
		return RESPAWN;
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
		if ((Area.inDuelArena(player) || Boundary.isIn(player, Boundary.DUEL_ARENAS))) {
			player.getActionSender().sendWalkableInterface(201);
			if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
				player.getActionSender().sendInteractionOption("Attack", 3, true);
			} else if (Area.inDuelArena(player)) {
				player.getActionSender().sendInteractionOption("Challenge", 3, true);
			} else {
				player.getActionSender().sendInteractionOption("null", 3, true);
			}
		} else {
			player.getActionSender().sendInteractionOption("null", 3, true);
		}
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
		if ((Area.inDuelArena(player) || Boundary.isIn(player, Boundary.DUEL_ARENAS))) {
			player.getActionSender().sendWalkableInterface(201);
			if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
				player.getActionSender().sendInteractionOption("Attack", 3, true);
			} else if (Area.inDuelArena(player)) {
				player.getActionSender().sendInteractionOption("Challenge", 3, true);
			} else {
				player.getActionSender().sendInteractionOption("null", 3, true);
			}
		} else {
			player.getActionSender().sendInteractionOption("null", 3, true);
		}
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