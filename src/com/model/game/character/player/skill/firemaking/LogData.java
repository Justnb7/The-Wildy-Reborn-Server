package com.model.game.character.player.skill.firemaking;

/**
 * Represents all our log data, for the firemaking skill.
 * @author Patrick van Elderen
 *
 */
public enum LogData {

	LOG(1511, 1, 40, 26185),
	Achey_LOG(2862, 1, 40, 26185),
	OAK_LOG(1521, 15, 60, 26185),
	WILLOW_LOG(1519, 30, 90, 26185),
	TEAK_LOG(6333, 35, 105, 26185),
	ARCTIC_PINE_LOG(10810, 42, 125, 26185),
	MAPLE_LOG(1517, 45, 135, 26185),
	ARTIC_PINE_LOG(10810, 42, 125, 26185),
	MAHOGANY_LOG(6332, 50, 157.5, 26185),
	YEW_LOG(1515, 60, 202.5, 26185),
	MAGIC_LOG(1513, 75, 303.8, 26185),
	REDWOOD_LOG(19669, 90, 350, 26185),
	BLUE_LOG(7406, 1, 250, 26576),
	GREEN_LOG(7405, 1, 250, 26575),
	RED_LOG(7404, 1, 250, 26186),
	WHITE_LOG(10328, 1, 250, 20000),
	PURPLE_LOG(10329, 1, 250, 20001);

	private int log;
	private int level;
	private double exp;
	private int fire;

	private LogData(int log, int level, double exp, int fire) {
		this.log = log;
		this.level = level;
		this.exp = exp;
		this.fire = fire;
	}

	public int getLog() {
		return log;
	}

	public int getLevel() {
		return level;
	}

	public double getExperience() {
		return exp;
	}
	
	public int getFire() {
		return fire;
	}

}
