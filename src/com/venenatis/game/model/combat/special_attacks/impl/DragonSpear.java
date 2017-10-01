package com.venenatis.game.model.combat.special_attacks.impl;

import com.venenatis.game.model.combat.special_attacks.SpecialAttack;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.impl.SizedPathFinder;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class DragonSpear implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 1249, 11824 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		//playing the needed effects
		player.playAnimation(Animation.create(1064));
		player.playGraphic(Graphic.create(253, 0, 100));
		target.playGraphic(Graphic.create(254, 0, 100));
		target.getAttributes().put("stunned", true);
		target.asPlayer().getActionSender().sendMessage("You have been stunned!");
		
		//the dragon spear movement
		int dir = -1;
		if (!RegionStoreManager.get().blockedNorth(target.getLocation(), target)) {
			dir = 0;
		} else if (!RegionStoreManager.get().blockedSouth(target.getLocation(), target)) {
			dir = 4;
		} else if (!RegionStoreManager.get().blockedEast(target.getLocation(), target)) {
			dir = 8;
		} else if (!RegionStoreManager.get().blockedWest(target.getLocation(), target)) {
			dir = 12;
		}
		boolean found_path = false;
		int targX = target.getLocation().getX();
		int targY = target.getLocation().getY();
		if (player.getLocation().getNorth().equals(target.getLocation())) {
			if (!RegionStoreManager.get().blockedNorth(target.getLocation(), target)) {
				found_path = true;
				targY += 1;
			}
		} else if (player.getLocation().getSouth().equals(target.getLocation())) {
			if (!RegionStoreManager.get().blockedSouth(target.getLocation(), target)) {
				found_path = true;
				targY -= 1;
			}
		} else if (player.getLocation().getWest().equals(target.getLocation())) {
			if (!RegionStoreManager.get().blockedWest(target.getLocation(), target)) {
				found_path = true;
				targX += 1;
			}
		} else if (player.getLocation().getEast().equals(target.getLocation())) {
			if (!RegionStoreManager.get().blockedEast(target.getLocation(), target)) {
				found_path = true;
				targX -= 1;
			}
		}
		if (!found_path) {
			if (dir == 0) {
				targY += 1;
			} else if (dir == 4) {
				targY -= 1;
			} else if (dir == 8) {
				targX -= 1;
			} else if (dir == 12) {
				targX += 1;
			}
		}
		target.doPath(new SizedPathFinder(), targX, targY);
		
		//Unstunning our victim
		World.getWorld().schedule(new Task(7) {

			@Override
			public void execute() {
				target.getAttributes().remove("stunned");
				this.stop();
			}
			
		});
	}

	@Override
	public int amountRequired() {
		return 25;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}
