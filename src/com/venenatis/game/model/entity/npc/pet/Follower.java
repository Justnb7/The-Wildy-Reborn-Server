package com.venenatis.game.model.entity.npc.pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.impl.RegionalPetCheck;
import com.venenatis.game.world.World;

/**
 * The class which represents functionality for the pet system.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class Follower extends NPC {
	
	/**
	 * The random number generator for this class.
	 */
	private static final Random random = new Random();

	/**
	 * Create pet instance
	 * 
	 * @param owner
	 *            The owner of the pet
	 * @param id
	 *            The pet id
	 */
	public Follower(Player owner, int id) {
		super(id, owner.getLocation(), 0);
		this.setLocation(owner.getLocation().transform(-1, 0));
		//System.out.printf("%s v %s%n", owner.getLocation(), owner.getLocation().transform(-1,  0));
		this.isPet = true;
		this.spawnedBy = owner;
		this.faceEntity(owner);
	}
	
	/**
	 * Drop the pet item, making the pet appear
	 * 
	 * @param player
	 *            The player dropping the item
	 * @param item
	 *            The pet item being dropped
	 * @param onLogin
	 *            Spawns the pet upon login
	 * @return Spawn the pet
	 */
	public static boolean drop(Player player, Item item, boolean onLogin) {
		
		//Check if we have an pet spawned already, we can only spawn one.
		if (player.getPet() > -1 && !onLogin) {
			player.message("You already have a pet roaming.");
			return false;
		}
		
		//Spawn the pet based on the Id stored in the players file.
		if(onLogin) {
			Pet pet = Pet.fromNpc(player.getPet());
			
			if (pet != null && player.getPet() > -1) {
				Follower p = new Follower(player, pet.getNpc());
				player.setPet(pet.getNpc());
				player.setAttribute("pet", p);
				World.getWorld().register(p);
				World.getWorld().schedule(new RegionalPetCheck(player, p, true));
			}
			return false;
		}
		
		//Spawn pet based on the item being dropped by the player.
		Pet petIds = Pet.from(item.getId());
		
		if (petIds != null) {
			Follower pet = new Follower(player, petIds.getNpc());
			player.setPet(petIds.getNpc());
			player.setAttribute("pet", pet);
			World.getWorld().register(pet);
			player.getInventory().remove(item);
			
			//Regional check if out of distance respawn pet.
			World.getWorld().schedule(new RegionalPetCheck(player, pet, false));
			return false;
		}
		return true;
	}

	/**
	 * Picks up the pet npc
	 * 
	 * @param player
	 *            The player picking up the pet
	 * @param pet
	 *            The pet being picked up
	 * @return despawn the pet, and respawn the pet item in the players
	 *         inventory.
	 */
	public static boolean pickup(Player player, NPC pet) {
		Pet data = Pet.fromNpc(pet.getId());
		if(data == null) {
			return false;
		}
		
		if(player.getInventory().hasSpaceFor(new Item(data.getItem()))) {
			//We can only pick up pets if there is atleast one spawned.
			if (player.getPet() > -1) {
				player.playAnimation(Animation.create(827));
				player.setPet(-1);
				player.removeAttribute("pet");
				player.getInventory().add(new Item(data.getItem()));
				World.getWorld().unregister(pet);
			}
			return true;
		} else {
			player.message("You have no space in your inventory.");
			return false;
		}
	}
	
	/**
	 * Does the pet have an chat option?
	 * 
	 * @param player
	 *            The player attempting to talk to the pet
	 * @param pet
	 *            The actual pet with the chat dialogue
	 * @return {@code true} if the npc is an pet and we have one spawned, {@code false} otherwise.
	 */
	public static boolean canTalkToPet(Player player, NPC pet) {
		return Pet.fromNpc(pet.getId()) != null && player.getPet() > -1;
	}
	
	/**
	 * Right click option to start talking to your pet.
	 * @param player
	 *        The player who owns the pet
	 * @param pet
	 *        The pet
	 * @return
	 */
	public static boolean talktoPet(Player player, NPC pet) {
		Pet pets = Pet.fromNpc(pet.getId());
		if (canTalkToPet(player, pet)) {
			player.getDialogueManager().start(pets.getPetDialogue(), player);
			return true;
		}
		return false;
	}
	
	/**
	 * Sends the pet transform mask.
	 * 
	 * @param player
	 *            The player owning the pet.
	 * @param pet
	 *            The pet being transformed.
	 */
	public static boolean transformPet(Player player, NPC pet) {
		Pet pets = Pet.fromNpc(pet.getId());
		int morphId = -1;
		
		if (pets != null && player.getPet() > -1 && pet.spawnedBy == player) {
			switch(pets) {
			
			case KALPHITE_PRINCESS_BUG:
				morphId = Pet.KALPHITE_PRINCESS_FLY.getNpc();
				break;
				
			case KALPHITE_PRINCESS_FLY:
				morphId = Pet.KALPHITE_PRINCESS_BUG.getNpc();
				break;
				
			case VETION_PURPLE:
				morphId = Pet.VETION_ORANGE.getNpc();
				break;
				
			case VETION_ORANGE:
				morphId = Pet.VETION_PURPLE.getNpc();
				break;
				
			case SNAKELING:
				morphId = Pet.SNAKELING_RED.getNpc();
				break;
				
			case SNAKELING_RED:
				morphId = Pet.SNAKELING_BLUE.getNpc();
				break;
				
			case SNAKELING_BLUE:
				morphId = Pet.SNAKELING.getNpc();
				break;
				
			case BABY_CHINCHOMPA:
				if (random.nextInt(1000) == 0) {
					morphId = Pet.BABY_CHINCHOMPA_GOLD.getNpc();
				} else {
					morphId = Pet.BABY_CHINCHOMPA_GREY.getNpc();
				}
				break;
				
			case BABY_CHINCHOMPA_GREY:
				if (random.nextInt(1000) == 0) {
					morphId = Pet.BABY_CHINCHOMPA_GOLD.getNpc();
				} else {
					morphId = Pet.BABY_CHINCHOMPA_BLACK.getNpc();
				}
				break;
				
			case BABY_CHINCHOMPA_BLACK:
				if (random.nextInt(1000) == 0) {
					morphId = Pet.BABY_CHINCHOMPA_GOLD.getNpc();
				} else {
					morphId = Pet.BABY_CHINCHOMPA.getNpc();
				}
				break;
				
			case BABY_CHINCHOMPA_GOLD:
				if (random.nextInt(1000) == 0) {
					morphId = Pet.BABY_CHINCHOMPA_GOLD.getNpc();
				} else {
					morphId = Pet.BABY_CHINCHOMPA.getNpc();
				}
				break;
				
			case NOON:
				morphId = Pet.MIDNIGHT.getNpc();
				break;
				
			case MIDNIGHT:
				morphId = Pet.NOON.getNpc();
				break;
			default:
				break;
			
			}
			pet.requestTransform(morphId);
			player.setPet(morphId);
			return true;
		}
		return false;
	}

	public static int getBasePetNpcId(int petId) {
		if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Rift guardian")) {
			petId = Pet.RIFT_GUARDIAN_FIRE.getNpc();
		} else if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Snakeling")) {
			petId = Pet.SNAKELING.getNpc();
		} else if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Kalphite Princess")) {
			petId = Pet.KALPHITE_PRINCESS_BUG.getNpc();
		} else if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Vet'ion Jr.")) {
			petId = Pet.VETION_PURPLE.getNpc();
		} else if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Baby Chinchompa")) {
			petId = Pet.BABY_CHINCHOMPA.getNpc();
		} else if (NPCDefinitions.get(petId).getName().equalsIgnoreCase("Rock Golem")) {
			petId = Pet.ROCK_GOLEM.getNpc();
		}
		return petId;
	}

	public static ArrayList<Integer> getItemAlternatives(int petId) {
		ArrayList<Integer> alternatives = new ArrayList<Integer>();

		NPCDefinitions npcDef = NPCDefinitions.get(Pet.from(petId).getNpc());

		if (npcDef.getName().equals("Rift guardian")) {
			alternatives.addAll(Arrays.asList(Pet.RIFT_GUARDIAN_AIR.getItem(), Pet.RIFT_GUARDIAN_ASTRAL.getItem(),
					Pet.RIFT_GUARDIAN_BLOOD.getItem(), Pet.RIFT_GUARDIAN_BODY.getItem(),
					Pet.RIFT_GUARDIAN_CHAOS.getItem(), Pet.RIFT_GUARDIAN_COSMIC.getItem(),
					Pet.RIFT_GUARDIAN_DEATH.getItem(), Pet.RIFT_GUARDIAN_EARTH.getItem(),
					Pet.RIFT_GUARDIAN_FIRE.getItem(), Pet.RIFT_GUARDIAN_LAW.getItem(),
					Pet.RIFT_GUARDIAN_MIND.getItem(), Pet.RIFT_GUARDIAN_NATURE.getItem(),
					Pet.RIFT_GUARDIAN_SOUL.getItem(), Pet.RIFT_GUARDIAN_WATER.getItem()));
		} else if (npcDef.getName().equals("Snakeling")) {
			alternatives.addAll(Arrays.asList(Pet.SNAKELING.getItem(), Pet.SNAKELING_RED.getItem(),
					Pet.SNAKELING_BLUE.getItem()));
		} else if (npcDef.getName().equals("Kalphite Princess")) {
			alternatives
					.addAll(Arrays.asList(Pet.KALPHITE_PRINCESS_BUG.getItem(), Pet.KALPHITE_PRINCESS_FLY.getItem()));
		} else if (npcDef.getName().equals("Vet'ion Jr.")) {
			alternatives.addAll(Arrays.asList(Pet.VETION_PURPLE.getItem(), Pet.VETION_ORANGE.getItem()));
		} else if (npcDef.getName().equals("Baby Chinchompa")) {
			alternatives.addAll(Arrays.asList(Pet.BABY_CHINCHOMPA.getItem(), Pet.BABY_CHINCHOMPA_BLACK.getItem(),
					Pet.BABY_CHINCHOMPA_GREY.getItem(), Pet.BABY_CHINCHOMPA_GOLD.getItem()));
		} else if (npcDef.getName().equals("Rock Golem")) {
			alternatives.addAll(Arrays.asList(Pet.ROCK_GOLEM.getItem(), Pet.ROCK_GOLEM_TIN.getItem(),
					Pet.ROCK_GOLEM_COPPER.getItem(), Pet.ROCK_GOLEM_IRON.getItem(), Pet.ROCK_GOLEM_BLURITE.getItem(),
					Pet.ROCK_GOLEM_SILVER.getItem(), Pet.ROCK_GOLEM_COAL.getItem(), Pet.ROCK_GOLEM_GOLD.getItem(),
					Pet.ROCK_GOLEM_MITHRIL.getItem(), Pet.ROCK_GOLEM_GRANITE.getItem(),
					Pet.ROCK_GOLEM_ADAMANTITE.getItem(), Pet.ROCK_GOLEM_RUNITE.getItem()));
		} else {
			alternatives.add(petId);
		}

		return alternatives;
	}

	/**
	 * Checks if we already have this pet.
	 * 
	 * @param player
	 *            The player to check
	 * @param itemId
	 *            The pet item to check
	 */
	public static boolean hasPet(Player player, int itemId) {
		for (int pet : getItemAlternatives(itemId)) {
			if (player.getInventory().contains(pet) || player.getBank().contains(pet) || (player.getPet() > 0 && Pet.fromNpc(player.getPet()).getItem() == pet)) {
				return true;
			}
		}
		return false;
	}

}