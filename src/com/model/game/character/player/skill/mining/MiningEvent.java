package com.model.game.character.player.skill.mining;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.Pets;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.dialogue.SimpleDialogues;
import com.model.game.location.Position;
import com.model.game.object.GlobalObject;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
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
	private final Rock rock;
	
	/**
	 * The object that we are mning
	 */
	private int objectId;
	
	/**
	 * The position of the object we're mining
	 */
	private Position position;
	
	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * @param player	the player this is created for
	 * @param objectId	the id value of the object being mined from
	 * @param position	the position of the object being mined from
	 * @param mineral	the mineral being mined
	 * @param pickaxe	the pickaxe being used to mine
	 */
	public MiningEvent(Player player, int objectId, Position position, Rock rock, Pickaxe pickaxe) {
		this.player = player;
		this.objectId = objectId;
		this.position = position;
		this.rock = rock;
		this.pickaxe = pickaxe;
	}
	
	@Override 
	public void update(CycleEventContainer container) {
		if (player == null) {
			container.stop();
			return;
		}
		if (!player.getItems().playerHasItem(pickaxe.getId()) && !player.getItems().isWearingItem(pickaxe.getId())) {
			player.getActionSender().sendMessage("That is strange! The pickaxe could not be found.");
			container.stop();
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			SimpleDialogues.sendStatement(player, "You have no more free slots.");
			container.stop();
			return;
		}
		if (objectId > 0) {
			if (Server.getGlobalObjects().exists(Rock.EMPTY_ROCK, position.getX(), position.getY(), position.getZ())) {
				player.getActionSender().sendMessage("This vein contains no more rocks.");
				container.stop();
				return;
			}
		}
		if (container.getTotalTicks() - lastAnimation > ANIMATION_CYCLE_DELAY) {
			player.playAnimation(pickaxe.getAnimation());
			lastAnimation = container.getTotalTicks();
		}
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null) {
			container.stop();
			return;
		}
		//if (Utility.random(rock.getDepletionProbability()) == 0) {
			if (objectId > 0) {
				Server.getGlobalObjects().add(new GlobalObject(Rock.EMPTY_ROCK, position.getX(), position.getY(), position.getZ(), 0, 10, rock.getRespawnRate(), objectId));
			}
		//}
		player.face(player, new Position(position.getX(), position.getY()));
		player.getItems().addItem(rock.getMineral(), 1);
		player.getSkills().addExperience(Skills.MINING, rock.getExperience());
		int random = Utility.random(rock.getPetChance());
		
		if (random == 0) {
			if(player.isPetSpawned()) {
				player.getBank().addOrSendToBank(13321, 1);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Rock Golem.", false);
			} else {
				Pets pets = Pets.ROCK_GOLEM;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPetSpawned(true);
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Rock Golem.", false);
			}
			
		}
	}
	
	@Override
	public void stop() {
		if (player == null) {
			return;
		}
		player.playAnimation(Animation.create(-1));
	}
}