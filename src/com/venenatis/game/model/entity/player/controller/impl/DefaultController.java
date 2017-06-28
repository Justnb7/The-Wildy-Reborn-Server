package com.venenatis.game.model.entity.player.controller.impl;

import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.bounty.BountyHunterConstants;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.PlayerOption;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.model.entity.player.controller.Controller;

public class DefaultController extends Controller {

	@Override
	public boolean canAttackNPC() {
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player player, Player opponent) {
		return true;
	}

	@Override
	public boolean canClickButton(int button) {
		return true;
	}

	@Override
	public boolean canClickEntity(Entity entity) {
		return true;
	}

	@Override
	public boolean canClickObject(int object) {
		return true;
	}

	@Override
	public boolean canDrink() {
		return true;
	}

	@Override
	public boolean canDrop(int item) {
		return true;
	}

	@Override
	public boolean canCommand() {
		return true;
	}

	@Override
	public boolean canEat() {
		return true;
	}

	@Override
	public boolean canEquip(int item, int slot) {
		return true;
	}

	@Override
	public boolean canLogout() {
		return true;
	}

	@Override
	public boolean canMove() {
		return true;
	}

	@Override
	public boolean canPickup(int item) {
		return true;
	}

	@Override
	public boolean canPray(Prayers prayer) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public boolean canTalk() {
		return true;
	}

	@Override
	public boolean canTeleport() {
		return true;
	}

	@Override
	public boolean canTrade() {
		return true;
	}

	@Override
	public boolean canUnequip(int item, int slot) {
		return true;
	}

	@Override
	public boolean canUseSpecial(Player player) {
		return true;
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
		player.logout();
	}

	@Override
	public void onLogout(Player player) {
	}

	@Override
	public void onStartup(Player player) {
		onStep(player);

		if (player.getClanChat() != null || player.getClanChat() == "") {
			ClanManager.join(player, player.getClanChat());
		}

	}

	@Override
	public void onTeleport(Player player) {

	}

	@Override
	public void onStep(Player player) {
		player.getActionSender().sendMultiIcon(Area.inMultiCombatZone(player) ? 1 : -1);

		/* Minigame */
		if (MinigameHandler.search(player).isPresent()) {
			MinigameHandler.search(player).ifPresent(m -> m.onDisplay(player));

			/* Wilderness */
		} else if (Area.inWilderness(player)) {

			int modY = player.getLocation().getY() > 6400 ? player.getLocation().getY() - 6400 : player.getLocation().getY();

			player.setWildLevel(((modY - 3521) / 8) + 1);

	        player.getActionSender().sendString("@yel@Level: " + player.getWildLevel(), 199);

	        player.setAttribute("left_wild_delay", 0);
	        BountyHunter.writeBountyStrings(player);
	        player.getActionSender().sendWalkableInterface(BountyHunterConstants.BOUNTY_INTERFACE_ID);

			player.getActionSender().sendPlayerOption(PlayerOption.ATTACK, true, false);

			player.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, true);

			/* Duel Arena */
		} else if (Area.inDuelArena(player) || player.getDuelArena().getStage() == DuelStage.ARENA) {
			if (player.getDuelArena().getStage() != DuelStage.ARENA) {
				player.getActionSender().sendPlayerOption(PlayerOption.ATTACK, true, true);
				player.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, false);
			} else {
				player.getActionSender().sendPlayerOption(PlayerOption.ATTACK, true, false);
				player.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, true);
			}
			player.getActionSender().sendWalkableInterface(player.getDuelArena().getStage() == DuelStage.ARENA ? -1 : 201);
		} else {
			player.getActionSender().sendPlayerOption(PlayerOption.ATTACK, false, true);
			player.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, true);
            player.getActionSender().sendWalkableInterface(-1);
		}
	}

	@Override
	public void process(Player player) {
		if (!(player.getWalkingQueue().isMoving() && player.getWalkingQueue().isRunning()) && player.getRunEnergy() != 100) {

			player.setRunRestore(player.getRunRestore() + 1);

			if (player.getRunRestore() == 3) {
				player.setRunRestore(0);

				int energy = player.getRunEnergy() + 1;

				if (energy > 100)
					energy = 100;

				player.setRunEnergy(energy);
				player.getActionSender().sendRunEnergy();
			}
		} else if (player.getWalkingQueue().isMoving() && player.getWalkingQueue().isRunning()) {
			if (player.getRunEnergy() <= 0) {
				player.getWalkingQueue().setRunningToggled(false);
				player.getActionSender().sendConfig(152, 0);
			} else {
				player.setRunEnergy(player.getRunEnergy() - 1);
				player.getActionSender().sendRunEnergy();
			}
		}
	}

	@Override
	public String toString() {
		return "Default Controller";
	}

	@Override
	public void onWalk(Player player) {

	}

	@Override
	public boolean canInteract() {
		return true;
	}

}