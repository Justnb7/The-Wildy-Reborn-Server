package com.model.game.character.player.skill.woodcutting;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.location.Location;
import com.model.task.events.CycleEvent;

public class Woodcutting {
	
	private static final Woodcutting INSTANCE = new Woodcutting();
	
	public void chop(Player player, int objectId, int x, int y) {
		Tree tree = Tree.forObject(objectId);
		player.face(player, new Location(x, y));
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < tree.getLevelRequired()) {
			player.getActionSender().sendMessage("You do not have the woodcutting level required to cut this tree down.");
			return;
		}
		Axe axe = Axe.getBest(player);
		if (axe == null) {
			player.getActionSender().sendMessage("You must have an axe and the level required to cut this tree down.");
			return;
		}
		if (player.getInventory().remaining() == 0) {
			player.getActionSender().sendMessage("You must have at least one free inventory space to do this.");
			return;
		}
		if (Server.getGlobalObjects().exists(tree.getStumpId(), x, y)) {
			player.getActionSender().sendMessage("This tree has been cut down to a stump, you must wait for it to grow.");
			return;
		}
		player.getSkillCyclesTask().stop();
		player.getActionSender().sendMessage("You swing your axe at the tree.");
		player.playAnimation(axe.getAnimation());
		player.getSkillCyclesTask().setSkill(Skills.WOODCUTTING);
		CycleEvent woodcuttingEvent = new WoodcuttingEvent(player, tree, axe, objectId, x , y);
		player.getSkillCyclesTask().add(woodcuttingEvent, 1);
	}
	
	public static Woodcutting getInstance() {
		return INSTANCE;
	}

}
