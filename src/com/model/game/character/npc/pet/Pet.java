package com.model.game.character.npc.pet;

import java.util.HashMap;
import java.util.Map;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * A pet system that uses the Npc class rather than loops
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Pet extends Npc {
	
	public enum Pets {
        //item id, npc id
        ZULRAH_RANGE(12921, 2127),

        ZULRAH_MAGE(12940, 2129),

        ZULRAH_MELEE(12939, 2128),

        DAGANNOTH_SUPREME(12643, 6626),

        DAGANNOTH_PRIME(12644, 6627),

        DAGANNOTH_REX(12645, 6641),

        ARMADYL(12649, 6643),

        BANDOS(12650, 6644),

        SARADOMIN(12651, 6646),

        ZAMORAK(12652, 6647),

        KING_BLACK_DRAGON(12653, 6652),

        CHAOS_ELEMENTAL(11995, 5907),

        KALPHITE_PRINCESS_FORM_1(12654, 6653),

        KRAKEN(12655, 6656),

        SCORPIA(13181, 5547),

        CALLISTO(13178, 497),

        VETION(13179, 5536),

        VENENATIS(13177, 495),

		JAD(13225, 5892),

		SMOKE_DEVIL(12648, 6655),

		BEAVER(13322, 6717),

		HERON(13320, 6715),

		ROCK_GOLEM(13321, 6716),

		HELLPUPPY(13247, 964);

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
		this.ownerId = owner.getIndex(); //same as spawnedBy should be removed in future
		this.faceEntity(owner);
		System.out.printf("Spawned npc id %d for player index %d%n", this.npcId, owner.getIndex());
	}
	
	/**
	 * Drop the pet item, making the pet appear
	 * @param player
	 *         The player dropping the item
	 * @param item
	 *         The pet item being dropped
	 * @return Spawn the pet
	 */
	public boolean drop(Player player, Item item) {
		Pet.Pets petIds = Pet.Pets.from(item.getId());
		if (petIds != null) {
			if (player.getPet() != null && player.isPetSpawned()) {
				player.message("You may only have one pet out at a time.");
				return false;
			} else {
				Pet pet = new Pet(player, petIds.getNpc());
				player.setPetSpawned(true);
				player.setPet(pet);
				World.getWorld().register(pet);
				player.getItems().remove(item);
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
	public boolean pickup(Player player, Npc pet) {
		Pet.Pets pets = Pet.Pets.fromNpc(pet.getId());
		if (pets != null && player.getItems().freeSlots() < 28) {
			if (player.getPet() != null && player.isPetSpawned()) {
				player.playAnimation(Animation.create(827));
				player.getItems().addItemtoInventory(new Item(pets.getItem()));
				World.getWorld().unregister(player.getPet());
				player.setPetSpawned(false);
				player.setPet(null);
				return true;
			}
		}
		return false;
	}

}
