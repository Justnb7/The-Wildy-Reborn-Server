package com.venenatis.game.content.server_tasks;

import com.venenatis.game.model.Item;


public enum Tasks {
	
	ROCK_CRABS("Kill 100 rock crabs", new int[] {100}, 100, Difficulty.EASY),
	CUT_100_LOGS("100 regular logs", new int[] {}, 100, Difficulty.EASY);

	private final String task;
	
	private final int[] npcs;
	
	private final int completeAmount;
	
	private final Difficulty difficulty;
	
	private Item[] rewards;

	private Tasks(String task, int[] npcs, int completeAccount, Difficulty difficulty, Item... rewards) {
		this.task = task;
		this.npcs = npcs;
		this.completeAmount = completeAccount;
		this.difficulty = difficulty;
		this.rewards = rewards;
	}

	public int getReward() {
		switch (difficulty) {
		case MEDIUM:
			return 2;
		case HARD:
			return 3;
		case ELITE:
			return 4;
		case EASY:
		default:
			return 1;
		}
	}
	
	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @return the npcs
	 */
	public int[] getNpcs() {
		return npcs;
	}

	/**
	 * @return the completeAmount
	 */
	public int getCompleteAmount() {
		return completeAmount;
	}

	/**
	 * @return the difficulty
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return the rewards
	 */
	public Item[] getRewards() {
		return rewards;
	}
	
}
