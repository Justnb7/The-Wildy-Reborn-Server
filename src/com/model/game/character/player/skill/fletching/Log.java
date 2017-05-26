package com.model.game.character.player.skill.fletching;

import java.util.HashMap;
import java.util.Map;

public enum Log {
	
	NORMAL(1511, new int[] { 53, 841, 839, 9174 }, new int[] { 1, 5, 10, 9 }, new double[] { 5, 10, 20, 24 }),
	
	OAK(1521, new int[] { 843, 9177, 845 }, new int[] { 20, 24, 25 }, new double[] { 16.5, 88, 25 }),
	
	WILLOW(1519, new int[] { 849, 9181, 847 }, new int[] { 35, 39, 40 }, new double[] { 66.6, 128, 83}),
	
	MAPLE(1517, new int[] { 853, 9183, 851 }, new int[] { 50, 54, 55 }, new double[] { 100, 164, 116.6 }),
	
	YEW(1515, new int[] { 857, 9185, 855 }, new int[] { 65, 69, 70 }, new double[] { 135, 200, 150 }),
	
	MAGIC(1513, new int[] { 861, 859 }, new int[] { 80, 85 }, new double[] { 166.6, 183 });
	
	/**
	 * The id of the logs
	 */
	private int logId;
	
	/**
	 * @return the logId
	 */
	public int getLogId() {
		return logId;
	}

	/**
	 * The first item displayed on the fletching interface.
	 */
	private int[] item;

	/**
	 * The level required to fletch the first item on the fletching interface.
	 */
	private int[] level;

	/**
	 * The experience granted for the first item on the flteching interface.
	 */
	private double[] experience;

	/**
	 * A map of item ids to logs.
	 */
	private static Map<Integer, Log> logs = new HashMap<Integer, Log>();

	/**
	 * Gets a log by an item id.
	 * @param item The item id.
	 * @return The Log, or <code>null</code> if the object is not a log.
	 */
	public static Log forId(int item) {
		return logs.get(item);
	}

	/**
	 * Populates the log map.
	 */
	static {
		for (Log log : Log.values()) {
			logs.put(log.logId, log);
		}
	}
	
	private Log(int logId, int[] item, int[] level, double[] experience) {
		this.logId = logId;
		this.item = item;
		this.level = level;
		this.experience = experience;
	}

	/**
	 * @return the item
	 */
	public int[] getItem() {
		return item;
	}

	/**
	 * @return the level
	 */
	public int[] getLevel() {
		return level;
	}

	/**
	 * @return the experience
	 */
	public double[] getExperience() {
		return experience;
	}
}
