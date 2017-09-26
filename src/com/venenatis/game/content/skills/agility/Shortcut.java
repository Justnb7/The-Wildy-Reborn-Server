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
	
	
	
	private static final int PIPES_EMOTE = 844;
	private static final int WALK = 1, MOVE = 2, AGILITY = 3;

	private static void agilityWalk(final Player player, final int walkAnimation, final int x, final int y) {
		player.getWalkingQueue().setRunningToggled(false);
		player.getActionSender().sendConfig(152, 0);
		player.setWalkAnimation(walkAnimation);
		player.getWalkingQueue().walkTo(x, y);
	}

	private static void handleAgility(Player player, int x, int y, int levelReq, int anim, int walk, String message) {
		if (player.getSkills().getLevel(Skills.AGILITY) < levelReq) {
			player.getActionSender().sendMessage("You need " + levelReq + " agility to use this shortcut.");
			return;
		}

		switch (walk) {
		case 1:
			player.getWalkingQueue().walkTo(x, y);
			break;
		case 2:
			player.setTeleportTarget(new Location(x, y, player.getZ()));
			break;
		case 3:
			agilityWalk(player, x, y, anim);
			break;
		}
		if (anim != 0 && anim != -1) {
			player.playAnimation(Animation.create(anim));
		}
		player.getActionSender().sendMessage(message);
	}

	public static void processAgilityShortcut(Player player, GameObject object) {
		if(object == null) {
			return;
		}
		
		int x;
		int y;
		int y2 = 0;
		switch (object.getId()) {
		/* taverlyObstaclePipe */
		case 16509:
			if (!player.getLocation().equals(Location.create(2886, 9799, 0)) && !player.getLocation().equals(Location.create(2892, 9799, 0))) {
				player.removeAttribute("busy");
				return;
			}
			if (player.getLocation().getX() == 2886) {
				y = 4;
				y2 = 2;
				x = 1;
			} else {
				y = -4;
				y2 = -2;
				x = -1;
			}
			player.playAnimation(new Animation(746));
			player.forceMove(new ForceMovement(x, 0, y2, 0, 45, 100, 3, Direction.NORTH), false);
			player.playAnimation(new Animation(748));
			player.forceMove(new ForceMovement(0, 0, y, 0, 0, 15, 1, Direction.NORTH), true);
			break;
			
		/* shamanRockClimb1 */
		/* shamanRockClimb2 */
		/* shamanRockClimb3 */
		/* shamanRockClimb4 */
		case 27362:

			break;

		/* faladorCrumblingWall */
		case 11844:
			x = 2;
			if (player.getLocation().getX() >= 2936) {
				x = -2;
			}
			player.playAnimation(new Animation(839));
			player.forceMove(new ForceMovement(0, 0, x, 0, 20, 60, 2, Direction.NORTH), true);
			player.getSkills().addExperience(Skills.AGILITY, 5);
			break;

		/* edgeDungeonPipe */
		case 16511:
			break;

		/* taverlySpikeJump */
		case 16510:
			if (!player.getLocation().equals(Location.create(2880, 9813, 0))
					&& !player.getLocation().equals(Location.create(2878, 9813))) {
				player.removeAttribute("busy");
				return;
			}
			x = -2;
			if (player.getLocation().equals(Location.create(2878, 9813, 0))) {
				x = 2;
			}
			player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 9813, 0), 0, 2);
			break;

		/* fremmySpikeJump */
		/* fremmySpikeJump2 */
		case 16544:
			if (object.getLocation().equals(Location.create(2774, 10003, 0))) {
				if (!player.getLocation().equals(Location.create(2775, 10003, 0)) && !player.getLocation().equals(Location.create(2773, 10003))) {
					player.removeAttribute("busy");
					return;
				}
				x = -2;
				if (player.getLocation().equals(Location.create(2773, 10003, 0))) {
					x = 2;
				}
				player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 10003, 0), 0, 2);
			} else {
				if (!player.getLocation().equals(Location.create(2770, 10002, 0)) && !player.getLocation().equals(Location.create(2768, 10002))) {
					player.removeAttribute("busy");
					return;
				}
				x = -2;
				if (player.getLocation().equals(Location.create(2768, 10002, 0))) {
					x = 2;
				}
				player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 10002, 0), 0, 2);
			}
			break;

		/* motherlodeDarkTunnel */
		/* motherlodeDarkTunnel2 */
		case 10047:

			break;

		/* ardougneLogBalance */
		case 16548:
		case 16546:
		case 16547:
			if (player.getLocation().getY() != 3336) {
				player.removeAttribute("busy");
				return;
			}
			x = -4;
			if (player.getLocation().getX() == 2598) {
				x = 4;
			}
			player.setRunningToggled(false, 4);
			player.forceWalk(Animation.create(762), player.getX() + x, player.getY(), 0, 4, true);
			player.getSkills().addExperience(Skills.AGILITY, 33);
			break;

		/* slayerRockClimb */
		case 26724:

			break;
		
		case 993:
			if (player.getY() == 3435) {
				handleAgility(player, 2761, 3438, 1, 3067, MOVE, "You jump over the stile.");
			} else if (player.getY() == 3438) {
				handleAgility(player, 2761, 3435, 1, 3067, MOVE, "You jump over the stile.");
			}
			break;
		case 8739:
			handleAgility(player, -2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			break;
		case 51:
			handleAgility(player, 1, 0, 66, 2240, WALK, "You squeeze through the railings");
			break;
			//TODO duplicate
		/*case 16544:
			if (player.getX() == 2773) {
				handleAgility(player, 2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			} else if (player.getX() == 2775) {
				handleAgility(player, -2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			} else if (player.getX() == 2770) {
				handleAgility(player, -2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			}
			break;*/
		case 16539:
			if (player.getX() == 2735) {
				handleAgility(player, -5, 0, 62, 2240, WALK, "You squeeze through the crevice.");
			} else if (player.getX() == 2730) {
				handleAgility(player, 5, 0, 62, 2240, WALK, "You squeeze through the crevice.");
			}
			break;
		case 12127:
			if (player.getY() == 4403) {
				handleAgility(player, 0, -2, 66, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.getY() == 4401) {
				handleAgility(player, 0, 2, 66, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.getY() == 4404) {
				handleAgility(player, 0, -2, 46, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.getY() == 4402) {
				handleAgility(player, 0, 2, 46, 2240, WALK, "You squeeze past the jutted wall.");
			}
			break;
		case 3933:
			if (player.getY() == 3232) {
				handleAgility(player, 0, 7, 85, 762, WALK, "You pass through the agility shortcut.");
			} else if (player.getY() == 3239) {
				handleAgility(player, 0, -7, 85, 762, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 4615:
		case 4616:
			if (player.getX() == 2595) {
				handleAgility(player, 2599, player.getY(), 1, 3067, MOVE, "You pass through the agility shortcut.");
			} else if (player.getX() == 2599) {
				handleAgility(player, 2595, player.getY(), 1, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
			//TODO duplicate
		/*case 11844:
			if (player.getX() == 2936) {
				handleAgility(player, -2, 0, 5, -1, WALK, "You pass through the agility shortcut.");
			} else if (player.getX() == 2934) {
				handleAgility(player, 2, 0, 5, -1, WALK, "You pass through the agility shortcut.");
			}
			break;*/
		case 20884:
			if (player.getX() == 2687) {// 2682, 9506
				handleAgility(player, -5, 0, 5, 762, WALK, "You walk across the log balance.");
			}
			break;
		case 20882:
			if (player.getX() == 2682) {// 2867, 9506
				handleAgility(player, 5, 0, 5, 762, WALK, "You walk across the log balance.");
			}
			break;
		case 14922:
			if (object.getX() == 2344 && object.getY() == 3651) {
				handleAgility(player, 2344, 3655, 1, 762, MOVE, "You crawl through the hole.");
			} else if (object.getX() == 2344 && object.getY() == 3654) {
				handleAgility(player, 2344, 3650, 1, 762, MOVE, "You crawl through the hole.");
			}
			break;
		case 9330:
			if (object.getX() == 2601 && object.getY() == 3336) {
				handleAgility(player, -4, 0, 33, getAnimation(PIPES_EMOTE), AGILITY, "You pass through the agility shortcut.");
			}
		case 5100:
			if (player.getY() == 9566) {
				handleAgility(player, 2655, 9573, 17, 762, MOVE, "You pass through the agility shortcut.");
			} else if (player.getY() == 9573) {
				handleAgility(player, 2655, 9573, 17, 762, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9328:
			if (object.getX() == 2599 && object.getY() == 3336) {
				handleAgility(player, 4, 0, 33, getAnimation(PIPES_EMOTE), AGILITY, "You pass through the agility shortcut.");
			}
			break;

			//TODO duplicate
/*		case 16509:
			if (player.getX() < object.getX()) {
				handleAgility(player, 2892, 9799, 70, getAnimation(PIPES_EMOTE), MOVE, "You pass through the agility shortcut.");
			} else {
				handleAgility(player, 2886, 9799, 70, getAnimation(PIPES_EMOTE), MOVE, "You pass through the agility shortcut.");
			}
			break;*/
			//TODO duplicate
		/*case 16510:
			if (player.getX() == 2880) {
				handleAgility(player, -2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			} else {
				handleAgility(player, -2, 0, 81, 3067, WALK, "You jump over the strange floor.");
			}
			break;*/

		case 9302:
			if (player.getY() == 3112) {
				handleAgility(player, 2575, 3107, 16, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;

		case 9301:
			if (player.getY() == 3107) {
				handleAgility(player, 2575, 3112, 16, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9309:
			if (player.getY() == 3309) {
				handleAgility(player, 2948, 3313, 26, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9310:
			if (player.getY() == 3313) {
				handleAgility(player, 2948, 3309, 26, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2322:
			if (player.getX() == 2709) {
				handleAgility(player, 2704, 3209, 10, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2323:
			if (player.getX() == 2705) {
				handleAgility(player, 2709, 3205, 10, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2332:
			if (player.getX() == 2906) {
				handleAgility(player, 4, 0, 1, 762, WALK, "You pass through the agility shortcut.");
			} else if (player.getX() == 2910) {
				handleAgility(player, -4, 0, 1, 762, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 3067:
			if (player.getX() == 2639) {
				handleAgility(player, -1, 0, 1, 3067, WALK, "You pass through the agility shortcut.");
			} else if (player.getX() == 2638) {
				handleAgility(player, -1, 0, 1, 3067, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 2618:
			if (player.getY() == 3492) {
				handleAgility(player, 0, +2, 1, 3067, WALK, "You jump over the broken fence.");
			} else if (player.getY() == 3494) {
				handleAgility(player, -0, -2, 1, 3067, WALK, "You jump over the broken fence.");
			}
			break;
		case 21738:
			brimhavenSkippingStone(player);
			break;
		case 21739:
			brimhavenSkippingStone(player);
			break;
		case 2296:
			if (player.getX() == 2603) {
				handleAgility(player, -5, 0, 1, -1, WALK, "You pass through the agility shortcut.");
			} else if (player.getX() == 2598) {
				handleAgility(player, 5, 0, 1, -1, WALK, "You pass through the agility shortcut.");
			}
			break;
		}
	}

	private static int getAnimation(int objectId) {
		switch (objectId) {
		case 154:
		case 4084:
		case 9330:
		case 9228:
		case 5100:
			return PIPES_EMOTE;
		}
		return -1;
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