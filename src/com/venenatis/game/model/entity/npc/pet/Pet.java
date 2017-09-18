package com.venenatis.game.model.entity.npc.pet;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

/**
 * The class which represents functionality for the pet system.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class Pet extends NPC {

	//Create pet instance
	public Pet(Player owner, int id) {
		super(id, owner.getLocation(), 0);
		this.setLocation(owner.getLocation().transform(-1, 0));
		System.out.printf("%s v %s%n", owner.getLocation(), owner.getLocation().transform(-1,  0));
		this.isPet = true;
		this.spawnedBy = owner.getIndex();
		this.ownerId = owner.getIndex();
		this.face(owner.getLocation());
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
		if(onLogin) {
			if (player.getPet() > -1) {
	            Pet pet = new Pet(player, player.getPet());
	            player.setPet(player.getPet());
	            World.getWorld().register(pet);
	            Server.getTaskScheduler().schedule(new Task(4) {

					@Override
					public void execute() {
						// Pet despawned or owner offline
						if (player.getIndex() < 1 || pet.getIndex() < 1) {
							stop();
							return;
						}
						int delta = player.getLocation().distance(pet.getLocation());
						if (delta >= 13 || delta <= -13) {
							pet.teleport(player.getLocation().transform(-1, 0)); 
						}
					}
				});
	        }
			return false;
		}
		Pets petIds = Pets.from(item.getId());
		if (petIds != null) {
			if (player.getPet() > -1) {
				player.getActionSender().sendMessage("You already have a pet roaming.");
				return false;
			} else {
				Pet pet = new Pet(player, petIds.getNpc());
				player.setPet(petIds.getNpc());
				World.getWorld().register(pet);
				player.getInventory().remove(item);
				
				Server.getTaskScheduler().schedule(new Task(4) {

					@Override
					public void execute() {
						// Pet despawned or owner offline
						if (player.getIndex() < 1 || pet.getIndex() < 1) {
							stop();
							return;
						}
						int delta = player.getLocation().distance(pet.getLocation());
						if (delta >= 13 || delta <= -13) {
							pet.teleport(player.getLocation().transform(-1, 0)); 
						}
					}
				});
				return false;
			}
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
		Pets data = Pets.fromNpc(pet.getId());
		if(data == null) {
			return false;
		}
		
		if(player.getInventory().hasSpaceFor(new Item(data.getItem()))) {
			if (player.getPet() > -1) {
				player.playAnimation(Animation.create(827));
				player.setPet(-1);
				player.getInventory().add(new Item(data.getItem()));
				World.getWorld().unregister(pet);
			}
			return true;
		} else {
			player.getActionSender().sendMessage("You have no space in your inventory.");
			return false;
		}
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
		Pets pets = Pets.fromNpc(pet.getId());
		if (pets != null && player.getPet() > -1) {
			switch(pets) {
			case ABYSSAL_ORPHAN:
				break;
			case BABY_CHINCHOMPA:
				break;
			case BABY_CHINCHOMPA_BLACK:
				break;
			case BABY_CHINCHOMPA_GOLD:
				break;
			case BABY_CHINCHOMPA_GREY:
				break;
			case BABY_MOLE:
				break;
			case BEAVER:
				break;
			case BLOODHOUND:
				break;
			case CALLISTO_CUB:
				break;
			case CHAOS_ELEMENTAL:
				break;
			case CHOMPY_CHICK:
				break;
			case DAGANNOTH_PRIME:
				break;
			case DAGANNOTH_REX:
				break;
			case DAGANNOTH_SUPREME:
				break;
			case DARK_CORE:
				break;
			case GENERAL_GRAARDOR:
				break;
			case GIANT_SQUIRREL:
				break;
			case HELLPUPPY:
				break;
			case HERON:
				break;
			case KALPHITE_PRINCESS_BUG:
				break;
			case KALPHITE_PRINCESS_FLY:
				break;
			case KRAKEN:
				break;
			case KREEARRA:
				break;
			case KRIL_TSUTSAROTH:
				break;
			case OLMLET:
				break;
			case PENANCE_PET:
				break;
			case PHOENIX:
				break;
			case PRINCE_BLACK_DRAGON:
				break;
			case RIFT_GUARDIAN_AIR:
				break;
			case RIFT_GUARDIAN_ASTRAL:
				break;
			case RIFT_GUARDIAN_BLOOD:
				break;
			case RIFT_GUARDIAN_BODY:
				break;
			case RIFT_GUARDIAN_CHAOS:
				break;
			case RIFT_GUARDIAN_COSMIC:
				break;
			case RIFT_GUARDIAN_DEATH:
				break;
			case RIFT_GUARDIAN_EARTH:
				break;
			case RIFT_GUARDIAN_FIRE:
				break;
			case RIFT_GUARDIAN_LAW:
				break;
			case RIFT_GUARDIAN_MIND:
				break;
			case RIFT_GUARDIAN_NATURE:
				break;
			case RIFT_GUARDIAN_SOUL:
				break;
			case RIFT_GUARDIAN_WATER:
				break;
			case ROCKY:
				break;
			case ROCK_GOLEM:
				break;
			case ROCK_GOLEM_ADAMANTITE:
				break;
			case ROCK_GOLEM_BLURITE:
				break;
			case ROCK_GOLEM_COAL:
				break;
			case ROCK_GOLEM_COPPER:
				break;
			case ROCK_GOLEM_GOLD:
				break;
			case ROCK_GOLEM_GRANITE:
				break;
			case ROCK_GOLEM_IRON:
				break;
			case ROCK_GOLEM_MITHRIL:
				break;
			case ROCK_GOLEM_RUNITE:
				break;
			case ROCK_GOLEM_SILVER:
				break;
			case ROCK_GOLEM_TIN:
				break;
			case SCORPIAS_OFFSPRING:
				break;
			case SMOKE_DEVIL:
				break;
			case SNAKELING:
				break;
			case SNAKELING_BLUE:
				break;
			case SNAKELING_RED:
				break;
			case TANGLEROOT:
				break;
			case TZREK_JAD:
				break;
			case VENENATIS_SPIDERLING:
				break;
			case VETION_ORANGE:
				break;
			case VETION_PURPLE:
				break;
			case ZILYANA:
				break;
			default:
				break;
			
			}
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
		Pets pets = Pets.fromNpc(pet.getId());
		int morphId = -1;
		if (pets != null && player.getPet() > -1) {
			switch(pets) {
			case SNAKELING:
				morphId = 2131;
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

}
