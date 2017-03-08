package com.model.game.character.npc.pet;

import java.util.HashMap;
import java.util.Map;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.item.Item;

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

        private final int item;
        private final int npc;

        Pets(int item, int npc) {
            this.item = item;
            this.npc = npc;
        }

        private static Map<Integer, Pets> petItems = new HashMap<Integer, Pets>();
        private static Map<Integer, Pets> petNpcs = new HashMap<Integer, Pets>();

        public static Pets from(int item) {
            return petItems.get(item);
        }

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

        public int getItem() {
            return item;
        }

        public int getNpc() {
            return npc;
        }
        
    }

	//Create pet instance
	public Pet(Player owner, int id) {
		super(id, owner.getPosition(), 0);
		NPCHandler.followPlayer(this, owner.getIndex());
		this.setAbsX(owner.getX());
		this.setAbsY(owner.getY() -1);
		//They don't even appear when im calling this method when we did
		//so they appear but dont follow?
		
		//So we figured out it was setabsx and y but it doesn't work if we apply it here
		//Only works when we apply @ the actual register like in commands for example
		
		/*this.makeX = owner.getX();
		this.makeY = owner.getY() -1;
		this.heightLevel = owner.getHeight();
		this.setOnTile(owner.getX(), owner.getY() -1, heightLevel);
		this.facePlayer(owner.getIndex());*/
		System.out.printf("Spawned npc id %d for player index %d%n", this.npcId, owner.getIndex());
	}
	
	public void drop(Player player, Item item) {
		Pet.Pets petIds = Pet.Pets.from(item.getId());
		if (petIds != null) {
			if (player.getPet() != null) {
				player.message("You may only have one pet out at a time.");
				return;
			} else {
				Pet pet = new Pet(player, petIds.getNpc());
				player.setPetSpawned(true);
				player.setPet(pet);
				World.getWorld().register(pet);
				player.getItems().remove(item);
				return;
			}
		}
	}

	public void pickup(Player player, Npc pet) {
		Pet.Pets pets = Pet.Pets.fromNpc(pet.getId());
		if (pets != null && player.getItems().freeSlots() < 28) {
			if (player.getPet() != null) {
				player.playAnimation(Animation.create(827));
				player.getItems().addItemtoInventory(new Item(pets.getItem()));
				World.getWorld().unregister(player.getPet());
				player.setPetSpawned(false);
				player.setPet(null);
				return;
			}
		}
	}

}
