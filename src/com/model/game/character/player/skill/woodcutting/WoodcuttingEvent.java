package com.model.game.character.player.skill.woodcutting;

import java.util.Optional;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.item.container.impl.Equipment;
import com.model.game.object.GlobalObject;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.utility.Utility;
import com.model.utility.cache.WorldObject;
import com.model.utility.cache.map.Region;

public class WoodcuttingEvent extends CycleEvent {

	private Player player;
	private Tree tree;
	private Axe axe;
	private int objectId, x, y, chops;
	
	public WoodcuttingEvent(Player player, Tree tree, Axe axe, int objectId, int x, int y) {
		this.player = player;
		this.tree = tree;
		this.axe = axe;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute(CycleEventContainer container) {
		if(player == null || !player.isActive() || player.isTeleporting() || player.isDead || container.getOwner() == null) {
			container.stop();
			return;
		}
		if (!player.getItems().playerHasItem(axe.getItemId()) && !player.getEquipment().contains(axe.getItemId())) {
			player.getActionSender().sendMessage("Your axe has dissapeared.");
			container.stop();
			return;
		}
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < axe.getLevelRequired()) {
			player.getActionSender().sendMessage("You no longer have the level required to operate this hatchet.");
			container.stop();
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.getActionSender().sendMessage("You have run out of free inventory space.");
			container.stop();
			return;
		}
		chops++;
		int chopChance = 1 + (int) (tree.getChopsRequired() * axe.getChopSpeed());
		if (Utility.random(tree.getChopdownChance()) == 0 || tree.equals(Tree.NORMAL) && Utility.random(chopChance) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = Region.getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			if(tree.equals(Tree.RED_WOOD) && player.getX() == 1574) {
				Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, player.heightLevel, 3, 10, tree.getRespawnTime(), objectId));
			} else if(tree.equals(Tree.RED_WOOD) && player.getX() == 1567) {
				Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, player.heightLevel, 5, 10, tree.getRespawnTime(), objectId));
			} else if(!tree.equals(Tree.RED_WOOD)) {
				Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, player.heightLevel, face, 10, tree.getRespawnTime(), objectId));
			}
			player.getItems().addItem(tree.getWood(), 1);
			if (wearingLumberjackOutfit()) {
				player.getSkills().addExperience(Skills.WOODCUTTING, tree.getExperience());
			} else {
				player.getSkills().addExperience(Skills.WOODCUTTING, tree.getExperience());
			}
			container.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Utility.random(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				player.getItems().addItem(tree.getWood(), 1);
				player.getSkills().addExperience(Skills.WOODCUTTING, tree.getExperience());
				Achievements.increase(player, AchievementType.WOODCUTTING, 1);
			}
		}
		if (container.getTotalTicks() % 4 == 0) {
			player.playAnimation(axe.getAnimation());
		}
	}
	
	private boolean wearingLumberjackOutfit() {
		if (player.getEquipment().getId(Equipment.HEAD_SLOT) == 10941
				&& player.getEquipment().getId(Equipment.CHEST_SLOT) == 10939
				&& player.getEquipment().getId(Equipment.LEGS_SLOT) == 10940
				&& player.getEquipment().getId(Equipment.FEET_SLOT) == 10933)
			return true;
		return false;
	}
	
	@Override
	public void stop() {
		if (player != null) {
			player.playAnimation(Animation.create(-1));
		}
	}
}
