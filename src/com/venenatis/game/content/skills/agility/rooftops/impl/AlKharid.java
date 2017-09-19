package com.venenatis.game.content.skills.agility.rooftops.impl;

import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.rooftops.Rooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;

public class AlKharid extends Rooftop {

	@Override
	public boolean requirements(Player player) {
		if(player.getSkills().getLevel(Skills.AGILITY) < 20) {
			SimpleDialogues.sendItemStatement(player, 6517, "", "You need an Agility level of 20 to use this course.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player, GameObject object) {
		if(requirements(player)) {
			switch(object.getId()) {
			case 10093:
				Agility.forceTeleport(player, Animation.create(828), Location.create(3273, 3192, 3), 0, 2);
				player.getSkills().addExperience(Skills.AGILITY, 10);
				return true;
			}
		}
		return false;
	}

}
