package com.model.game.character.player.controller;

import com.model.game.character.player.Player;
import com.model.game.location.Position;



public abstract class Controller {
	
	public abstract void tick(Player player);

	public abstract boolean canTalk(Player player);

	public abstract boolean canMove(Player player);

	public abstract boolean canSave();

	public abstract boolean canClick(Player player);

	public abstract boolean isSafe();

	public abstract boolean canAttackNPC(Player player);

	public abstract boolean canAttackPlayer(Player attacker, Player victim);

	public abstract boolean allowMultiSpells(Player player);

	public abstract boolean allowPvPCombat(Player player);

	public abstract void onDeath(Player player);

	public abstract Position getRespawnLocation(Player player);

	public abstract boolean canLogOut(Player player);

	public abstract void onDisconnect(Player player);

	public abstract void onControllerInit(Player player);

	public abstract boolean canTeleport(Player player);

	public abstract void onTeleport(Player player);

	public abstract boolean canTrade(Player player);

	public abstract boolean canEquip(Player player, int id, int slot);

	public abstract boolean canUsePrayer(Player player);

	public abstract boolean canEat(Player player);

	public abstract boolean canDrink(Player player);

	public abstract boolean canUseSpecialAttack(Player player);

	public abstract boolean transitionOnWalk(Player player);
	
	public abstract void onWalk(Player player);
	
	public abstract void onControllerLeave(Player player);

	@Override
	public abstract String toString();

	public void throwException(Player player, String action) {
		System.out.println("||||||||||||||||||||||||||");
		System.out.println("UNABLE TO " + action + " FOR PLAYER " + player.getName() + "!");
		System.out.println("CONTROLLER: " + player.getController().toString());
		System.out.println("||||||||||||||||||||||||||");
	}
}

