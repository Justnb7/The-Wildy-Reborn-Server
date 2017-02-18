package com.model.game.character.player.content.achievements;

import com.model.game.character.player.Player;

public abstract class AchievementRequirement {
	/**
	 * 
	 * @param player
	 * @return The state at which the Player object has the ability or pre-requisites
	 */
	abstract boolean isAble(Player player);
}