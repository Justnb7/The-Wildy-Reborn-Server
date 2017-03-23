package com.model.game.character.npc.pet;

import java.util.HashMap;
import java.util.Map;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;

/**
 * A pet system that uses the Npc class rather than loops
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Pet extends NPC {
	
	//Enum data credits to Stan Jansen.
	public enum Pets {
        //item id, npc id
		ABYSSAL_ORPHAN(13262, 5883),
		BABY_MOLE(12646, 6635),
		CALLISTO_CUB(13178, 497),
		HELLPUPPY(13247, 964),
		CHAOS_ELEMENTAL(11995, 5907),
		DAGANNOTH_PRIME(12644, 6627),
		DAGANNOTH_REX(12645, 6630),
		DAGANNOTH_SUPREME(12643, 6626),
		DARK_CORE(12816, 388),
		GENERAL_GRAARDOR(12650, 6632),
		KRIL_TSUTSAROTH(12652, 6634),
		KRAKEN(12655, 6640),
		KREEARRA(12649, 6631),
		PENANCE_PET(12703, 6642),
		SMOKE_DEVIL(12648, 6639),
		ZILYANA(12651, 6633),
		SNAKELING(12921, 2130),
		SNAKELING_RED(12939, 2131),
		SNAKELING_BLUE(12940, 2132),
		PRINCE_BLACK_DRAGON(12653, 6636),
		SCORPIAS_OFFSPRING(13181, 5547),
		TZREK_JAD(13225, 5892),
		VENENATIS_SPIDERLING(13177, 5557),
		VETION_PURPLE(13179, 5559),
		VETION_ORANGE(13180, 5560),
		KALPHITE_PRINCESS_BUG(12647, 6638),
		KALPHITE_PRINCESS_FLY(12654, 6637),
		HERON(13320, 6715),
		ROCK_GOLEM(13321, 7439),
		ROCK_GOLEM_TIN(21187, 7440),
		ROCK_GOLEM_COPPER(21188, 7441),
		ROCK_GOLEM_IRON(21189, 7442),
		ROCK_GOLEM_BLURITE(21190, 7443),
		ROCK_GOLEM_SILVER(21191, 7444),
		ROCK_GOLEM_COAL(21192, 7445),
		ROCK_GOLEM_GOLD(21193, 7446),
		ROCK_GOLEM_MITHRIL(21194, 7447),
		ROCK_GOLEM_GRANITE(21195, 7448),
		ROCK_GOLEM_ADAMANTITE(21196, 7449),
		ROCK_GOLEM_RUNITE(21197, 7450),
		BEAVER(13322, 6717),
		GIANT_SQUIRREL(20659,7334),
		ROCKY(20663, 7336),
		TANGLEROOT(20661, 7335),
		RIFT_GUARDIAN_FIRE(20665, 7337),
		RIFT_GUARDIAN_AIR(20667, 7337),
		RIFT_GUARDIAN_MIND(20669, 7337),
		RIFT_GUARDIAN_WATER(20671, 7337),
		RIFT_GUARDIAN_EARTH(20673, 7337),
		RIFT_GUARDIAN_CHAOS(20675, 7337),
		RIFT_GUARDIAN_BODY(20677, 7337),
		RIFT_GUARDIAN_COSMIC(20679, 7337),
		RIFT_GUARDIAN_NATURE(20681, 7337),
		RIFT_GUARDIAN_LAW(20683, 7337),
		RIFT_GUARDIAN_DEATH(20685, 7337),
		RIFT_GUARDIAN_SOUL(20687, 7337),
		RIFT_GUARDIAN_ASTRAL(20689, 7337),
		RIFT_GUARDIAN_BLOOD(20691, 7337),
		BABY_CHINCHOMPA(13323, 6756),
		BABY_CHINCHOMPA_GREY(13324, 6757),
		BABY_CHINCHOMPA_BLACK(13325, 6758),
		BABY_CHINCHOMPA_GOLD(13326, 6759),
		CHOMPY_CHICK(13071, 4001),
		BLOODHOUND(19730, 7232),
		PHOENIX(20693, 7368),
		OLMLET(20851, 7519);

		/**
		 * The pet item
		 */
        private final int item;
        
        /**
         * The pet
         */
        private final int npc;

        /**
         * Constructs an pet
         * @param item
         *         The itemId
         * @param npc
         *         The npcId
         */
        Pets(int item, int npc) {
            this.item = item;
            this.npc = npc;
        }

        /**
         * We store all our pet items in a map so we can access them later.
         */
        private static Map<Integer, Pets> petItems = new HashMap<Integer, Pets>();
        
        /**
         * We're also storing our pet npcs in a map so we can access these later too.
         */
        private static Map<Integer, Pets> petNpcs = new HashMap<Integer, Pets>();

        /**
         * Get the pet by itemId
         * @param item
         *         The itemId
         * @return The pet item
         */
        public static Pets from(int item) {
            return petItems.get(item);
        }

        /**
         * Grabs the pet by npcId
         * @param npc
         *         The npc Id
         * @return The pet npc
         */
        public static Pets fromNpc(int npc) {
            return petNpcs.get(npc);
        }

        static {
            for (Pets pet : Pets.values()) {
                petItems.put(pet.item, pet);
            }
            for (Pets pet : Pets.values()) {
                petNpcs.put(pet.npc, pet);
            }
        }

        /**
         * An public getter for the pet item
         * @return
         */
        public int getItem() {
            return item;
        }

        /**
         * A public getter for the pet npc
         * @return
         */
        public int getNpc() {
            return npc;
        }
        
    }

	//Create pet instance
	public Pet(Player owner, int id) {
		super(id, owner.getPosition(), 0);
		this.setAbsX(owner.getX());
		this.setAbsY(owner.getY() - 1);
		this.isPet = true;
		this.spawnedBy = owner.getIndex();
		this.ownerId = owner.getIndex();
		this.faceEntity(owner);
	}
	
	/**
	 * Drop the pet item, making the pet appear
	 * @param player
	 *         The player dropping the item
	 * @param item
	 *         The pet item being dropped
	 * @return Spawn the pet
	 */
	public static boolean drop(Player player, Item item) {
		Pet.Pets petIds = Pet.Pets.from(item.getId());
		if (petIds != null) {
			if (player.isPetSpawned()) {
				player.getActionSender().sendMessage("You may only have one pet out at a time.");
				return false;
			} else {
				Pet pet = new Pet(player, petIds.getNpc());
				player.setPetSpawned(true);
				player.setPet(petIds.getNpc());
				World.getWorld().register(pet);
				player.getItems().remove(item);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// Pet despawned or owner offline
						if (player.getIndex() < 1 || pet.getIndex() < 1) {
							container.stop();
							return;
						}
						int delta = player.getPosition().distance(pet.getPosition());
						if (delta >= 13 || delta <= -13) {
							// TODO teleport npc to player thats itt
							Position move = new Position(player.getX(), player.getY() -1, player.getZ());
							pet.setLocation(move);
						}
					}

				}, 4);
				return false;
			}
		}
		return true;
	}

	/**
	 * Picks up the pet npc
	 * @param player
	 *         The player picking up the pet
	 * @param pet 
	 *         The pet being picked up
	 * @return despawn the pet, and respawn the pet item in the players inventory.
	 */
	public static boolean pickup(Player player, NPC pet) {
		Pet.Pets pets = Pet.Pets.fromNpc(pet.getId());
		if (pets != null && player.getItems().freeSlots() < 28) {
			if (player.isPetSpawned()) {
				player.playAnimation(Animation.create(827));
				player.getItems().addItemtoInventory(new Item(pets.getItem()));
				World.getWorld().unregister(pet);
				player.setPetSpawned(false);
				player.setPet(-1);
				return true;
			}
		}
		return false;
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
		Pet.Pets pets = Pet.Pets.fromNpc(pet.getId());
		if (pets != null && player.isPetSpawned()) {
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
				player.dialogue().start("OLMLET");
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

}
