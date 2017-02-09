package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendSkillPacket;
import com.model.task.ScheduledTask;

public class RestoreStats extends ScheduledTask {
	
	public RestoreStats() {
		super(60);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				for (int level = 0; level < player.getSkills().getLevels().length; level++) {
					if (player.getSkills().getLevel(level) < player.getSkills().getLevelForExperience(level)) {
						if (level != 5) { // prayer doesn't restore
							player.getSkills().setLevel(level, player.getSkills().getLevel(level) + 1);
							player.write(new SendSkillPacket(level));
						}
					} else if (player.getSkills().getLevel(level) > player.getSkills().getLevelForExperience(level)) {
						player.getSkills().setLevel(level, player.getSkills().getLevel(level) - 1);
						player.write(new SendSkillPacket(level));
					}
				}
			}
		}
	}

}
