package com.model.game.character.player.skill.mining;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler.Skill;
import com.model.utility.Location3D;

/**
 * The {@link Mining} class will manage all operations that the mining skill entails. 
 * 
 * @author Jason MacKeigan
 * @date Feb 18, 2015, 5:09:38 PM
 */
public class Mining {
	
	private static final int MINIMUM_EXTRACTION_TIME = 2;
	
	/**
	 * The player that this {@link Mining} object is created for
	 */
	private final Player player;
	
	/**
	 * Constructs a new mining class for a singular player
	 * @param player	the player this class is being created for
	 */
	public Mining(Player player) {
		this.player = player;
	}
	
	/**
	 * This function allows a singular player to start mining if possible
	 * @param objectId	the object the player is trying to mine from
	 * @param location	the location of the object
	 */
	public void mine(int objectId, Location3D location) {
		Mineral mineral = Mineral.forObjectId(objectId);
		if (mineral == null) {
			return;
		}
		if (player.getSkills().getLevel(Skill.MINING.getId()) < mineral.getLevel()) {
			player.write(new SendMessagePacket("You need a mining level of " + mineral.getLevel() + " to mine this."));
			return;
		}
		if (Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ())) {
			player.write(new SendMessagePacket("This vein contains no more minerals."));
			return;
		}
		Pickaxe pickaxe = Pickaxe.getBestPickaxe(player);
		if (pickaxe == null) {
			player.write(new SendMessagePacket("You need a pickaxe to mine this vein."));
			return;
		}
		if (player.getItems().getFreeSlots() == 0) {
			player.getDialogueHandler().sendStatement(player, "You have no more free slots.");
			return;
		}
		int levelReduction = (int) Math.floor(player.getSkills().getLevel(Skill.MINING.getId()) / 10);
		int pickaxeReduction = pickaxe.getExtractionReduction();
		int extractionTime = mineral.getExtractionRate() - (levelReduction + pickaxeReduction);
		if (extractionTime < MINIMUM_EXTRACTION_TIME) {
			extractionTime = MINIMUM_EXTRACTION_TIME;
		}
		player.write(new SendMessagePacket("You swing your pickaxe at the rock."));
		player.playAnimation(Animation.create(pickaxe.getAnimation()));
		player.turnPlayerTo(location.getX(), location.getY());
		player.getSkilling().stop();
		player.getSkilling().setSkill(Skill.MINING);
		player.getSkilling().add(new MiningEvent(player, objectId, location, mineral, pickaxe), extractionTime);
	}
	
	/**
	 * This function allows a singular player to start mining on an npc if possible
	 * @param npc		the non playable character we're mining from
	 * @param mineral	the mineral we're going to obtain from mining
	 * @param location	the location of the npc and or mineral
	 */
	public void mine(Npc npc, Mineral mineral, Location3D location) {
		if (npc == null || npc.isDead) {
			player.write(new SendMessagePacket("This contains no more minerals."));
			return;
		}
		Pickaxe pickaxe = Pickaxe.getBestPickaxe(player);
		if (pickaxe == null) {
			player.write(new SendMessagePacket("You need a pickaxe to mine this vein."));
			return;
		}
		if (player.getItems().getFreeSlots() == 0) {
			player.getDialogueHandler().sendStatement(player,"You have no more free slots.");
			return;
		}
		int levelReduction = (int) Math.floor(player.getSkills().getLevel(Skill.MINING.getId()) / 10);
		int pickaxeReduction = pickaxe.getExtractionReduction();
		int extractionTime = mineral.getExtractionRate() - (levelReduction + pickaxeReduction);
		if (extractionTime < MINIMUM_EXTRACTION_TIME) {
			extractionTime = MINIMUM_EXTRACTION_TIME;
		}
		player.write(new SendMessagePacket("You swing your pickaxe at the rock."));
		player.playAnimation(Animation.create(pickaxe.getAnimation()));
		player.turnPlayerTo(location.getX(), location.getY());
		player.getSkilling().stop();
		player.getSkilling().setSkill(Skill.MINING);
		player.getSkilling().add(new MiningEvent(player, npc, location, mineral, pickaxe), extractionTime);
	}

}