package com.model.game.character.player.skill.fletching.fletchable.impl;

import java.util.HashMap;

public enum Log {
	
	NORMAL_LOG(1511, 1, 40.0D),
	ACHEY_LOG(2862, 1, 40.0D),
	OAK_LOG(1521, 15, 60.0D),
	WILLOW_LOG(1519, 30, 90.0D),
	TEAK_LOG(6333, 35, 105.0D),
	ARCTIC_PINE_LOG(10810, 42, 125.0D),
	MAPLE_LOG(1517, 45, 135.0D),
	MOHOGANY_LOG(6332, 50, 157.5D),
	EUCALYPTUS_LOG(12581, 58, 193.5D),
	YEW_LOG(1515, 60, 202.5D),
	MAGIC_LOG(1513, 75, 460.5D);

	public static final void declare() {
		for (Log data : values())
			logs.put(Integer.valueOf(data.logId), data);
	}

	private int logId;
	private int levelRequired;
	private double experience;

	private static HashMap<Integer, Log> logs = new HashMap<Integer, Log>();

	public static Log getLogById(int id) {
		return logs.get(Integer.valueOf(id));
	}

	private Log(int logId, int levelRequired, double exp) {
		this.logId = logId;
		this.levelRequired = levelRequired;
		experience = exp;
	}

	public double getExperience() {
		return experience;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getLogId() {
		return logId;
	}
}