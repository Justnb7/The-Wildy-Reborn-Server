package com.venenatis.game.content.activity;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;

/**
 * Represents an abstract model of activities players can perform.
 *
 * @author Seven
 */
public abstract class Activity {

	private String name;

	public Activity(String name) {
		this.name = name;
	}

	/**
	 * Determines if a {@code player} can logout while
	 * in this activity.
	 *
	 * @param player
	 *    The player to check.
	 */
	public boolean canLogout(Player player) {
		return false;
	}

	/**
	 * The method called when a {@code player} logs into this activity.
	 *
	 * @param player
	 *    The player logging in.
	 */
	public void onLogin(Player player) {

	}

	/**
	 * The method called when a {@code player} logs out.
	 *
	 * @param player
	 *    The player logging out.
	 */
	public void onLogout(Player player) {

	}

	/**
	 * Determines if this {@code player} can teleport to a specified {@code location}.
	 *
	 * @param player
	 *    The player to check.
	 *
	 * @return {@code true} If this player can teleport. {@code false} otherwise.
	 */
	public boolean canTeleport(Player player) {
		player.getActionSender().sendMessage("You are not allowed to teleport here.");
		return false;
	}

	/**
	 * Determines if this {@code player} can use commands in this activity.
	 *
	 * @param player
	 *    The player to check.
	 *
	 * @return {@code true} If this player can use commands. {@code false} otherwise.
	 */
	public boolean canUseCommands(Player player) {
		player.getActionSender().sendMessage("You are not allowed to use commands here.");
		return false;
	}

	/**
	 * Determines if a {@code player} can unequip an {@code item}.
	 *
	 * @param player
	 *    The player trying to unequip.
	 *
	 * @param item
	 *    The item being equipped.
	 *
	 * @return {@code true} If a player can unequip this item. {@code false} otherwise.
	 */
	public boolean canUnequip(Player player, Item item) {
		return true;
	}

	/**
	 * Determines if a {@code player} can equip an {@code item}.
	 *
	 * @param player
	 *    The player trying to equip.
	 *
	 * @param item
	 *    The item being equipped.
	 *
	 * @return {@code true} If a player can equip this item. {@code false} otherwise.
	 */
	public boolean canEquip(Player player, Item item) {
		return true;
	}

	/**
	 * Determines if a {@code player} can click a button in a minigame.
	 *
	 * @param player
	 *    The player clicking the button
	 *
	 * @param button
	 *    The button being clicked.
	 */
	public boolean canClickButton(Player player, int button) {
		return false;
	}

	/**
	 * The method called when a {@code player} clicks a button in the minigame.
	 *
	 * @param player
	 *    The player clicking the button.
	 *
	 * @param button
	 *    The button being clicked.
	 */
	public void onButtonClick(Player player, int button) {

	}

	/**
	 * The method called when a {@code player} first clicks on a mob.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param entity
	 *    The entity being clicked.
	 */
	public void onFirstClickNpc(Player player, Entity entity) {

	}

	/**
	 * The method called when a {@code player} second clicks on a mob.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param entity
	 *    The entity being clicked.
	 */
	public void onSecondClickNpc(Player player, Entity entity) {

	}

	/**
	 * The method called when a {@code player} third clicks on a mob in this minigame.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param entity
	 *    The entity being clicked.
	 */
	public void onThirdClickNpc(Player player, Entity entity) {

	}

	/**
	 * The method called when a {@code player} first clicks on an object in this minigame.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param object
	 *    The object being clicked.
	 */
	public void onFirstClickObject(Player player, GameObject object) {

	}

	/**
	 * The method called when a {@code player} second clicks on an object in this minigame.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param object
	 *    The object being clicked.
	 */
	public void onSecondClickObject(Player player, GameObject object) {

	}

	/**
	 * The method called when a {@code player} third clicks on an object in this minigame.
	 *
	 * @param player
	 *    The player clicking.
	 *
	 * @param object
	 *    The object being clicked.
	 */
	public void onThirdClickObject(Player player, GameObject object) {

	}

	/**
	 * Determines if a {@code player} in this activity can trade.
	 *
	 * @param player
	 *    The player to check.
	 *
	 * @param other
	 *    The other player to check.
	 *
	 * @return {@code true} If this player can trade. {@code false} otherwise.
	 */
	public boolean canTrade(Player player, Player other) {
		return false;
	}

	/**
	 * Determines if a {@code player} can hit another entity.
	 *
	 * @param player
	 *    The player to check.
	 *
	 * @param entity
	 *    The entity to check.
	 */
	public boolean canHit(Player player, Entity entity) {
		return false;
	}

	/**
	 * The method called when the activty gets reset.
	 */
	public void onReset(Player player) {

	}

	public String getName() {
		return name;
	}

}