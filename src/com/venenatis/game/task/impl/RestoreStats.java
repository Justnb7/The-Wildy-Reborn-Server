package com.venenatis.game.task.impl;

import com.venenatis.game.content.SkillCapePerks;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class RestoreStats extends Task {
	
	public RestoreStats() {
		super(60);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.getCombatState().isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
					return;
				}
				int health = SkillCapePerks.HITPOINTS.isWearing(player) || SkillCapePerks.isWearingMaxCape(player) ? 2 : 1;
				player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + health);
				for (int level = 0; level < player.getSkills().getLevels().length; level++) {
					if (player.getSkills().getLevel(level) < player.getSkills().getLevelForExperience(level)) {
						if (level != 5) { // prayer doesn't restore
							player.getSkills().setLevel(level, player.getSkills().getLevel(level) + 1);
							player.getActionSender().sendSkillLevel(level);
						}
					} else if (player.getSkills().getLevel(level) > player.getSkills().getLevelForExperience(level)) {
						player.getSkills().setLevel(level, player.getSkills().getLevel(level) - 1);
						player.getActionSender().sendSkillLevel(level);
					}
				}
			}
		}
	}

}
