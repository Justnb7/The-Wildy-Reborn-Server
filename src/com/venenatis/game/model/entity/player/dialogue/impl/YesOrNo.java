package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class YesOrNo extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, "Select an Option", "Yes.", "No.");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		switch (index) {
		case 1: // Yes
			switch((Integer)player.getAttribute("yes_no_action")) {
			case 1:
				if (player.getInventory().contains(8851, 200)) {
					player.setTeleportTarget(new Location(2847, 3540, 2));
					player.getActionSender().removeAllInterfaces();
					player.getWarriorsGuild().cycle();
				} else {
					send(DialogueType.NPC, 4289, Expression.DEFAULT, "You need atleast 200 warrior guild tokens.", "You can get some by operating the armour animator.");
				}
				break;
			}
			break;
		case 2: // No
			player.getActionSender().removeAllInterfaces();
			break;
		}
	}

}
