package com.venenatis.game.model.entity.player.controller.impl;

import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;

/**
 * Controller for no interactions by player (movement, talking, clicking, etc).
 * @author Daniel
 *
 */
public class ZeroInteractionController extends Controller {
	
	private final int[] clickableButtons = { 51772, 2461, 2462, 16141, 16142, 16143, 16137, 16138, 16139, 16140 };

	@Override
	public boolean canAttackNPC() {
		return false;
	}

	@Override
	public boolean canAttackPlayer(Player player, Player opponent) {
		return false;
	}

	@Override
	public boolean canClickButton(int button) {
		for (int id : clickableButtons) {
			if (button == id) {
				return true;				
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canClickEntity(Entity entity) {
		return false;
	}
	
	@Override
	public boolean canClickObject(int object) {
		return false;
	}

	@Override
	public boolean canDrink() {
		return false;
	}

	@Override
	public boolean canDrop(int item) {
		return false;
	}

	@Override
	public boolean canEat() {
		return false;
	}

	@Override
	public boolean canEquip(int item, int slot) {
		return false;
	}

	@Override
	public boolean canLogout() {
		return false;
	}

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public boolean canPickup(int item) {
		return false;
	}

	@Override
	public boolean canPray(Prayers prayer) {
		return false;
	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public boolean canTalk() {
		return false;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canTrade() {
		return false;
	}

	@Override
	public boolean canUnequip(int item, int slot) {
		return false;
	}

	@Override
	public boolean canUseSpecial(Player player) {
		return false;
	}

	@Override
	public boolean isSafe() {
		return false;
	}

	@Override
	public void onDeath(Player player) {
		
	}

	@Override
	public void onExit(Player player) {
		
	}

	@Override
	public void onLogout(Player player) {

	}

	@Override
	public void onStartup(Player player) {
		onStep(player);
	}

	@Override
	public void onTeleport(Player player) {
		
	}
	
	@Override
	public void onStep(Player player) {
	}

	@Override
	public void process(Player player) {

	}

	@Override
	public String toString() {
		return "Zero Interaction Controller";
	}

	@Override
	public boolean canCommand() {
		return true;
	}

	@Override
	public void onWalk(Player player) {
		
	}

	@Override
	public boolean canInteract() {
		return false;
	}

}