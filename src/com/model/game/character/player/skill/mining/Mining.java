package com.model.game.character.player.skill.mining;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.dialogue.SimpleDialogues;
import com.model.game.location.Position;

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
	 * @param position	the location of the object
	 */
	public void mine(int objectId, Position position) {
		Rock rock = Rock.forObjectId(objectId);
		if (rock == null) {
			return;
		}
		if (player.getSkills().getLevel(Skills.MINING) < rock.getLevel()) {
			player.message("You need a mining level of " + rock.getLevel() + " to mine this.");
			return;
		}
		if (Server.getGlobalObjects().exists(Rock.EMPTY_ROCK, position.getX(), position.getY(), position.getZ())) {
			player.message("This vein contains no more minerals.");
			return;
		}
		Pickaxe pickaxe = Pickaxe.getBestPickaxe(player);
		if (pickaxe == null) {
			player.message("You need a pickaxe to mine this vein.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			SimpleDialogues.sendStatement(player, "You have no more free slots.");
			return;
		}
		int levelReduction = (int) Math.floor(player.getSkills().getLevel(Skills.MINING) / 10);
		int pickaxeReduction = pickaxe.getExtractionReduction();
		int extractionTime = rock.getExtractionRate() - (levelReduction + pickaxeReduction);
		if (extractionTime < MINIMUM_EXTRACTION_TIME) {
			extractionTime = MINIMUM_EXTRACTION_TIME;
		}
		player.message("You swing your pickaxe at the rock.");
		player.playAnimation(pickaxe.getAnimation());
		player.face(player, new Position(position.getX(), position.getY()));
		player.getSkillCyclesTask().stop();
		player.getSkillCyclesTask().setSkill(Skills.MINING);
		player.getSkillCyclesTask().add(new MiningEvent(player, objectId, position, rock, pickaxe), extractionTime);
	}
}