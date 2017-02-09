package com.model.game.character.player.skill.woodcutting;

import java.util.Optional;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.object.GlobalObject;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.utility.Utility;
import com.model.utility.cache.WorldObject;
import com.model.utility.cache.map.Region;

public class WoodcuttingEvent extends CycleEvent {
	
	private Player player;
	private Tree tree;
	private Hatchet hatchet;
	private int objectId, x, y, chops;
	
	public WoodcuttingEvent(Player player, Tree tree, Hatchet hatchet, int objectId, int x, int y) {
		this.player = player;
		this.tree = tree;
		this.hatchet = hatchet;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null || container.getOwner() == null) {
			container.stop();
			return;
		}
		if (!player.getItems().playerHasItem(hatchet.getItemId()) && !player.getItems().isWearingItem(hatchet.getItemId())) {
			player.write(new SendMessagePacket("Your axe has dissapeared."));
			container.stop();
			return;
		}
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < hatchet.getLevelRequired()) {
			player.write(new SendMessagePacket("You no longer have the level required to operate this hatchet."));
			container.stop();
			return;
		}
		if (player.getItems().getFreeSlots() == 0) {
			player.write(new SendMessagePacket("You have run out of free inventory space."));
			container.stop();
			return;
		}
		chops++;
		int chopChance = 1 + (int) (tree.getChopsRequired() * hatchet.getChopSpeed());
		if (Utility.getRandom(tree.getChopdownChance()) == 0 || tree.equals(Tree.NORMAL) && Utility.getRandom(chopChance) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = Region.getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, player.heightLevel, face, 10, tree.getRespawnTime(), objectId));
			player.getItems().addItem(tree.getWood(), 1);
			player.getSkills().addExperience(Skills.WOODCUTTING, tree.getExperience() * Constants.SKILL_MODIFIER);
			container.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Utility.getRandom(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				player.getItems().addItem(tree.getWood(), 1);
				player.getSkills().addExperience(Skills.WOODCUTTING, tree.getExperience() * Constants.SKILL_MODIFIER);
				//Achievements.increase(player, AchievementType.WOODCUTTING, 1);
			}
		}
		if (container.getTotalTicks() % 4 == 0) {
			player.playAnimation(Animation.create(hatchet.getAnimation()));
		}
	}
	
	@Override
	public void stop() {
		if (player != null) {
			player.playAnimation(Animation.create(65535));
		}
	}

}