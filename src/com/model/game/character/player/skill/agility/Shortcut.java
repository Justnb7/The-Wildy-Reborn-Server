package com.model.game.character.player.skill.agility;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendConfig;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.task.ScheduledTask;

public class Shortcut {
	
	private static final int PIPES_EMOTE = 844;
	private static final int WALK = 1, MOVE = 2, AGILITY = 3;

	public static void agilityWalk(final Player player, final int walkAnimation, final int x, final int y) {
		player.setRunning(false);
		player.getMovementHandler().setForcedMovement(true);
		player.write(new SendConfig(173, 0));
		player.walkAnimation = walkAnimation;
		player.getPA().requestUpdates();
		player.getPA().walkTo(x, y);
	}

	private static void handleAgility(Player player, int x, int y, int anim, int walk, String message) {

		switch (walk) {
		case 1:
			player.getPA().walkTo(x, y);
			break;
		case 2:
			player.getPA().movePlayer(x, y, player.heightLevel);
			break;
		case 3:
			agilityWalk(player, x, y, anim);
			break;
		}
		if (anim != 0 && anim != -1) {
			player.playAnimation(Animation.create(anim));
		}
		player.write(new SendMessagePacket(message));
	}

	public static void processAgilityShortcut(Player player) {
		switch (player.objectId) {
		case 993:
			if (player.absY == 3435) {
				handleAgility(player, 2761, 3438, 3067, MOVE, "You jump over the stile.");
			} else if (player.absY == 3438) {
				handleAgility(player, 2761, 3435, 3067, MOVE, "You jump over the stile.");
			}
			break;
		case 8739:
			handleAgility(player, -2, 0, 3067, WALK, "You jump over the strange floor.");
			break;
		case 51:
			handleAgility(player, 1, 0, 2240, WALK, "You squeeze through the railings");
			break;
		case 16544:
			if (player.absX == 2773) {
				handleAgility(player, 2, 0, 3067, WALK, "You jump over the strange floor.");
			} else if (player.absX == 2775) {
				handleAgility(player, -2, 0, 3067, WALK, "You jump over the strange floor.");
			} else if (player.absX == 2770) {
				handleAgility(player, -2, 0, 3067, WALK, "You jump over the strange floor.");
			}
			break;
		case 16539:
			if (player.absX == 2735) {
				handleAgility(player, -5, 0, 2240, WALK, "You squeeze through the crevice.");
			} else if (player.absX == 2730) {
				handleAgility(player, 5, 0, 2240, WALK, "You squeeze through the crevice.");
			}
			break;
		case 12127:
			if (player.absY == 4403) {
				handleAgility(player, 0, -2, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.absY == 4401) {
				handleAgility(player, 0, 2, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.absY == 4404) {
				handleAgility(player, 0, -2, 2240, WALK, "You squeeze past the jutted wall.");
			} else if (player.absY == 4402) {
				handleAgility(player, 0, 2, 2240, WALK, "You squeeze past the jutted wall.");
			}
			break;
		case 3933:
			if (player.absY == 3232) {
				handleAgility(player, 0, 7, 762, WALK, "You pass through the agility shortcut.");
			} else if (player.absY == 3239) {
				handleAgility(player, 0, -7, 762, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 4615:
		case 4616:
			if (player.absX == 2595) {
				handleAgility(player, 2599, player.absY, 3067, MOVE, "You pass through the agility shortcut.");
			} else if (player.absX == 2599) {
				handleAgility(player, 2595, player.absY, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 11844:
			if (player.absX == 2936) {
				handleAgility(player, -2, 0, -1, WALK, "You pass through the agility shortcut.");
			} else if (player.absX == 2934) {
				handleAgility(player, 2, 0, -1, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 20884:
			if (player.absX == 2687) {// 2682, 9506
				handleAgility(player, -5, 0, 762, WALK, "You walk across the log balance.");
			}
			break;
		case 20882:
			if (player.absX == 2682) {// 2867, 9506
				handleAgility(player, 5, 0, 762, WALK, "You walk across the log balance.");
			}
			break;
		case 14922:
			if (player.objectX == 2344 && player.objectY == 3651) {
				handleAgility(player, 2344, 3655, 762, MOVE, "You crawl through the hole.");
			} else if (player.objectX == 2344 && player.objectY == 3654) {
				handleAgility(player, 2344, 3650, 762, MOVE, "You crawl through the hole.");
			}
			break;
		case 9330:
			if (player.objectX == 2601 && player.objectY == 3336) {
				handleAgility(player, -4, 0, getAnimation(PIPES_EMOTE), AGILITY, "You pass through the agility shortcut.");
			}
		case 5100:
			if (player.absY == 9566) {
				handleAgility(player, 2655, 9573, 762, MOVE, "You pass through the agility shortcut.");
			} else if (player.absY == 9573) {
				handleAgility(player, 2655, 9573, 762, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9328:
			if (player.objectX == 2599 && player.objectY == 3336) {
				handleAgility(player, 4, 0, getAnimation(PIPES_EMOTE), AGILITY, "You pass through the agility shortcut.");
			}
			break;

		case 16509:
			if (player.absX < player.objectX) {
				handleAgility(player, 2892, 9799, getAnimation(PIPES_EMOTE), MOVE, "You pass through the agility shortcut.");
			} else {
				handleAgility(player, 2886, 9799, getAnimation(PIPES_EMOTE), MOVE, "You pass through the agility shortcut.");
			}
			break;

		case 16510:
			if (player.absX == 2880) {
				handleAgility(player, -2, 0, 3067, WALK, "You jump over the strange floor.");
			} else {
				handleAgility(player, -2, 0, 3067, WALK, "You jump over the strange floor.");
			}
			break;

		case 9302:
			if (player.absY == 3112) {
				handleAgility(player, 2575, 3107, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;

		case 9301:
			if (player.absY == 3107) {
				handleAgility(player, 2575, 3112, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9309:
			if (player.absY == 3309) {
				handleAgility(player, 2948, 3313, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 9310:
			if (player.absY == 3313) {
				handleAgility(player, 2948, 3309, 844, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2322:
			if (player.absX == 2709) {
				handleAgility(player, 2704, 3209, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2323:
			if (player.absX == 2705) {
				handleAgility(player, 2709, 3205, 3067, MOVE, "You pass through the agility shortcut.");
			}
			break;
		case 2332:
			if (player.absX == 2906) {
				handleAgility(player, 4, 0, 762, WALK, "You pass through the agility shortcut.");
			} else if (player.absX == 2910) {
				handleAgility(player, -4, 0, 762, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 3067:
			if (player.absX == 2639) {
				handleAgility(player, -1, 0, 3067, WALK, "You pass through the agility shortcut.");
			} else if (player.absX == 2638) {
				handleAgility(player, -1, 0, 3067, WALK, "You pass through the agility shortcut.");
			}
			break;
		case 2618:
			if (player.absY == 3492) {
				handleAgility(player, 0, +2, 3067, WALK, "You jump over the broken fence.");
			} else if (player.absY == 3494) {
				handleAgility(player, -0, -2, 3067, WALK, "You jump over the broken fence.");
			}
			break;
		case 21738:
			brimhavenSkippingStone(player);
			break;
		case 21739:
			brimhavenSkippingStone(player);
			break;
		case 2296:
			if (player.absX == 2603) {
				handleAgility(player, -5, 0, 1, -1, "You pass through the agility shortcut.");
			} else if (player.absX == 2598) {
				handleAgility(player, 5, 0, -1, WALK, "You pass through the agility shortcut.");
			}
			break;
		}
	}

	public static int getAnimation(int objectId) {
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

	public static void brimhavenSkippingStone(final Player player) {
		if (player.stopPlayerPacket) {
			return;
		}
		player.stopPlayerPacket = true;
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(769));
				if (player.absX <= 2997) {
					stop();
				}
			}
		});
		Server.getTaskScheduler().schedule(new ScheduledTask(3) {

			@Override
			public void execute() {
				if (player.absX >= 2648) {
					player.teleportToX = player.absX - 2;
					player.teleportToY = player.absY - 5;
					if (player.absX <= 2997) {
						stop();
					}
				} else if (player.absX <= 2648) {
					player.teleportToX = player.absX + 2;
					player.teleportToY = player.absY + 5;
					if (player.absX >= 2645) {
						stop();
					}
				}

			}

			@Override
			public void onStop() {
				setAnimationBack(player);
				player.stopPlayerPacket = false;
			}
		});
	}

	private static void setAnimationBack(Player player) {
		player.setRunning(true);
		player.write(new SendConfig(173, 1));
		player.walkAnimation = 0x333;
		player.getPA().requestUpdates();
	}

}
