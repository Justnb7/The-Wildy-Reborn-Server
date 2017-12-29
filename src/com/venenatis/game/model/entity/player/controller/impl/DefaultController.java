package com.venenatis.game.model.entity.player.controller.impl;

import com.venenatis.game.constants.StringConstants;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.bounty.BountyHunterConstants;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelStage;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsPrayerDrainEvent;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.combat.PrayerHandler.PrayerData;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.net.packet.ActionSender.MinimapState;
import com.venenatis.game.world.World;

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
	public boolean canPray(PrayerData prayer) {
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
	}

	@Override
	public void onTeleport(Player player) {

	}

	@Override
	public void onStep(Player player) {
		
		if(player.isJailed() && !BoundaryManager.isWithinBoundary(player.getLocation(), "Jail")) {
			player.setTeleportTarget(new Location(3015, 3194, 0));
		}
		
		if (BoundaryManager.isWithinBoundary(player.getLocation(), "PvP Zone")) {
			int modY = player.getLocation().getY() > 6400 ? player.getLocation().getY() - 6400 : player.getLocation().getY();
			player.setWildLevel(((modY - 3521) / 8) + 1);
			player.getActionSender().sendString("@yel@Level: " + player.getWildLevel(), 199);
			player.getActionSender().sendInteractionOption(StringConstants.ATTACK_ACTION, 3, true);
			player.getActionSender().sendInteractionOption("null", 2, false);
			player.setAttribute("left_wild_delay", 0L);
			BountyHunter.writeBountyStrings(player);
			player.getActionSender().sendWalkableInterface(BountyHunterConstants.BOUNTY_INTERFACE_ID);
		} else if (player.getAttributes().get("duel_stage") != null && player.getAttributes().get("duel_stage") == DuelStage.FIGHTING_STAGE) {
			player.getActionSender().sendMinimapState(MinimapState.NORMAL);
			player.getActionSender().sendInteractionOption(StringConstants.DUEL_FIGHT, 3, true);
			player.getActionSender().sendInteractionOption("null", 2, false);
			player.getActionSender().sendWalkableInterface(201);
		} else if (BoundaryManager.isWithinBoundary(player.getLocation(), "DuelArena")) {
			player.getActionSender().sendInteractionOption(StringConstants.DUEL_ACTION, 2, false);
			player.getActionSender().sendWalkableInterface(201);
		} else {
			player.getActionSender().sendMinimapState(MinimapState.NORMAL);
			player.getActionSender().sendInteractionOption("null", 3, false);
			player.getActionSender().sendInteractionOption("null", 1, false);
			if (BarrowsHandler.getSingleton().withinBarrows(player)) {
				player.getActionSender().sendWalkableInterface(4535); //4535 for default Kill Count: 0 42050
				if (BarrowsHandler.getSingleton().withinCrypt(player)) {
					World.getWorld().schedule(new BarrowsPrayerDrainEvent(player));
				}
			}/* else if (Wintertodt.inWintertodt(this)) {
				getActionSender().sendWalkableInterface(57100);
			}*/ else {
				player.getActionSender().sendWalkableInterface(-1);
			}
		}
		player.getActionSender().sendMultiIcon(BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "multi_combat") ? 1 : -1);
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
				player.getActionSender().sendString(energy+"%", 149);
			}
		} else if (player.getWalkingQueue().isMoving() && player.getWalkingQueue().isRunning()) {
			if (player.getRunEnergy() <= 0) {
				player.getWalkingQueue().setRunningToggled(false);
				player.getActionSender().sendConfig(152, 0);
			} else {
				if(!player.getRights().isOwner(player))
				player.setRunEnergy(player.getRunEnergy() - 1);
				player.getActionSender().sendRunEnergy();
				player.getActionSender().sendString(player.getRunEnergy()+"%", 149);
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