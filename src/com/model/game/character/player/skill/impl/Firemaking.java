package com.model.game.character.player.skill.impl;

import org.omicron.jagex.runescape.CollisionMap;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.object.GlobalObject;
import com.model.task.ScheduledTask;

public class Firemaking extends SkillHandler {

	private static int[][] data = { { 1511, 1, 40, 26185 }, // LOG
			{ 7406, 1, 315, 11406 }, // BLUE LOG
			{ 7405, 1, 315, 11405 }, // GREEN LOG
			{ 7404, 1, 315, 11404 }, // RED LOG
			{ 10328, 1, 315, 20000 }, // WHITE LOG
			{ 10329, 1, 315, 20001 }, // PURPLE LOG
			{ 2862, 1, 40, 26185 }, // ACHEY
			{ 1521, 15, 60, 26185 }, // OAK
			{ 1519, 30, 105, 26185 }, // WILLOW
			{ 6333, 35, 105, 26185 }, // TEAK
			{ 1517, 45, 135, 26185 }, // MAPLE
			{ 10810, 45, 135, 26185 }, // ARTIC PINE
			{ 6332, 50, 158, 26185 }, // MAHOGANY
			{ 1515, 60, 203, 26185 }, // YEW
			{ 1513, 75, 304, 26185 }, // MAGIC
	};

	public static boolean playerLogs(int i, int l) {
		boolean flag = false;
		for (int[] aData : data) {
			if ((i == aData[0] && requiredItem(l)) || (requiredItem(i) && l == aData[0])) {
				flag = true;
			}
		}
		return flag;
	}

	private static int getAnimation(int item, int item1) {
		int[][] dataobtained = { { 841, 6714 }, { 843, 6715 }, { 849, 6716 }, { 853, 6717 }, { 857, 6718 }, { 861, 6719 }, };
		for (int[] aDataobtained : dataobtained) {
			if (item == aDataobtained[0] || item1 == aDataobtained[0]) {
				return aDataobtained[1];
			}
		}
		return 733;
	}

	private static boolean requiredItem(int i) {
		int[] dataobtained = { 841, 843, 849, 853, 857, 861, 590 };
		for (int aDataobtained : dataobtained) {
			if (i == aDataobtained) {
				return true;
			}
		}
		return false;
	}

	public static void grabData(final Player player, final int useWith, final int withUse) {
		if (player.absX == 2855 && player.absY == 3441) {
			return;
		}
		if (SkillHandler.isSkillActive(player, Skills.WOODCUTTING)) {
			player.write(new SendMessagePacket("You cannot perform this action while Woodcutting."));
			return;
		}
		final int[] coords = new int[3];
		coords[0] = player.getX();
		coords[1] = player.getY();
		coords[2] = player.getZ();
		for (int[] aData : data) {
			if (GlobalObject.fireExists(aData[3], player.getX(), player.getY(), player.getZ()) != null) {
				player.write(new SendMessagePacket("You can't light a fire on a fire!"));
				return;
			}
			if ((requiredItem(useWith) && withUse == aData[0] || useWith == aData[0] && requiredItem(withUse))) {
				if (player.getSkills().getLevel(Skills.FIREMAKING) < aData[1]) {
					player.write(new SendMessagePacket("You don't have the correct Firemaking level to light this log!"));
					player.write(new SendMessagePacket("You need the Firemaking level of at least " + aData[1] + "."));
					return;
				}
				if (System.currentTimeMillis() - player.lastFire > 1200) {

					if (player.playerIsFiremaking) {
						return;
					}

					final int[] time = new int[3];
					final int log = aData[0];
					final int fire = aData[3];
					if (System.currentTimeMillis() - player.lastFire > 3000) {
						player.playAnimation(Animation.create(getAnimation(useWith, withUse)));
						time[0] = 4;
						time[1] = 3;
					} else {
						time[0] = 1;
						time[1] = 2;
					}
					player.playerIsFiremaking = true;
					final GroundItem logItem = new GroundItem(new Item(log), coords[0], coords[1], coords[2], player);
					GroundItemHandler.createGroundItem(logItem);

					Server.getTaskScheduler().schedule(new ScheduledTask(time[0]) {
						@Override
						public void execute() {
							GlobalObject.createAnObject(player, fire, coords[0], coords[1]);
							GroundItemHandler.removeGroundItem(logItem);
							player.playerIsFiremaking = false;
							stop();
						}
					}.attach(player));

					if (!CollisionMap.isEastBlocked(player.heightLevel, player.absX - 1, player.absY)) {
						player.getPA().walkTo(-1, 0);
					} else if (!CollisionMap.isWestBlocked(player.heightLevel, player.absX + 1, player.absY)) {
						player.getPA().walkTo(1, 0);
					}

					player.write(new SendMessagePacket("You light the logs."));
					Server.getTaskScheduler().schedule(new ScheduledTask(time[1]) {
						@Override
						public void execute() {
							player.playAnimation(Animation.create(65535));
							stop();
						}
					}.attach(player));

					Server.getTaskScheduler().schedule(new ScheduledTask(100) {
						@Override
						public void execute() {
							GlobalObject.createAnObject(player, -1, coords[0], coords[1]);
							stop();
						}

						@Override
						public void onStop() {
							if (player.getOutStream() != null && player != null && player.isActive()) {
								GroundItemHandler.createGroundItem(new GroundItem(new Item(592), coords[0], coords[1], coords[2], player));
							}
						}
					}.attach(player));
					player.write(new SendMessagePacket("" + aData[3]));
					player.getSkills().addExperience(Skills.FIREMAKING, aData[2]);
					player.turnPlayerTo(player.getX() + 1, player.getY());
					player.getItems().deleteItem(aData[0], player.getItems().getItemSlot(aData[0]), 1);
					player.lastFire = System.currentTimeMillis();
				}
			}
		}
	}
}