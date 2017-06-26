package com.venenatis.game.content.activity.minigames.impl.duelarena;

import com.venenatis.game.model.entity.player.Player;

public interface RuleCondition {

	boolean canSelect(final Player player, final DuelRules rules);
	
}