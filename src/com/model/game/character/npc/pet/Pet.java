package com.model.game.character.npc.pet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.RegionalPetCheck;
import com.model.utility.Utility;

/**
 * @author MaxiiPad | Patrick van Elderen | Handling PetData
 * @Date: 02/05/2015 
 */

public class Pet {
	
	
	public enum Pets {
		
		/**
		 * DO NOT ADD IN RANDOM ORDER, THEY'RE ALL IN ALPHABETIC ORDER!!!
		 */
		
		BABY_MOLE(12646, 6635, -1, new int[] { 5779 }, 1500),
		CHAOS_ELEMENTAL_JR(12694, 2055, -1, new int[] { 2054 }, 150),
		CHAOS_FANATIC_Jr(12694, 2055, -1, new int[] { 6619 }, 500),
		CHOMPY_CHICK(13071, 4001, -1, new int[] {-1}, -1),
		CALLISTO_CUB(13178, 497, -1, new int[] {6609}, 1000),
		GOMMANDER_ZILYANA_JR(12651, 6633, -1, new int[] { 2205 }, 2500),
		DAGANNOTH_REX(12645, 6630, -1, new int[] { 2267 }, 2500), 
		DAGANNOTH_PRIME(12644, 6629, -1, new int[] { 2266 }, 2500), 
		DAGANNOTH_SUPREME(12643, 6628, -1, new int[] { 2265 }, 2500), 
		DARK_ENERGY_CORE(12816, 388, -1, new int[] {319}, 2500),
		GENERAL_GRAARDOR_JR(12650, 6632, -1, new int[] { 2215 }, 2500),
		KREE_ARRA_JR(12649, 6631, -1, new int[] { 3162 }, 2500),
		KRIL_TSUTSAROTH(12652, 6634, -1, new int[] { 3129 }, 2500),
		KALPHITE_PRINCESS_GREEN(12647, 6638, 6637, new int[] { 4303 }, 1500),
		KALPHITE_PRINCESS_ORANGE(12654, 6637, 6638, new int[] { 4304 }, 1000),
		KRAKEN(12655, 6640, -1, new int[] {494}, 1500),
		PRINCE_BLACK_DRAGON(12653, 6636, -1, new int[] { 239 }, 1500),
		SCORPIAS_OFFSPRING(13181, 4194, -1, new int[] {6615}, 1000),
		SNAKELING_GREEN(12921, 2130, 2131, new int[] { 2043 }, 2000),
		SNAKELING_RED(12921, 2131, 2132, new int[] { 2043 }, 2000),
		SNAKELING_BLUE(12921, 2132, 2130, new int[] { 2043 }, 2000),
		VENENATIS_SPIDERLING(13177, 495, -1, new int[] {6610}, 1000),
		VETION_PURPLE(13179, 5536, 5537, new int[] {6611}, 1000),
		VETION_ORANGE(13180, 5537, 5536, new int[] {6612}, 1000),
		TZREK_JAD(13225, 5892, -1, new int[] { 3127 }, 100),
		OLMET(20851, 7519, -1, new int[] { -1 }, -1);
		

		/**
		 * The item id associated with the npc
		 */
		private int itemId;
		
		/**
		 * The npc id for the character associated with the pet
		 */
		private int petId;
		
		/**
		 * Defines the stage of the pet
		 */
		private int nextStage;
		
		/**
		 * The ids of the boss npcs that drop this pet
		 */
		private int[] bossIds;

		/**
		 * The absolute maximum probability of receiving an item from the boss
		 */
		private int dropRate;
		

		private Pets(int itemId, int petId, int nextStage, int[] bossIds, int dropRate) {
			this.itemId = itemId;
			this.petId = petId;
			this.nextStage = nextStage;
			this.bossIds = bossIds;
			this.dropRate = dropRate;
		}
		
		/**
		 * Returns the item id used to spawn the pet
		 * 
		 * @return the item id
		 */
		public int getItemId() {
			return itemId;
		}
		
		/**
		 * Returns the npcid associated with the pet
		 * 
		 * @return the pet id
		 */
		public int getPetID() {
			return petId;
		}

		/**
		 * Returns the stage associated with the pet
		 * 
		 * @return the stage of the pet
		 */
		public int getNextStage() {
			return nextStage;
		}
		
		/**
		 * Returns the bosses associated with this pet
		 * 
		 * @return the boss ids
		 */
		public int[] getBossIds() {
			return this.bossIds;
		}

		/**
		 * Returns the drop rate associated with this pet
		 * 
		 * @return the drop rate
		 */
		public int getDropRate() {
			return this.dropRate;
		}

		public static HashMap<Integer, Pets> petdata = new HashMap<Integer, Pets>();
		
		/**
		 * A set of the elements from the Pet emum
		 */
		public static Set<Pets> PETS = EnumSet.allOf(Pets.class);
		
		/**
		 * Gets the Pet object associated with the item id
		 * 
		 * @param itemId
		 *            the item id
		 * @return the pet
		 */
		public static final Pets get(int itemId) {
			for (Pets pet : PETS) {
				if (pet.getItemId() == itemId)
					return pet;
			}
			return null;
		}
		
		static {
			for (Pets pet : Pets.values()) {
				petdata.put(pet.getPetID(), pet);
			}
		}
	}
	
	/**
	 * The npc assigned to the pet
	 */
	private Npc npc;
	
	/**
	 * Returns the npc
	 * 
	 * @return the npc
	 */
	public Npc getNpc() {
		return this.npc;
	}
	
	public static void drop(Player player, int bossId) {
		Pets pet = null;
		for (Pets p : Pets.values()) {
			for (int boss : p.bossIds) {
				if (boss == bossId) {
					pet = p;
					break;
				}
			}
		}
		if (pet == null) {
			return;
		}
		if (pet.getDropRate() == -1) {
			return;
		}
		if (player.getItems().bankContains(pet.getItemId()) || player.getItems().playerHasItem(pet.getItemId())) {
			return;
		}
		if (Utility.getRandom(pet.dropRate) == 0) {
			String name = Npc.getName(bossId);
			player.write(new SendMessagePacket("Shortly after the " + name + " fell, you found a Pet " + name + "."));
			if (player.petId > 1) {
				if (player.getItems().getFreeSlots() > 0) {
					player.getItems().addItem(pet.getItemId(), 1);
					player.write(new SendMessagePacket("You feel something weird sneaking into your backpack."));
				} else {
					player.getItems().sendItemToAnyTab(pet.getItemId(), 1);
					player.write(new SendMessagePacket("You had no space in your inventory there for your pet walked to your bank."));
				}
			} else {
				player.getPets().spawnPet(player, pet.getItemId(), false);
				player.write(new SendMessagePacket("You have a funny feeling like you're being followed."));
			}
			PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + player.getName() + " has just received a Pet " + name.toLowerCase() + " from killing the boss.");
			}
	}
	
	/**
	 * Checking if the NPC i is a Registered Pet NPC
	 * @param i
	 * 			NPCID
	 * @return if it is a Pet NPC
	 */
	public boolean isPetNPC(int i) {
		for (Pets pet : Pets.values()) {
			if (pet.getPetID() == i) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checking if the Item i is a Registered Pet Item
	 * @param i
	 * 			ItemID
	 * @return if it is a Pet Item
	 */
	public boolean isPetItem(int i) {
		for (Pets pet : Pets.values()) {
			if (pet.getItemId() == i) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Picking up the Pet
	 * @param player
	 * 				Player in question
	 * @param item
	 * 				Whether the item should be used/included
	 * @return
	 * 			Used for debugging
	 */
	public boolean pickupPet(Player player, boolean item, Npc pet) {
		
		// ok so what we've done is removed the massive Npc loop and replaced it by directly 
		// getting the Npc instance (aka our pet) by world.npcs.get(index_sent_by_client)
		int reimbursalNPC = 0;
		if (player.getItems().getFreeSlots() < 1 && item) {
			player.write(new SendMessagePacket("You do not have enough inventory space to pick-up your pet."));
			return false;
		}
			
		// i'll leave this here so you can use this but of code in future whereever PI has 
		// massive Npc loops
		// whatever npc is being looped isn't the one we're interacting with right now.. skip to next.
		/*if (player.npcClickIndex != npc.getIndex()) {
			continue;
		}*/
		
	
		// Check the player's index is the same one associated with the npc
		if (player.getIndex() != pet.spawnedBy) {
			player.write(new SendMessagePacket("This is not your pet."));
			//player.write(new SendMessagePacket(String.format("%d vs %d or %d", player.getIndex(), pet.spawnedBy, pet.ownerId)));
			return false;
		}
		
		if ((pet.isPet) && (pet.ownerId == player.getIndex())) {
			reimbursalNPC = pet.npcId;
			pet.absX = pet.absY = 0;
			pet.ownerId = -1;
			npc = null;
			if (!item)
				return true;
		}
		
		if (reimbursalNPC > 0 && item) {
			for (Pets pets : Pets.values()) {
				if (pets.getPetID() == reimbursalNPC) {
					player.playAnimation(Animation.create(827));
					player.getItems().addItem(pets.getItemId(), 1);
					player.petId = -1;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Right click option to start talking to your pet.
	 * @param player
	 * @param npcId
	 * @return
	 */
	public boolean talktoPet(Player player, int npcId, Npc pet) {
		if (player.getIndex() != pet.spawnedBy) {
			player.write(new SendMessagePacket("This is not your pet."));
			return false;
		}
		for (Pets pets : Pets.values()) {
			if (pets != null) {
				switch (npcId) {
				case 7519:
					player.dialogue().start("OLMLET");
					break;
				case 6630:

					break;
					
				case 6629:

					break;
					
				case 6628:

					break;
					
				case 6635:

					break;
						
				case 6636:

					break;
					
				case 6638:

					break;
					
				case 318:
					
					break;
					
				case 2130:
					
					break;
					
				case 2131:

					break;
					
				case 2132:

					break;
					
				case 5892:
					int randomChat = Utility.getRandom(1);
					switch (randomChat) {
					case 0:

						break;
						
					case 1:

						break;
					}
					break;
					
				case 5907:

					break;
					
				case 497:

					break;
					
				case 5547:

					break;
					
				case 495:

					break;
					
				case 5536:

					break;
					
				case 5537:

					break;
					
				case 6632:

					break;
					
				case 6634:

					break;
					
				case 6631:

					break;
					
				case 6633:

					break;
					
				case 6640:

					break;
				}
			} else {
				player.write(new SendMessagePacket("This is not your pet."));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Spawning the Pet
	 * @param player
	 * 				The player who will own the pet
	 * @param item
	 * 				The item dropped
	 * @param login
	 * 				If it's login(To decide item usage)
	 * @return
	 */
	public boolean spawnPet(Player player, int item, boolean login) {
		if(player.petId > 1 && !login) {
			player.write(new SendMessagePacket("You already have a pet roaming with you."));
			return false;
		}
		for (Pets pets : Pets.values()) {
			if ((pets.getItemId() == item) || (login && (pets.getPetID() == player.petId))) {
				Npc pet = NPCHandler.spawnPetNpc(player, pets.getPetID(), player.absX, player.absY - 1, player.heightLevel);
				//System.out.printf("spawned pet id %d owned by %d%n", pet.npcId, pet.spawnedBy);
				player.petId = pets.getPetID();
				player.petNpcIndex = pet.getIndex();
				if (!login){
					player.getItems().deleteItem(pets.getItemId(), 1);
				}
				Server.getTaskScheduler().schedule(new RegionalPetCheck(player, login));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Similar to getPlayer() but for the Pet Object
	 * @param player
	 * 				The player who's pet is being pulled
	 * @return 
	 * 			NPC object
	 */
	public Npc getPet(Player player) {
		Npc npc = null;
		for (Npc i : World.getWorld().getNpcs()) {
			if (i == null)
				continue;
			if (i.ownerId == player.getIndex()) {
				//System.out.println("Pet is not null");
				npc = i;
			}
		}
		return npc;
	}
	
	public void callPet(Player player) {
		if(player.petId < 1){
		   return;
		}	
		if (getPet(player) == null) {
			spawnPet(player, 0, true);
		} else {
			for (Npc i : World.getWorld().getNpcs()) {
				if (i == null)
					continue;
				if ((i.isPet) && (i.ownerId == player.getIndex())) {
					i.absX = i.absY = 0;
					i.ownerId = -1;
					i = null;
					break;
				}
			}
			callPet(player);
		}
	}

	public boolean hasNextStage(Player player, int npc) {
		for (Pets pet : Pets.values()) {
			if (pet.getPetID() == npc && pet.getNextStage() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean handleNextStage(Player player) {
		if (hasNextStage(player, player.petId)) {
			Npc npc = getPet(player);
			int spawnAt_X = 0;
			int spawnAt_Y = 0;
			for (Pets pet : Pets.values()) {
				if (pet.getPetID() == player.petId) {
					spawnAt_X = npc.absX;
					spawnAt_Y = npc.absY;
					player.petId = pet.getNextStage();
					npc.absX = npc.absY = 0;
					npc = null;
					NPCHandler.spawnPetNpc(player, player.petId, spawnAt_X, spawnAt_Y, player.heightLevel);
					return true;
				}
			}
		}
		return false;
	}
}
