package com.model.game.character.player.skill.woodcutting;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler.Skill;
import com.model.game.location.Position;
import com.model.task.events.CycleEvent;

public class Woodcutting {
	
	private static final Woodcutting INSTANCE = new Woodcutting();
	
	public void chop(Player player, int objectId, int x, int y) {
		Tree tree = Tree.forObject(objectId);
		player.face(new Position(x, y));
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < tree.getLevelRequired()) {
			player.write(new SendMessagePacket("You do not have the woodcutting level required to cut this tree down."));
			return;
		}
		Hatchet hatchet = Hatchet.getBest(player);
		if (hatchet == null) {
			player.write(new SendMessagePacket("You must have an axe and the level required to cut this tree down."));
			return;
		}
		if (player.getItems().getFreeSlots() == 0) {
			player.write(new SendMessagePacket("You must have at least one free inventory space to do this."));
			return;
		}
		if (Server.getGlobalObjects().exists(tree.getStumpId(), x, y)) {
			player.write(new SendMessagePacket("This tree has been cut down to a stump, you must wait for it to grow."));
			return;
		}
		player.getSkilling().stop();
		player.write(new SendMessagePacket("You swing your axe at the tree."));
		player.playAnimation(Animation.create(hatchet.getAnimation()));
		player.getSkilling().setSkill(Skill.WOODCUTTING);
		CycleEvent woodcuttingEvent = new WoodcuttingEvent(player, tree, hatchet, objectId, x , y);
		player.getSkilling().add(woodcuttingEvent, 1);
	}
	
	public static Woodcutting getInstance() {
		return INSTANCE;
	}

}