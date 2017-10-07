package com.venenatis.game.content.skills.agility;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

public class Shortcut {

	public static boolean processAgilityShortcut(Player player, GameObject object) {
		
		int x;
		int y;
		int y2 = 0;
		Direction face;
		
		switch (object.getId()) {
		/* taverlyObstaclePipe */
		case 16509:
			if(!player.getLocation().equals(Location.create(2886, 9799, 0)) && !player.getLocation().equals(Location.create(2892, 9799, 0))) {
				player.removeAttribute("busy");
				return false;
			}
			if(player.getSkills().getLevel(Skills.AGILITY) < 70) {
				SimpleDialogues.sendStatement(player, "You need an Agility level of 70 to enter this area.");
				return false;
			}
			player.setAttribute("busy", true);
			
			if (player.getLocation().getX() == 2886) {
				y = 4;
				y2 = 2;
				x = 1;
				face = Direction.NORTH;
			} else {
				y = -4;
				y2 = -2;
				x = -1;
				face = Direction.WEST;
			}
			player.playAnimation(new Animation(746));
			player.forceMove(new ForceMovement(x, 0, y2, 0, 45, 100, 2, face), false);
			player.playAnimation(new Animation(748, 50));
			player.forceMove(new ForceMovement(0, 0, y, 0, 0, 15, 1, face), true);
			return true;

		}
		return false;
	}

	private static void brimhavenSkippingStone(final Player player) {
		if (player.getSkills().getLevel(Skills.AGILITY) < 12) {
			SimpleDialogues.sendStatement(player, "You need 12 agility to use these stepping stones");
			return;
		}
		Server.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(769));
				if (player.getX() <= 2997) {
					stop();
				}
			}
		});
		Server.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				if (player.getX() >= 2648) {
					player.setTeleportTarget(new Location(player.getX() - 2, player.getY() - 5, player.getZ()));
					if (player.getX() <= 2997) {
						stop();
					}
				} else if (player.getX() <= 2648) {
					player.setTeleportTarget(new Location(player.getX() + 2, player.getY() + 5, player.getZ()));
					if (player.getX() >= 2645) {
						stop();
					}
				}
			}

			@Override
			public void onStop() {
				player.getSkills().addExperience(Skills.AGILITY, 300);
				setAnimationBack(player);
			}
		});
	}

	private static void setAnimationBack(Player player) {
		player.getWalkingQueue().setRunningToggled(true);
		player.getActionSender().sendConfig(152, 1);
		player.setWalkAnimation(0x333);
	}

}