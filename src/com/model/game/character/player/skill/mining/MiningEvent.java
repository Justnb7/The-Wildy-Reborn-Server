package com.model.game.character.player.skill.mining;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.object.GlobalObject;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.utility.Location3D;
import com.model.utility.Utility;

/**
 * Represents a singular event that is executed when a player attempts to mine. 
 * 
 * @author Jason MacKeigan
 * @date Feb 18, 2015, 6:17:11 PM
 */
public class MiningEvent extends CycleEvent {
	
	/**
	 * The amount of cycles that must pass before the animation is updated
	 */
	private final int ANIMATION_CYCLE_DELAY = 15;
	
	/**
	 * The value in cycles of the last animation
	 */
	private int lastAnimation;
	
	/**
	 * The player attempting to mine
	 */
	private final Player player;
	
	/**
	 * The pickaxe being used to mine
	 */
	private final Pickaxe pickaxe;
	
	/**
	 * The mineral being mined
	 */
	private final Mineral mineral;
	
	/**
	 * The object that we are mning
	 */
	private int objectId;
	
	/**
	 * The location of the object we're mining
	 */
	private Location3D location;
	
	/**
	 * The npc the player is mining, if any
	 */
	private Npc npc;
	
	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * @param player	the player this is created for
	 * @param objectId	the id value of the object being mined from
	 * @param location	the location of the object being mined from
	 * @param mineral	the mineral being mined
	 * @param pickaxe	the pickaxe being used to mine
	 */
	public MiningEvent(Player player, int objectId, Location3D location, Mineral mineral, Pickaxe pickaxe) {
		this.player = player;
		this.objectId = objectId;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}
	
	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * @param player	the player this is created for
	 * @param npc		the npc being from from
	 * @param location	the location of the npc
	 * @param mineral	the mineral being mined
	 * @param pickaxe	the pickaxe being used to mine
	 */
	public MiningEvent(Player player, Npc npc, Location3D location, Mineral mineral, Pickaxe pickaxe) {
		this.player = player;
		this.npc = npc;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}
	
	@Override 
	public void update(CycleEventContainer container) {
		if (player == null) {
			container.stop();
			return;
		}
		if (!player.getItems().playerHasItem(pickaxe.getItemId())
				&& !player.getItems().isWearingItem(pickaxe.getItemId())) {
			player.write(new SendMessagePacket("That is strange! The pickaxe could not be found."));
			container.stop();
			return;
		}
		if (player.getItems().getFreeSlots() == 0) {
			player.getDialogueHandler().sendStatement(player, "You have no more free slots.");
			container.stop();
			return;
		}
		if (objectId > 0) {
			if (Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ())) {
				player.write(new SendMessagePacket("This vein contains no more minerals."));
				container.stop();
				return;
			}
		} else {
			if (npc == null || npc.isDead) {
				player.write(new SendMessagePacket("This vein contains no more minerals."));
				container.stop();
				return;
			}
		}
		if (container.getTotalTicks() - lastAnimation > ANIMATION_CYCLE_DELAY) {
			player.playAnimation(Animation.create(pickaxe.getAnimation()));
			lastAnimation = container.getTotalTicks();
		}
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null) {
			container.stop();
			return;
		}
		if (Utility.getRandom(mineral.getDepletionProbability()) == 0 || mineral.getDepletionProbability() == 0) {
			if (objectId > 0) {
				Server.getGlobalObjects().add(new GlobalObject(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ(), 0, 10, mineral.getRespawnRate(), objectId));
			} else {
				npc.isDead = true;
				npc.needRespawn = false;
			}
		}
		player.turnPlayerTo(location.getX(), location.getY());
		player.getItems().addItem(mineral.getMineral(), 1);
		player.getSkills().addExperience(Skills.MINING, mineral.getExperience());
	}
	
	@Override
	public void stop() {
		if (player == null) {
			return;
		}
		player.stopAnimation();
	}
}
